package com.gps.devices.message.transmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Receiver implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private final CompletableFuture<HttpResponse<String>> currentResponse;

    public Receiver(CompletableFuture<HttpResponse<String>> currentResponse) {
        this.currentResponse = currentResponse;
    }

    @Override
    public Boolean call() {

        HttpResponse<String> response = null;
        try {
            response = this.currentResponse.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            return false;
        }

        if(response.statusCode() != 200) {
            logger.info("resp status: {}",  response.statusCode());
        }
        else{
            logger.debug("resp status: {}",  response.statusCode());
        }

        return true;
    }
}
