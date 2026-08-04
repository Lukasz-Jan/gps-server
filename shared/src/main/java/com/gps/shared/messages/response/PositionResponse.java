package com.gps.shared.messages.response;

import jakarta.validation.constraints.NotEmpty;

public record PositionResponse(@NotEmpty String macAddress,
                               @NotEmpty PositionResult positionResult) {}


