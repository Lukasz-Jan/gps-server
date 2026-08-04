package com.gps.devices.transmit;

import com.gps.shared.Constants;
import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Ack implements Runnable {

    protected final CompletableFuture<HttpResponse<String>> currentResponse;
    protected final StringSubscriber flowSubscriber;

    public Ack(CompletableFuture<HttpResponse<String>> currentResponse, StringSubscriber flowSubscriber) {
        this.currentResponse = currentResponse;
        this.flowSubscriber = flowSubscriber;
    }
}
