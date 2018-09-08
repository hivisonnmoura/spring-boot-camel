package com.learn.apache.camel.springbootcamel.route;


import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

@ActiveProfiles("dev")
@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //To clean the cache and the context after each test
@SpringBootTest
public class SimpleCamelRouteTest {

    @Autowired
    ProducerTemplate producerTemplate;


    @BeforeClass
    public static void startCleanUp() throws IOException {
        FileUtils.cleanDirectory(new File("data/input"));
        FileUtils.deleteDirectory(new File("data/output"));
    }

    @Test
    public void testMoveFile() throws IOException, InterruptedException {

        String message = new String(Files.readAllBytes(Paths.get("dataFile/fileAdd.txt")));
        String fileName = "fileTest.txt";


        producerTemplate.sendBodyAndHeader("{{fromRoute}}",
                                                        message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());
    }
}
