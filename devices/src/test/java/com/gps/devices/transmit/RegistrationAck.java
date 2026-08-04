package com.gps.devices.transmit;

import com.gps.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class RegistrationAck extends Ack {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationAck.class);

    private static final AtomicInteger timeoutsQuantity = new AtomicInteger(0);
    private static final AtomicInteger failuresQuantity = new AtomicInteger(0);


    public RegistrationAck(
            CompletableFuture<HttpResponse<String>> currentResponse
            , StringSubscriber flowSubscriber
    ) {
        super(currentResponse, flowSubscriber);
    }

    @Override
    public void run() {

        HttpResponse<String> response;
        try {
            response = this.currentResponse.get(Constants.REGISTER_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("InterruptedException \n{}", e.getMessage());
            failuresQuantity.incrementAndGet();
            Thread.currentThread().interrupt();
            return;
        } catch (ExecutionException e) {

            if (e.getCause() instanceof HttpTimeoutException) {
                logger.warn("timeout for request: \n{}", flowSubscriber.getBody());
                timeoutsQuantity.incrementAndGet();
            } else {
                logger.warn("registration sending exception ", e);
            }
            failuresQuantity.incrementAndGet();
            return;
        } catch (TimeoutException e) {
            logger.warn("registration sending exception ", e);
            failuresQuantity.incrementAndGet();
            timeoutsQuantity.incrementAndGet();
            return;
        }

        if (response.statusCode() != HttpStatus.OK.value()) {
            logger.warn("registration resp status: {}\n {}", response.statusCode(), flowSubscriber.getBody());
            failuresQuantity.incrementAndGet();
        } else {
            logger.debug("registration status: {}", response.statusCode());
        }
    }

    public static long getTimeoutsQuantity() {
        return timeoutsQuantity.longValue();
    }

    public static long getFailuresQuantity() {
        return failuresQuantity.longValue();
    }

    public static void resetTimeoutsQuantity() {
        timeoutsQuantity.set(0);
    }

    public static void resetFailuresQuantity() {
        failuresQuantity.set(0);
    }
}
