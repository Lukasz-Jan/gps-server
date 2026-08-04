package com.gps.devices.transmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class RegistrationAck implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationAck.class);
    private final CompletableFuture<HttpResponse<String>> currentResponse;
    private final StringSubscriber flowSubscriber;

    public static final AtomicInteger timeoutsQuantity = new AtomicInteger(0);

    public RegistrationAck(CompletableFuture<HttpResponse<String>> currentResponse
            , StringSubscriber flowSubscriber) {
        this.currentResponse = currentResponse;
        this.flowSubscriber = flowSubscriber;
    }

    @Override
    public Boolean call() {

        HttpResponse<String> response = null;
        try {
            response = this.currentResponse.get();
        } catch (InterruptedException e) {
            logger.info("InterruptedException \n{}", e.getMessage());
            return false;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpTimeoutException) {
                logger.info("timeout for request: \n{}", flowSubscriber.getBody());
                timeoutsQuantity.getAndAdd(1);
            }
            return false;
        }

        if (response.statusCode() != 200) {
            logger.info("resp status: {}\n {}", response.statusCode(), response.request());
        } else {
            logger.debug("resp status: {}", response.statusCode());
        }

        return true;
    }

    public static long getTimeoutsQuantity() {
        return timeoutsQuantity.longValue();
    }
}
