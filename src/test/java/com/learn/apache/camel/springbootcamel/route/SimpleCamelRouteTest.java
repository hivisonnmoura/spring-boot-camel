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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        FileUtils.deleteDirectory(new File("data/input/error"));
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

    @Test
    public void testMoveFile_ADD() throws IOException, InterruptedException {
        String message = new String(Files.readAllBytes(Paths.get("dataFile/fileAdd.txt")));

        String fileName = "fileAddTest.txt";
        producerTemplate.sendBodyAndHeader("{{fromRoute}}",
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());

        String outputMessage = "Data Updated Successfully !";
        String output = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        assertEquals(outputMessage ,output);
    }

    @Test
    public void testMoveFile_UPDATE() throws IOException, InterruptedException {
        String message = new String(Files.readAllBytes(Paths.get("dataFile/fileUpdate.txt")));

        String fileName = "fileUpdateTest.txt";
        producerTemplate.sendBodyAndHeader("{{fromRoute}}",
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());

        String outputMessage = "Data Updated Successfully !";
        String output = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        assertEquals(outputMessage ,output);
    }

    @Test
    public void testMoveFile_DELETE() throws IOException, InterruptedException {
        String message = new String(Files.readAllBytes(Paths.get("dataFile/fileDelete.txt")));

        String fileName = "fileDeleteTest.txt";
        producerTemplate.sendBodyAndHeader("{{fromRoute}}",
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());

        String outputMessage = "Data Updated Successfully !";
        String output = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        assertEquals(outputMessage ,output);
    }


    @Test
    public void testMoveFile_ADD_Exception() throws IOException, InterruptedException {
        String message = new String(Files.readAllBytes(Paths.get("dataFile/fileAddException.txt")));

        String fileName = "fileAddTest.txt";
        producerTemplate.sendBodyAndHeader("{{fromRoute}}",
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());
        File errorDirectory = new File("data/input/error");
        assertFalse(errorDirectory.exists());
    }

}
