package com.gps.devices.transmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Ack implements Callable<Boolean> {

    public static final AtomicInteger timeoutsQuantity = new AtomicInteger(0);

    protected final CompletableFuture<HttpResponse<String>> currentResponse;
    protected final StringSubscriber flowSubscriber;

    public Ack(CompletableFuture<HttpResponse<String>> currentResponse, StringSubscriber flowSubscriber) {
        this.currentResponse = currentResponse;
        this.flowSubscriber = flowSubscriber;
    }

    public static long getTimeoutsQuantity() {
        return timeoutsQuantity.longValue();
    }
}
