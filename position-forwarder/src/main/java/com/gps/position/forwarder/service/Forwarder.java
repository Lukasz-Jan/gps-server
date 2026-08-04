package com.gps.position.forwarder.service;


import com.gps.shared.messages.common.forward.ForwarderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Forwarder extends ForwarderBase implements Runnable, Stop {

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

    @Override
    public void run() {

        synchronized (this) {
            currentThread = Thread.currentThread();
        }
        String msg;
        boolean shallBreak = false;

        while (started) {
            try {
                msg = requestQueue.take();

                if (isCurrentThreadEndMsg(msg)) {
                    logger.debug("ending que...");
                    shallBreak = true;
                    started = false;
                }
                else if (isEndMessage(msg)) {
                    reofferMarkedMsgToRequestQueue(msg);
                }
                else {
                    shallBreak = forwardToJmsQueue(msg);
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted exception for queue.take", e);
                started = false;
                shallBreak = true;
                Thread.currentThread().interrupt();
            }
            if (shallBreak) break;
        }
    }

    protected void markEnd() {
        try {
            boolean marked = requestQueue.offerFirst(endMarker, REOFER_TIMEOUT_S,
                    TimeUnit.SECONDS);
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
