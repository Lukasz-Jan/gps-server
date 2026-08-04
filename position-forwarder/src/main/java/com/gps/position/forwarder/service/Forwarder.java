package com.gps.position.forwarder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.BlockingQueue;

public class Forwarder implements Runnable, StartStop {

    private static final Logger logger = LoggerFactory.getLogger(Forwarder.class);
    private final BlockingQueue<String> requestQueue;
    private final JmsTemplate jms;
    private final String postionQueueName;

    private boolean started;

    public Forwarder(JmsTemplate jms, String postionQueueName) {
        requestQueue = QueueProvider.getPositionsQueue();
        this.jms = jms;
        this.postionQueueName = postionQueueName;
        started = true;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void end() {
        started = false;
        requestQueue.clear();
    }

    @Override
    public void run() {

        String msg = "";
        try {

            while (started) {
                msg = requestQueue.take();
                jms.convertAndSend(postionQueueName, msg);
                logger.debug("To forward: \n{}", msg);
            }
        } catch (InterruptedException e) {
            logger.info("InterruptedException");
        } catch (JmsException e) {
            logger.error("JMS EXCEPTION");
        }
    }
}
