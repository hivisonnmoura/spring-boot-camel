package com.learn.apache.camel.springbootcamel.processor.alert;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@PropertySource("classpath:application.yml")
public class MailProcessor implements Processor {

    private final JavaMailSender mailSender;
    private final Environment environment;


    public MailProcessor(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;

    }

    @Override
    public void process(Exchange exchange) throws Exception {

        Exception e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        log.info("Exception Caught in mail processor : {}", e.getMessage());

        String messageBody  = StringUtils.join("Exception happened in the camel route : ", e.getMessage());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(environment.getProperty("mailFrom"));
        simpleMailMessage.setTo(environment.getProperty("mailTo"));
        simpleMailMessage.setSubject("Exception in camel route");
        simpleMailMessage.setText(messageBody);

        mailSender.send(simpleMailMessage);
    }

}
