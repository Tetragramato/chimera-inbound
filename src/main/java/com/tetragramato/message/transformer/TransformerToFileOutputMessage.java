package com.tetragramato.message.transformer;

import com.tetragramato.message.FileOutputMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.support.MutableMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * @author vivienbrissat
 * Date: 2019-01-09
 */
@Component
public class TransformerToFileOutputMessage {

    private String directoryOutput;

    public TransformerToFileOutputMessage(@Value("${ged.directory.output}") final String directoryOutput) {
        this.directoryOutput = directoryOutput;
    }

    public Message<FileOutputMessage> fromMessage(Message<?> message) {

        FileOutputMessage fileOutputMessage = FileOutputMessage.builder()
                                                               .dateFileInbound(ZonedDateTime.now())
                                                               .fileName(String.valueOf(message.getHeaders().get(FileHeaders.FILENAME)))
                                                               .fileOutput(directoryOutput + "/" + message.getHeaders().get(FileHeaders.FILENAME))
                                                               .build();

        return MutableMessageBuilder.withPayload(fileOutputMessage)
                                    .build();
    }

}
