package com.tetragramato.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author vivienbrissat
 * Date: 2019-01-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileOutputMessage {

    private String        fileOutput;
    private String        fileName;
    private ZonedDateTime dateFileInbound;
}
