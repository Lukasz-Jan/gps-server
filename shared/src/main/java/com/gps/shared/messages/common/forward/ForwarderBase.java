package com.gps.shared.messages.common.forward;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public abstract class ForwarderBase {

    private static final Logger logger = LoggerFactory.getLogger(ForwarderBase.class);
    protected static final int REOFER_TIMEOUT_S = 5;
    protected static final int BACKOFF_MS = 400;
    protected final BlockingDeque<String> requestQueue;
    protected final JmsTemplate jms;
    protected final String jmsQueueName;
    protected Thread currentThread;
    protected volatile boolean started;
    private final String parentMarker;
    protected Map<String, Thread> registry = Map.of();


    protected ForwarderBase(BlockingDeque<String> requestQueue, JmsTemplate jms, String jmsQueueName,
            String parentMarker) {
        this.requestQueue = requestQueue;
        this.jms = jms;
        this.jmsQueueName = jmsQueueName;
        this.parentMarker = parentMarker;
    }

    protected void interruptCurrentThread() {
        synchronized (this) {
            if (Objects.nonNull(currentThread)) {
                currentThread.interrupt();
            }
        }
    }

    protected boolean isCurrentThreadEndMsg(String endMsg) {
        return getEndMarker().equals(endMsg);
    }

    protected boolean isEndMessage(String msg) {
        return msg.startsWith(parentMarker);
    }

    protected boolean forwardToJmsQueue(String msg) {

        try {
            jms.convertAndSend(jmsQueueName, msg);
            logger.debug("forwarded: \n{}", msg);
        } catch (JmsException e) {

            logger.error("error while sending to jms ", e);

            try {
                boolean offer = requestQueue.offer(msg, REOFER_TIMEOUT_S, TimeUnit.SECONDS);
                if (!offer) {
                    logger.error("Lost message: \n {}", msg);
                }
                Thread.sleep(BACKOFF_MS);
            } catch (InterruptedException interruptedException) {
                logger.error("re-ofer to queue error ", interruptedException);
                started = false;
                Thread.currentThread().interrupt();
                return true;
            }
        }
        return false;
    }

    protected abstract String getEndMarker();

    protected void reofferMarkedMsgToRequestQueue(String msg) {
        try {
            boolean offer = requestQueue.offerFirst(msg);
            if (!offer) {
                logger.error("Lost marked message: \n {}", msg);

                Thread thread = registry.get(msg);
                if (Objects.nonNull(thread)) {
                    thread.interrupt();
                } else {
                    logger.error("marker {} reoffer - thread null", msg);
                }
            }
        } catch (Exception exc) {
            logger.error("control msg to request queue error ", exc);
            Thread.currentThread().interrupt();
        }
    }

    public void setRegistry(Map<String, Thread> registry) {
        this.registry = registry;
    }
}
