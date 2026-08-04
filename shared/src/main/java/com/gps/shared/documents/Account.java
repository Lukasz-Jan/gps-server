package com.gps.shared.documents;

import com.gps.shared.Constants;
import com.gps.shared.messages.DeviceType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = Constants.GPS_DATA_COLLECTION)
public record Account(
        @Indexed(unique = true)
        String macAddress,
        String deviceName,
        String owner,
        DeviceType deviceType,
        List<Position> positions) {
}
