package com.gps.register.forwarder.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QueueService {

    private static final int NO_OF_WORKERS = 1;
    private final Thread[] workers = new Thread[NO_OF_WORKERS];
    private final Forwarder[] forwarders = new Forwarder[NO_OF_WORKERS];
    private static final int JOIN_TIMEOUT_MS = 20_000;

    @Autowired
    public QueueService(JmsTemplate jms, @Value("${registerQueue}") String registerQueueName) {

        Map<String, Thread> registry = new HashMap<>();
        String parentMarker = UUID.randomUUID().toString();

        for (int i = 0; i < NO_OF_WORKERS; i++) {

            forwarders[i] = new Forwarder(jms, registerQueueName, parentMarker);
            workers[i] = new Thread(forwarders[i], Integer.toString(i));
            registry.put(forwarders[i].getEndMarker(), workers[i]);
        }

        Map<String, Thread> immRegistry = Map.copyOf(registry);
        for (int i = 0; i < NO_OF_WORKERS; i++) {
            forwarders[i].setRegistry(immRegistry);
        }
    }

    @PostConstruct
    public void start() {
        for (int i = 0; i < NO_OF_WORKERS; i++) {
            workers[i].start();
        }
    }

    @PreDestroy
    public void stop() {

        for (int i = 0; i < NO_OF_WORKERS; i++) {
            forwarders[i].end();
        }

        for (int i = 0; i < NO_OF_WORKERS; i++) {
            try {
                workers[i].join(JOIN_TIMEOUT_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (int i = 0; i < NO_OF_WORKERS; i++) {
            if (workers[i].isAlive()) {
                workers[i].interrupt();
                try {
                    workers[i].join(JOIN_TIMEOUT_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
