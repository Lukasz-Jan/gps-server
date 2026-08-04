package com.gps.position.forwarder.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private final int noOfThreads = 1;
    private final Thread[] threads = new Thread[noOfThreads];
    private final Forwarder[] trs = new Forwarder[noOfThreads];

    @Autowired
    public QueueService(JmsTemplate jms,
                        @Value("${positionQueue}") String postionQueueName) {
        for (int i = 0; i < noOfThreads; i++) {
            trs[i] = new Forwarder(jms, postionQueueName);
            threads[i] = new Thread(trs[i], Integer.valueOf(i).toString());
        }
    }

    @PostConstruct
    public void start() {
        for (int i = 0; i < noOfThreads; i++) {
            threads[i].start();
        }
    }

    @PreDestroy
    public void stop() {
        for (int i = 0; i < noOfThreads; i++) {
            trs[i].end();
            try {
                threads[i].join(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
