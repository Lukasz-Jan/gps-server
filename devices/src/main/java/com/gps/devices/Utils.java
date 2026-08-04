package com.gps.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static void showHeap() {

        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        logger.info("heapSize   : " + heapSize);
        logger.info("heapMaxSize: " + heapMaxSize);

    }
}
