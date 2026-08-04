package com.gps.position.forwarder.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.gps.shared.Constants;

public class QueueProvider {

        private static final BlockingQueue<String> blockingQue
                = new LinkedBlockingQueue<>(Constants.MAX_SRV_CAPACITY + 1_000);

    public static BlockingQueue<String> getPositionsQueue() {
        return blockingQue;
    }
}
