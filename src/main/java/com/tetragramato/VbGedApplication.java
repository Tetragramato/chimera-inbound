package com.tetragramato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author vivienbrissat
 * Date: 2019-01-06
 */
@SpringBootApplication(scanBasePackages = "com.tetragramato")
@EnableConfigurationProperties
public class VbGedApplication {

    public static void main(String[] args) {
        SpringApplication.run(VbGedApplication.class, args);
    }
}
