package com.gps.devices.request.factory;

import com.gps.devices.message.factory.AbstractMessageFactory;
import org.springframework.http.MediaType;

import java.net.http.HttpRequest;

public class RequestFactory {

    private RequestFactory() {
    }

    public static HttpRequest createHttpRequest(AbstractMessageFactory factory, String address,
                                                String endPoint, MediaType mediaType) {
        return new Request
                .Builder(factory)
                .setMacAddress(address)
                .setMediaType(mediaType)
                .setEndPoint(endPoint)
                .build()
                .httpRequest();
    }

    public static Request createRequest(AbstractMessageFactory factory,
                                            String endPoint, MediaType mediaType) {
        return new Request
                .Builder(factory)
                .setMediaType(mediaType)
                .setEndPoint(endPoint)
                .isRandom(true)
                .build();
    }
}
