package com.tetragramato;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration from YML for managed documents extensions.
 *
 * @author vivienbrissat
 * Date: 2019-01-11
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix="chimera.document")
public class FileFilterConfiguration {
    private List<String> extensions;
}
