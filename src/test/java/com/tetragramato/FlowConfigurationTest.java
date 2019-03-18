package com.tetragramato;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * TODO : under construction
 *
 * @author vivienbrissat
 * Date: 2019-03-18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FlowConfigurationTest {

    @Autowired
    FlowConfiguration flowConfiguration;

    @Before
    public void configure(){
        String path = new ClassPathResource("/testsInbound").getPath();
        flowConfiguration.setDirectoryInput(path);
    }

    @Test
    public void testFileFromInputDir(){
        IntegrationFlow integrationFlow = flowConfiguration.fileFromInputDir();
        assertNotNull(integrationFlow);
    }

}