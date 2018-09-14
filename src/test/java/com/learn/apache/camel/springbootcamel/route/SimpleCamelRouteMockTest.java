package com.learn.apache.camel.springbootcamel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ActiveProfiles("mock")
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SimpleCamelRouteMockTest {


    @Autowired
    CamelContext context;

    @Autowired
    Environment environment;

    @EndpointInject(uri = "{{toRoute1}}")
    private MockEndpoint route1;

    @EndpointInject(uri = "{{toRoute3}}")
    private MockEndpoint route3;

    @Autowired
    protected CamelContext createCamelContext() {
        return context;
    }


    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    public void testMoveFileMock() throws IOException, InterruptedException {
        String message = new String(Files.readAllBytes(Paths.get("dataFile/fileAdd.txt")));

        route1.expectedMessageCount(1);
        route1.expectedBodiesReceived(message);

        producerTemplate.sendBodyAndHeader("{{startRoute}}", message, "env", environment.getActiveProfiles());
        route1.assertIsSatisfied();

    }


    @Test
    public void testMoveFileMockandDB() throws InterruptedException, IOException {

        String message = new String(Files.readAllBytes(Paths.get("dataFile/fileAdd.txt")));

        String outMessage = "Data Updated Successfully !";

        route1.expectedMessageCount(1);
        route1.expectedBodiesReceived(message);

        route3.expectedMessageCount(1);
        route3.expectedBodiesReceived(outMessage);


        producerTemplate.sendBodyAndHeader("{{startRoute}}", message, "env", environment.getActiveProfiles());
        route3.assertIsSatisfied();

    }
}