package com.tetragramato;

import com.tetragramato.filter.FileExtensionsFilter;
import com.tetragramato.message.transformer.TransformerToFileOutputMessage;
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
 * @author vivienbrissat
 * Date: 2019-01-10
 */
@Configuration
public class FlowConfiguration {

    private String directoryInput;

    private String directoryOutput;

    private String topicExchangeName;

    private String routingKey;

    private TransformerToFileOutputMessage transformerToFileOutputMessage;

    private FileFilterConfiguration fileFilterConfiguration;

    public FlowConfiguration(@Value("${ged.directory.input}") final String directoryInput,
                             @Value("${ged.directory.output}") final String directoryOutput,
                             @Value("${ged.amqp.exchange}") final String topicExchangeName,
                             @Value("${ged.amqp.routingKey}") final String routingKey,
                             final TransformerToFileOutputMessage transformerToFileOutputMessage,
                             final FileFilterConfiguration fileFilterConfiguration) {
        this.directoryInput = directoryInput;
        this.directoryOutput = directoryOutput;
        this.topicExchangeName = topicExchangeName;
        this.routingKey = routingKey;
        this.transformerToFileOutputMessage = transformerToFileOutputMessage;
        this.fileFilterConfiguration = fileFilterConfiguration;
    }

    @Bean
    public MessageChannel publishSubscribeInputFile() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public MessageChannel publishSubscribeOutputFile() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public FileListFilter<File> extensionsAllowedFilter() {
        return new FileExtensionsFilter(fileFilterConfiguration.getExtensions());
    }

    @Bean
    public IntegrationFlow fileFromInputDir() {
        return IntegrationFlows.from(Files.inboundAdapter(new File(directoryInput))
                                          .ignoreHidden(true)
                                          .preventDuplicates(true)
                                          .useWatchService(true)
                                          .filter(extensionsAllowedFilter()), e -> e.poller(Pollers.fixedDelay(1000)))
                               .log(LoggingHandler.Level.INFO, "TookInputFile")
                               .channel(publishSubscribeInputFile())
                               .get();
    }

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
