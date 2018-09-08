package com.learn.apache.camel.springbootcamel.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SimpleCamelRoute extends RouteBuilder {

    private final Environment environment;

    public SimpleCamelRoute(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void configure() throws Exception {

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
    }
}
