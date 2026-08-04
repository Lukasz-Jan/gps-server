package com.gps.devices.transmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RegistrationAck extends Ack   {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationAck.class);

    public RegistrationAck(CompletableFuture<HttpResponse<String>> currentResponse
            , StringSubscriber flowSubscriber) {
        super(currentResponse,flowSubscriber);
    }

    @Override
    public Boolean call() {

        HttpResponse<String> response;
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
            logger.error("registration resp status: {}\n {}", response.statusCode(), flowSubscriber.getBody());
        } else {
            logger.debug("registration status: {}", response.statusCode());
        }
        return true;
    }
}
