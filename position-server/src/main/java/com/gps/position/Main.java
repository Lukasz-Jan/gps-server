package com.gps.position;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages={"com.gps.position", "com.gps.shared"})
public class Main {

    public static void main(String[] args) {

        ConfigurableApplicationContext appCtx = SpringApplication.run(Main.class, args);
        appCtx.registerShutdownHook();
    }
}
