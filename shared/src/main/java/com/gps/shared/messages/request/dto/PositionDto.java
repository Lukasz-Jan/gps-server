package com.gps.shared.messages.request.dto;

import com.gps.shared.messages.request.Message;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public record PositionDto(String macAddress,
        double latidude, double longitude, String timestamp) implements Message {

    public PositionDto {
        if(Math.abs(latidude) > (Math.PI)/2) {
            throw new IllegalArgumentException("Latitude behind range");
        }
        if(Math.abs(longitude) > (Math.PI)) {
            throw new IllegalArgumentException("Longitude behind range");
        }

        try {
            DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Wrong format for timestamp, " +
                    "shall be ISO_ZONED_DATE_TIME");
        }
    }

}

