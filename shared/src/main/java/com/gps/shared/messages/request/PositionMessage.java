package com.gps.shared.messages.request;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public record PositionMessage(String macAddress, double latidude, double longitude,
                              String timestamp) {

    public PositionMessage {
        if(Math.abs(latidude) > (Math.PI)/2) {;
            throw new IllegalArgumentException("Latitude behind range");
        }
        if(Math.abs(longitude) > (Math.PI)) {;
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
