package com.gps.devices.message.factory;

import com.gps.shared.messages.request.PositionMessage;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PositionMessageFactory extends BaseFactory {

    public String createRandomPositionMessage() {

        double longitude = createLongitude();
        double latitude = createLatitude();

        String timestamp = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now());
        PositionMessage positionMessage = new PositionMessage(createMac(), latitude,
                longitude, timestamp);
        return msgUtils.serializePosition(positionMessage);
    }

    public String createPositionMessage(String macAddress) {

        double longitude = createLongitude();
        double latitude = createLatitude();

        String timestamp = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now());
        PositionMessage positionMessage = new PositionMessage(macAddress, latitude,
                longitude, timestamp);
        return msgUtils.serializePosition(positionMessage);
    }

    @Override
    public String createMac() {
        return super.createMac();
    }

    private double createLatitude() {
        double random = rand.nextInt(-9000, 9000) / 100d;
        return random / 360 * 2 * Math.PI;
    }

    private double createLongitude() {
        double random = rand.nextInt(-1799, 1799) / 10d;
        return random / 360 * 2 * Math.PI;
    }
}
