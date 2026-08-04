package com.gps.devices.message.factory;

import com.gps.shared.messages.DeviceType;
import com.gps.shared.messages.request.RegisterMessage;

public class RegisterMessageFactory extends BaseFactory {

    private static int ownerCounter;

    private final String[] deviceNames = {"Nokia", "Samsung", "Motorola",
            "Toyota", "Apple", "Mazda", "Panasonic"};

    private final String ownerBase = "Kazimierz_Kowalski_";

    public RegisterMessage createRandomRegisterMessage() {

        String pseudoRandomMac = createMac();
        String name = createDeviceName();
        String owner = createDeviceOwner();
        DeviceType type = DeviceType.type(rand.nextInt(0, DeviceType.values().length));

        return new RegisterMessage(pseudoRandomMac
                , name, owner, type);
    }

    public RegisterMessage createRandomRegisterMessage(String fixedMac) {
        String name = createDeviceName();
        String owner = createDeviceOwner();
        DeviceType type = DeviceType.type(rand.nextInt(0, DeviceType.values().length));

        return new RegisterMessage(fixedMac
                , name, owner, type);
    }

    private String createDeviceName() {

        return deviceNames[rand.nextInt(0, deviceNames.length)];
    }

    private String createDeviceOwner() {
        return new StringBuilder(ownerBase).append(Integer.valueOf(ownerCounter++)).toString();
    }
}
