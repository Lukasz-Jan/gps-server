package com.gps.shared.messages.request.dto.tests;

import com.gps.shared.messages.DeviceType;
import com.gps.shared.messages.request.Message;

public record RegistrationDto(String macAddress, String name,
                              String owner, DeviceType deviceType) implements Message {
}