package com.gps.position.forwarder.service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import com.gps.shared.Constants;


public class QueueProvider {

    private QueueProvider() {
    }

    private static final BlockingDeque<String> blockingQue
            = new LinkedBlockingDeque<>(Constants.MAX_SRV_CAPACITY + 1_000);

    public static BlockingDeque<String> getQueue() {
        return blockingQue;
    }
}
