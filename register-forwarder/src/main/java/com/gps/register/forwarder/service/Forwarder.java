package com.gps.register.forwarder.service;

import com.gps.shared.messages.common.forward.ForwarderBase;
import com.gps.shared.messages.common.forward.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Forwarder extends ForwarderBase implements Stop {

    private static final Logger logger = LoggerFactory.getLogger(Forwarder.class);
    private final String endMarker;

    public Forwarder(JmsTemplate jms, String jmsQueueName, String parentMarker) {

        super(QueueProvider.getQueue(), jms, jmsQueueName, parentMarker);
        started = true;
        endMarker = parentMarker + UUID.randomUUID();
    }

    @Override
    public void end() {
        markEnd();
    }

    protected void markEnd() {
        try {
            boolean marked = requestQueue.offer(endMarker, RE_OFFER_TIMEOUT_S, TimeUnit.SECONDS);
            if (!marked) {
                logger.error("Marking end failed");
                interruptCurrentThread();
            }
        } catch (InterruptedException e) {
            logger.error("Trying to stop - queue full:  ", e);
            interruptCurrentThread();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected  String getEndMarker() {
        return endMarker;
    }
}
