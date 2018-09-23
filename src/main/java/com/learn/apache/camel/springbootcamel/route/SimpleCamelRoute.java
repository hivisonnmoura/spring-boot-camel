package com.learn.apache.camel.springbootcamel.route;

import com.learn.apache.camel.springbootcamel.domain.Item;
import com.learn.apache.camel.springbootcamel.exception.DataException;
import com.learn.apache.camel.springbootcamel.processor.BuildSQLProcessor;
import com.learn.apache.camel.springbootcamel.processor.SuccessProcessor;
import com.learn.apache.camel.springbootcamel.processor.alert.MailProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Slf4j
@Component
public class SimpleCamelRoute extends RouteBuilder {

    private final Environment environment;
    private final DataSource dataSourceCamel;
    private final BuildSQLProcessor buildSQLProcessor;
    private final SuccessProcessor successProcessor;
    private final MailProcessor mailProcessor;

    public SimpleCamelRoute(Environment environment, @Qualifier("dataSourceCamel") DataSource dataSourceCamel, BuildSQLProcessor buildSQLProcessor, SuccessProcessor successProcessor, MailProcessor mailProcessor) {
        this.environment = environment;
        this.dataSourceCamel = dataSourceCamel;
        this.buildSQLProcessor = buildSQLProcessor;
        this.successProcessor = successProcessor;
        this.mailProcessor = mailProcessor;
    }

    @Override
    public void configure() throws Exception {
        log.info("Start the Camel route at {}", LocalDateTime.now());

        DataFormat bindy = new BindyCsvDataFormat(Item.class);

        errorHandler(deadLetterChannel("log:errorInRoute?level=ERROR&showProperties=true")
                .maximumRedeliveries(3).redeliveryDelay(300).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR));

        onException(PSQLException.class).log(LoggingLevel.ERROR, "PSQLException in the route &{body}")
            .maximumRedeliveries(3).redeliveryDelay(300).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR);

        onException(DataException.class).log(LoggingLevel.ERROR, "Exception in th route ${body}").process(mailProcessor)
                .maximumRedeliveries(3).redeliveryDelay(300).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR);

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
                .log(LoggingLevel.INFO, "Mock environment flow and the body is ${body}")
                .end()
                .to("{{toRoute1}}")
                .unmarshal(bindy)
                .log("The unmarshaller object is ${body}")
                .split(body())
                .log("Record is ${body}")
                .process(buildSQLProcessor)
                .to("{{toRoute2}}")
                .end()
                .process(successProcessor)
                .to("{{toRoute3}}");

        log.info("Ending the Camel route at {}. Time execute", LocalDateTime.now());
    }
}
