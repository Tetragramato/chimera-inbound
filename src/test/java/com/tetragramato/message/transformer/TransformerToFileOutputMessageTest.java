package com.tetragramato.message.transformer;

import com.tetragramato.message.FileOutputMessage;
import org.junit.Test;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author vivienbrissat
 * Date: 2019-03-18
 */
public class TransformerToFileOutputMessageTest {

    @Test
    public void testTransformerToFileOutputMessage(){

        //Given
        MessageHeaders messageHeaders = new MessageHeaders(Collections.singletonMap(FileHeaders.FILENAME, "toto.txt"));
        Message<String> message = MessageBuilder.createMessage("try",messageHeaders);

        FileOutputMessage expectedMessage = FileOutputMessage.builder()
                         .fileName("toto.txt")
                         .fileOutput("/test/toto.txt")
                         .build();

        TransformerToFileOutputMessage transformerToFileOutputMessage = new TransformerToFileOutputMessage("/test");

        //When
        Message<FileOutputMessage> transformed = transformerToFileOutputMessage.fromMessage(message);

        //Then
        assertThat(transformed.getPayload()).isEqualToIgnoringGivenFields(expectedMessage,"dateFileInbound");
        assertThat(transformed.getHeaders()).isNotNull();
        assertThat(transformed.getHeaders().entrySet().size()).isEqualTo(2);
    }

}