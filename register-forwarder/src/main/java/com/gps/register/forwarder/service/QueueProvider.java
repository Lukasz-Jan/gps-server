package com.gps.register.forwarder.service;

import com.gps.shared.Constants;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class QueueProvider  {

    private QueueProvider() {
    }

    private static final BlockingDeque<String> registerReqtQue =
            new LinkedBlockingDeque<>(Constants.MAX_SRV_CAPACITY + 1_000);


    public static BlockingDeque<String> getQueue()  {
        return registerReqtQue;
    }
}
