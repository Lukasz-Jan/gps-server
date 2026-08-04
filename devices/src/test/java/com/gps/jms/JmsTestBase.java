package com.gps.jms;

import org.springframework.jms.core.JmsTemplate;

import static org.junit.jupiter.api.Assertions.fail;

public class JmsTestBase {

    protected JmsTemplate jms;

    protected void convertAndSend(String queName, String json) {
        try {
            jms.convertAndSend(queName, json);
        } catch (Exception e) {
            fail("jms send fail,  no connection to gps server");
        }
    }
}
