package com.gps.shared.messages.request;

import com.gps.shared.messages.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterMessage(@NotBlank String macAddress, @NotBlank String name,
                              @NotBlank String owner, DeviceType deviceType) {

}