package com.gps.shared.documents;

import com.gps.shared.messages.DeviceType;
import com.gps.shared.messages.request.PositionMessageM;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "gpsAccounts")
public record Account(@Id String macAddress, String deviceName
        , String owner, DeviceType deviceType, List<PositionMessageM> list) {
}
