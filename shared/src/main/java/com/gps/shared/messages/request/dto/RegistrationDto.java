package com.gps.shared.messages.request.dto;

import com.gps.shared.messages.DeviceType;
import com.gps.shared.messages.request.Message;

import java.util.Objects;

public record RegistrationDto(String macAddress, String name,
                              String owner, DeviceType deviceType) implements Message
{

    public RegistrationDto {
        if (Objects.isNull(macAddress) || macAddress.isEmpty()) {
            throw new IllegalArgumentException("mac address not defined");
        }
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("device name empty");
        }
        if (Objects.isNull(owner) || owner.isEmpty()) {
            throw new IllegalArgumentException("device owner not defined");
        }
        if (Objects.isNull(deviceType)) {
            throw new IllegalArgumentException("device type empty");
        }
    }
}