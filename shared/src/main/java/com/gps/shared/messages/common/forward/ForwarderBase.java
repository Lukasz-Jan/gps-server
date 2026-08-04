package com.gps.shared.messages.common.forward;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public abstract class ForwarderBase implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ForwarderBase.class);
    protected static final int RE_OFFER_TIMEOUT_S = 5;
    protected static final int BACKOFF_MS = 5000;
    protected static final int ANTI_LIVELOCK_SLEEP_MS = 200;
    protected final BlockingDeque<String> requestQueue;
    protected final JmsTemplate jms;
    protected final String jmsQueueName;
    protected Thread currentThread;
    protected volatile boolean started;
    private final String parentMarker;
    protected Map<String, Thread> registry = Map.of();


    protected ForwarderBase(
            BlockingDeque<String> requestQueue, JmsTemplate jms, String jmsQueueName,
            String parentMarker
    ) {
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

    protected boolean isEndMessageForOtherThread(String msg) {
        return msg.startsWith(parentMarker);
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
                logger.debug("To forward: \n{}", msg);

                if (isCurrentThreadEndMsg(msg)) {
                    logger.debug("ending que...");
                    shallBreak = true;
                    started = false;
                }
                else if (isEndMessageForOtherThread(msg)) {
                    shallBreak = reofferMarkedMsgToRequestQueue(msg);
                }
                else {
                    shallBreak = forwardToJmsQueue(msg);
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted exception for queue.take", e);
                started = false;
                Thread.currentThread().interrupt();
                shallBreak = true;
            }
            if (shallBreak) break;
        }
    }

    protected boolean forwardToJmsQueue(String msg) {

        try {
            jms.convertAndSend(jmsQueueName, msg);
            logger.debug("forwarded: \n{}", msg);
        } catch (JmsException e) {

            logger.error("error while sending to jms ", e);

            try {
                boolean offer = requestQueue.offer(msg, RE_OFFER_TIMEOUT_S, TimeUnit.SECONDS);
                if (!offer) {
                    logger.error("Lost message: \n {}", msg);
                }
                Thread.sleep(BACKOFF_MS);
            } catch (InterruptedException interruptedException) {
                logger.error("re-offer to queue error ", interruptedException);
                started = false;
                Thread.currentThread().interrupt();
                return true;
            }
        }
        return false;
    }

    protected abstract String getEndMarker();

    protected boolean reofferMarkedMsgToRequestQueue(String msg) {

        boolean offer = false;
        try {

            Thread foreignThread = registry.get(msg);

            if (Objects.nonNull(foreignThread) && foreignThread.isAlive()) {

                offer = requestQueue.offerFirst(msg, RE_OFFER_TIMEOUT_S, TimeUnit.SECONDS);
                if (!offer) {
                    foreignThread.interrupt();
                }
                else {
                    antiLivelockSleep();
                }
            } else {
                logger.debug("Dropping marker for dead/unknown worker {}", msg);
            }
            return false;
        } catch (InterruptedException e) {
            started = false;
            Thread.currentThread().interrupt();
            return true;
        }
    }

    private void antiLivelockSleep() throws InterruptedException {
        Thread.sleep(ANTI_LIVELOCK_SLEEP_MS);
    }

    public void setRegistry(Map<String, Thread> registry) {
        this.registry = registry;
    }
}
