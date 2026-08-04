package com.gps.devices;

import com.gps.devices.message.factory.EndpointsMode;
import com.gps.devices.transmit.AsyncFetcher;
import com.gps.devices.transmit.FetchMode;
import com.gps.devices.transmit.GpsDataSender;
import com.gps.devices.transmit.RegistrationAck;
import com.gps.shared.Constants;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class GpsDataSenderLocalTest extends TestBase  {

    private GpsDataSender gpsDataSender;

    @Override
    @BeforeEach
    void setUp() {

        super.setUp();
        try {
            dbClient.remove(new Query(), Constants.GPS_DATA_COLLECTION);
        } catch (Exception e) {
            fail("no database connection");
        }
        gpsDataSender = new GpsDataSender(dbClient);

        MongoCollection<Document> accounts = dbClient.getCollection(Constants.GPS_DATA_COLLECTION);
        logger.info("accounts accounts.countDocuments() {}", accounts.countDocuments());

        RegistrationAck.resetTimeoutsQuantity();
        RegistrationAck.resetFailuresQuantity();
    }

    @ParameterizedTest
    @ValueSource(ints = {1000})
    void send_one_registration_and_its_positions(int positionsQuantity) {

        Assertions.assertDoesNotThrow(() -> gpsDataSender.sendRegisterAndItsPositions(positionsQuantity, EndpointsMode.LOCALY));

        await()
                .atMost(Duration.ofSeconds(23))
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.POSITIONS, positionsQuantity),
                        equalTo(1L)
                );
    }
}
