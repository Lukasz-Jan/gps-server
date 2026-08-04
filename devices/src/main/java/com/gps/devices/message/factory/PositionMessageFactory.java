package com.gps.devices.message.factory;

import com.gps.shared.Constants;
import com.gps.shared.messages.request.Message;
import com.gps.shared.messages.request.dto.tests.PositionDto;
import org.apache.tomcat.util.http.InvalidParameterException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class PositionMessageFactory extends AbstractMessageFactory {

    public String createRandomPositionMessage() {

        double longitude = createLongitude();
        double latitude = createLatitude();

        String timestamp = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now(ZONE_ID));
        Message message = new PositionDto(createRandomMac(), latitude,
                longitude, timestamp);

        return msgUtils.serializeMessage(message);
    }

    public String createPositionMessageJson(String macAddress) {

        double longitude = createLongitude();
        double latitude = createLatitude();

        String timestamp = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now(ZONE_ID));
        Message positionMessage = new PositionDto(macAddress, latitude,
                longitude, timestamp);
        return msgUtils.serializeMessage(positionMessage);
    }

    public Message createMessage(String macAddress) {

        double longitude = createLongitude();
        double latitude = createLatitude();

        String timestamp = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now(ZONE_ID));
        return new PositionDto(macAddress, latitude,
                longitude, timestamp);
    }

    @Override
    public List<String> getEndpoints() {
        return List.of(Constants.POSITION_ENDPOINT, Constants.POSITION_ENDPOINT_2);
    }

    @Override
    public String createJson(String macAddress) {
        if (Objects.isNull(macAddress)) {
            throw new InvalidParameterException(" no mac address");
        }
        Message message = createMessage(macAddress);
        return msgUtils.serializeMessage(message);
    }

    @Override
    public Message createRandomMessage() {
        return null;
    }

    private double createLatitude() {
        double random = randomProvider.nextInt(-9000, 9000) / 100d;
        return random / 360 * 2 * Math.PI;
    }

    private double createLongitude() {
        double random = randomProvider.nextInt(-1799, 1799) / 10d;
        return random / 360 * 2 * Math.PI;
    }
}
