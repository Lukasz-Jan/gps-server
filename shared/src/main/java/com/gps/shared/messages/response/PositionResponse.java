package com.gps.shared.messages.response;

import java.util.Objects;

public record PositionResponse(String macAddress,
                               PositionReceiveResult positionResult) {


    public PositionResponse {
        if (Objects.isNull(macAddress) || macAddress.isEmpty()) {
            throw new IllegalArgumentException("address is null");
        }

        if (Objects.isNull(positionResult)) {
            throw new IllegalArgumentException("receive result null");
        }
    }
}


