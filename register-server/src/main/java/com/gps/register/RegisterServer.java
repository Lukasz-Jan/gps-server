package com.gps.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
public class RegisterServer {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServer.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext appCtx = SpringApplication.run(RegisterServer.class, args);
        appCtx.registerShutdownHook();
        logger.info("Register server started");
    }
}
