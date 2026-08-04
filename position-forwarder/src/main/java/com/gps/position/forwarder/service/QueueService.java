package com.gps.position.forwarder.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private static final int NO_OF_THREADS = 1;
    private final Thread[] threads = new Thread[NO_OF_THREADS];
    private final Forwarder[] forwarderThread = new Forwarder[NO_OF_THREADS];

    @Autowired
    public QueueService(JmsTemplate jms,
                        @Value("${positionQueue}") String postionQueueName) {
        for (int i = 0; i < NO_OF_THREADS; i++) {
            forwarderThread[i] = new Forwarder(jms, postionQueueName);
            threads[i] = new Thread(forwarderThread[i], Integer.toString(i));
        }
    }

    @PostConstruct
    public void start() {
        for (int i = 0; i < NO_OF_THREADS; i++) {
            threads[i].start();
        }
    }

    @PreDestroy
    public void stop() {
        for (int i = 0; i < NO_OF_THREADS; i++) {
            forwarderThread[i].end();
            try {
                threads[i].join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
