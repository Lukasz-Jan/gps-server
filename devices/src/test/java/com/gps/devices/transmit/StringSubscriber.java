package com.gps.devices.transmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Flow;

public class StringSubscriber implements Flow.Subscriber<ByteBuffer>{

    private static final Logger logger = LoggerFactory.getLogger(StringSubscriber.class);
    private String requestMsg;

    private final HttpResponse.BodySubscriber<String> bodySubscriber;

    protected StringSubscriber(HttpResponse.BodySubscriber<String> bodySubscriber) {
        this.bodySubscriber = bodySubscriber;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        bodySubscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(ByteBuffer item) {

        requestMsg = new String(item.array());
        bodySubscriber.onNext(List.of(item));
    }

    @Override
    public void onError(Throwable throwable) {
        bodySubscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        bodySubscriber.onComplete();
    }

    public String getBody() {
        return requestMsg;
    }
}
