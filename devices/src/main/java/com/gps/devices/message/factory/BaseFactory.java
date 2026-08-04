package com.gps.devices.message.factory;

import com.gps.shared.messages.utils.Utils;

import java.util.Random;
import java.util.UUID;

public class BaseFactory {

    protected static final Random rand = new Random();
    protected final Utils msgUtils = new Utils();

    protected String createMac() {

        String rnd =
                UUID.randomUUID().toString().replace("-", "").substring(0, 18);

        String pseudoRandomMac = rnd.substring(0, 2) + ":" + rnd.substring(2,
                4) + ":" + rnd.substring(4, 6) + ":" + rnd.substring(6, 8) +
                ":" + rnd.substring(8, 10) + ":" + rnd.substring(10, 12);

        return pseudoRandomMac;
    }
}