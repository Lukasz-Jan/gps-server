package com.gps.devices.message.factory;

import com.gps.shared.messages.request.Message;
import com.gps.shared.messages.utils.Utils;

import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class AbstractMessageFactory {

    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
    protected static final Random randomProvider = new Random();
    protected final Utils msgUtils = new Utils();

    public String createRandomMac() {

        String rnd =
                UUID.randomUUID().toString().replace("-", "").substring(0, 18);
        return rnd.substring(0, 2) + ":" + rnd.substring(2,
                4) + ":" + rnd.substring(4, 6) + ":" + rnd.substring(6, 8) +
                ":" + rnd.substring(8, 10) + ":" + rnd.substring(10, 12);
    }

    public abstract Message createMessage(String macAddress);
    public abstract List<String> getEndpoints();
    public abstract String createJson(String macAddress);
    public abstract Message createRandomMessage();

    public String createJson(Message message) {
        return msgUtils.serializeMessage(message);
    }
}