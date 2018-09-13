package com.learn.apache.camel.springbootcamel.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SimpleCamelRoute extends RouteBuilder {

    private final Environment environment;

    public SimpleCamelRoute(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void configure() throws Exception {
        log.info("Start the Camel route at {}", LocalDateTime.now());

        from("{{startRoute}}")
                .log(LoggingLevel.INFO, "Timer Invoked " + environment.getProperty("message"))

                .choice()
                    .when(header("env").isNotEqualTo("mock"))
                        /*
                         *  [.pollEnrich]I do not need any exchange that has been send from the 'from' method,
                         *  I wanna construct my own exchange start from this particular note.
                         *  Meaning that the route will read my file from input and create a Exchange
                         *  object to be used in this route.
                         */
                        .pollEnrich("{{fromRoute}}")
                        .log(LoggingLevel.INFO, "Exchange body: ${body}")
                    .otherwise()
                        .log(LoggingLevel.INFO,"Mock environment flow and the body is ${body}")
                .end()
                .to("{{toRoute1}}");

        log.info("Ending the Camel route at {}. Time execute", LocalDateTime.now());
    }
}
