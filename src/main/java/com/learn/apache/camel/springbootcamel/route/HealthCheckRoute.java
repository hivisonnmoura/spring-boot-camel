package com.learn.apache.camel.springbootcamel.route;

import com.learn.apache.camel.springbootcamel.processor.HealthCheckProcessor;
import com.learn.apache.camel.springbootcamel.processor.alert.MailProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckRoute extends RouteBuilder {

    private final HealthCheckProcessor healthCheckProcessor;
    private final MailProcessor mailCheckProcessor;


    public HealthCheckRoute(HealthCheckProcessor healthCheckProcessor, MailProcessor mailCheckProcessor) {
        this.healthCheckProcessor = healthCheckProcessor;
        this.mailCheckProcessor = mailCheckProcessor;
    }

    @Override
    public void configure() throws Exception {

        from("{{healthRoute}}").routeId("healthRoute")
                .pollEnrich("http://localhost:8088/actuator/health")
                .process(healthCheckProcessor)
                .choice()
                    .when(header("error").isEqualTo(true))
                    .process(mailCheckProcessor)
                    .end();
    }
}
