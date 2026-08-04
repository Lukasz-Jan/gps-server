package com.gps.shared.messages.request.dto.data;

public record Position(double latitude, double longitude,
                       String timestamp) {
}