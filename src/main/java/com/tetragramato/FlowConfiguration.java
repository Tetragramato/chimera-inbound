package com.tetragramato;

import com.tetragramato.filter.FileExtensionsFilter;
import com.tetragramato.message.transformer.TransformerToFileOutputMessage;
import lombok.Data;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

import java.io.File;

/**
 * Main Spring Integration Flow configuration.
 *
 * For this, we have 3 flows :
 *  fileFromInputDir -> fileToOutputDir -> fileToAMQP
 *
 * @author vivienbrissat
 * Date: 2019-01-10
 */
@Data
@Configuration
public class FlowConfiguration {

    private String directoryInput;

    private String directoryOutput;

    private String topicExchangeName;

    private String routingKey;

    private TransformerToFileOutputMessage transformerToFileOutputMessage;

    private FileFilterConfiguration fileFilterConfiguration;

    public FlowConfiguration(@Value("${chimera.directory.input}") final String directoryInput,
                             @Value("${chimera.directory.output}") final String directoryOutput,
                             @Value("${chimera.amqp.exchange}") final String topicExchangeName,
                             @Value("${chimera.amqp.routingKey}") final String routingKey,
                             final TransformerToFileOutputMessage transformerToFileOutputMessage,
                             final FileFilterConfiguration fileFilterConfiguration) {
        this.directoryInput = directoryInput;
        this.directoryOutput = directoryOutput;
        this.topicExchangeName = topicExchangeName;
        this.routingKey = routingKey;
        this.transformerToFileOutputMessage = transformerToFileOutputMessage;
        this.fileFilterConfiguration = fileFilterConfiguration;
    }

    /**
     * Channel for the fileFromInputDir flow.
     *
     * @return MessageChannel
     */
    @Bean
    public MessageChannel publishSubscribeInputFile() {
        return MessageChannels.publishSubscribe().get();
    }

    /**
     * Channel for the fileToOutputDir flow.
     *
     * @return MessageChannel
     */
    @Bean
    public MessageChannel publishSubscribeOutputFile() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public FileListFilter<File> extensionsAllowedFilter() {
        return new FileExtensionsFilter(fileFilterConfiguration.getExtensions());
    }

    /**
     * Flow for scanning files in the input Directory, and publish a message with documents infos in his MessageChannel.
     * FIXME : watch-service from java NIO dooes not work with Docker Volumes.
     *
     * @return IntegrationFlow
     */
    @Bean
    public IntegrationFlow fileFromInputDir() {
        return IntegrationFlows.from(Files.inboundAdapter(new File(directoryInput))
                                          .ignoreHidden(true)
                                          .preventDuplicates(true)
                                          //.useWatchService(true)
                                          //.watchEvents(FileReadingMessageSource.WatchEventType.CREATE)
                                          .filter(extensionsAllowedFilter()), e -> e.poller(Pollers.fixedDelay(1000)))
                               .log(LoggingHandler.Level.INFO, "TookInputFile")
                               .channel(publishSubscribeInputFile())
                               .get();
    }

    /**
     * Flow for move documents in Output Directory, and publish a message with documents infos in his MessageChannel.
     *
     * @return IntegrationFlow
     */
    @Bean
    public IntegrationFlow fileToOutputDir() {
        return IntegrationFlows.from(publishSubscribeInputFile())
                               .log(LoggingHandler.Level.INFO, "CreatedOutputFile")
                               .handle(Files.outboundGateway(new File(directoryOutput))
                                            .fileExistsMode(FileExistsMode.IGNORE)
                                            .deleteSourceFiles(true)
                                            .autoCreateDirectory(true))
                               .channel(publishSubscribeOutputFile())
                               .get();
    }

    /**
     * Flow for publishing the message from the Output messageChannel to AMQP.
     *
     * @return IntegrationFlow
     */
    @Bean
    public IntegrationFlow fileToAMQP(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(publishSubscribeOutputFile())
                               .log(LoggingHandler.Level.INFO, "SendedToAMQP")
                               .transform(transformerToFileOutputMessage)
                               .handle(Amqp.outboundAdapter(amqpTemplate)
                                           .exchangeName(topicExchangeName)
                                           .routingKey(routingKey))
                               .get();
    }
}
