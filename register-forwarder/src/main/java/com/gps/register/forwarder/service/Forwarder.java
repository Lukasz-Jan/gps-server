package com.gps.register.forwarder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.BlockingQueue;

public class Forwarder implements Runnable, StartStop {

    private static final Logger logger = LoggerFactory.getLogger(Forwarder.class);
    private final BlockingQueue<String> requestQue;
    private final JmsTemplate jms;
    private final String registerQueueName;

    private boolean started;

    public Forwarder(JmsTemplate jms, String registerQueueName) {
        requestQue = QueueProvider.getRequestQueue();
        this.jms = jms;
        this.registerQueueName = registerQueueName;
        started = true;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void end() {
        started = false;
        requestQue.clear();
    }

    @Override
    public void run() {

        String msg = "";
        try {
            while(started) {
                msg = requestQue.take();
                jms.convertAndSend(registerQueueName, msg);
                logger.debug("To forward: \n{}", msg);
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException");
        } catch (JmsException e) {
            logger.error("JMS EXCEPTION");
        }
    }
}
