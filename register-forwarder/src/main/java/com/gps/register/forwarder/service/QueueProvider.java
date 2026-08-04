package com.gps.register.forwarder.service;

import com.gps.shared.Constants;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueProvider {

    private static final BlockingQueue<String> registerReqtQue =
            new LinkedBlockingQueue<>(Constants.MAX_SRV_CAPACITY + 1_000);

    public static BlockingQueue<String> getRequestQueue() {
        return registerReqtQue;
    }
}
