package com.gps.shared.messages.request.dto.tests;

import com.gps.shared.messages.request.Message;

public record PositionDto(String macAddress,
        double latidude, double longitude, String timestamp) implements Message {

}

