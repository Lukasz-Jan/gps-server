package com.gps.shared.messages;


import java.util.HashMap;
import java.util.Map;

public enum DeviceType {

    MOBILE_PHONE(0),
    SMART_WATCH(1),
    RADIO(2),
    CAR_NAVIGATION(3),
    SHIP_NAVIGATION(4);

    private static final Map<Integer, DeviceType> BY_NUMBER = new HashMap<>();

    static {
        for (DeviceType deviceType : values()) {
            BY_NUMBER.put(deviceType.number, deviceType);
        }
    }

    private final int number;

    DeviceType(int i) {this.number = i;}

    public static DeviceType type(int i) {
        return BY_NUMBER.get(i);
    }
}
