package com.gps.devices.message.factory;

import com.gps.shared.Constants;
import com.gps.shared.messages.DeviceType;
import com.gps.shared.messages.request.Message;
import com.gps.shared.messages.request.dto.tests.RegistrationDto;


import java.util.List;

public class RegistrationMessageFactory extends AbstractMessageFactory {

    private static int ownerCounter;

    private final String[] deviceNames = {"Nokia", "Samsung", "Motorola",
            "Toyota", "Apple", "Mazda", "Panasonic"};

    private static final String OWNER_BASE = "Kazimierz_Kowalski_";

    @Override
    public String createJson(String macAddress) {
            Message message = createMessage(macAddress);
            return msgUtils.serializeMessage(message);
    }


    @Override
    public Message createRandomMessage() {

        String pseudoRandomMac = createRandomMac();
        String name = createDeviceName();
        String owner = createDeviceOwner();
        DeviceType type = DeviceType.type(randomProvider.nextInt(0, DeviceType.values().length));
        return new RegistrationDto(pseudoRandomMac,  name, owner, type);
    }

    @Override
    public Message createMessage(String macAddress) {
        String name = createDeviceName();
        String owner = createDeviceOwner();
        DeviceType type = DeviceType.type(randomProvider.nextInt(0, DeviceType.values().length));

        return new RegistrationDto(macAddress,  name, owner, type);
    }

    public RegistrationDto createRandomRegisterMessage(String fixedMac) {
        String name = createDeviceName();
        String owner = createDeviceOwner();
        DeviceType type = DeviceType.type(randomProvider.nextInt(0, DeviceType.values().length));

        return new RegistrationDto(fixedMac
                , name, owner, type);
    }

    private String createDeviceName() {

        return deviceNames[randomProvider.nextInt(0, deviceNames.length)];
    }

    private static String createDeviceOwner() {
        return OWNER_BASE + ownerCounter++;
    }

    @Override
    public List<String> getEndpoints() {
        return List.of(Constants.REGISTER_ENDPOINT);
    }
}
