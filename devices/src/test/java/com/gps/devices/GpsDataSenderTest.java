package com.gps.devices;

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
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.mongodb.core.query.Query;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.fail;

class GpsDataSenderTest extends TestBase {

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
    }

    @ParameterizedTest
    @ValueSource(ints = {1000})
    void send_one_registration_and_its_positions(int positionsQuantity) {

        Assertions.assertDoesNotThrow(() -> gpsDataSender.sendRegisterAndItsPositions(positionsQuantity));

        await()
                .atMost(Duration.ofSeconds(11))
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.POSITIONS, positionsQuantity),
                        equalTo(1L)
                );
    }

    @ParameterizedTest
    @MethodSource("devicesAndPositionsQuantity_1")
    void send_registrations_and_their_positions(int devicesQuantity, int positionsPerDevice) {

        int insertRegistersTime = 5;
        int registrationsPollInterval = 2;

        Assertions.assertDoesNotThrow(() -> generate_registrations_positions_then_send(devicesQuantity,
                positionsPerDevice, insertRegistersTime, registrationsPollInterval));

        long sentRegistrationsQuantity = (long) devicesQuantity - RegistrationAck.getTimeoutsQuantity();

        await()
                .atMost(Duration.ofSeconds(10))
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.POSITIONS, positionsPerDevice),
                        greaterThanOrEqualTo(sentRegistrationsQuantity)
                );
    }

    @ParameterizedTest
    @MethodSource("devicesAndPositionsQuantity_2")
    void send_registrations_and_their_positions_2(int devicesQuantity, int positionsPerDevice) {

        int insertRegistrationsTime = 5;
        int registrationsPollInterval = 2;

        Assertions.assertDoesNotThrow(() -> generate_registrations_positions_then_send(devicesQuantity,
                positionsPerDevice, insertRegistrationsTime, registrationsPollInterval));

        long sentRegistrationsQuantity = (long) devicesQuantity - RegistrationAck.getTimeoutsQuantity();

        await()
                .atMost(Duration.ofSeconds(50))
                .with()
                .pollInterval(Duration.ofSeconds(5))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.POSITIONS, positionsPerDevice),
                        greaterThanOrEqualTo(sentRegistrationsQuantity)
                );
    }

    @ParameterizedTest
    @MethodSource("devicesAndPositionsQuantity_2_5")
    void send_registrations_and_their_positions_2_5(int devicesQuantity, int positionsPerDevice) {

        int timeToCommitFirstRegistrations = 20;
        int registrationsPollInterval = 5;

        Assertions.assertDoesNotThrow(() -> generate_registrations_positions_then_send(devicesQuantity,
                positionsPerDevice, timeToCommitFirstRegistrations, registrationsPollInterval));

        long sentRegistrationsQuantity = (long) devicesQuantity - RegistrationAck.getTimeoutsQuantity();

        logger.info("sent registrations quantity: {}", sentRegistrationsQuantity);
        logger.info("timeouts quantity          : {}", RegistrationAck.getTimeoutsQuantity());

        await()
                .atMost(Duration.ofSeconds(1200))
                .with()
                .pollInterval(Duration.ofSeconds(60))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.POSITIONS, positionsPerDevice),
                        greaterThanOrEqualTo(sentRegistrationsQuantity)
                );
    }

    @ParameterizedTest
    @MethodSource("devicesAndPositionsQuantity_3")
    void send_registrations_and_their_positions_large_numbers(int devicesQuantity, int positionsPerDevice) {

        int timeToCommitInitislRegistrations = 40;
        int registrationsPollInterval = 10;

        Assertions.assertDoesNotThrow(() -> generate_registrations_positions_then_send(devicesQuantity,
                positionsPerDevice, timeToCommitInitislRegistrations, registrationsPollInterval));

        long sentRegistrationsQuantity = (long) devicesQuantity - RegistrationAck.getTimeoutsQuantity();

        logger.info("sent registrations quantity: {}", sentRegistrationsQuantity);

        await()
                .atMost(Duration.ofSeconds(5400))
                .with()
                .pollInterval(Duration.ofSeconds(60))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.POSITIONS, positionsPerDevice),
                        greaterThanOrEqualTo(sentRegistrationsQuantity)
                );
    }

    @ParameterizedTest
    @MethodSource("devicesAndPositionsQuantity_4")
    void send_registrations_and_their_positions_large_numbers1(int devicesQuantity, int positionsPerDevice) {

        int timeToCommitFirstRegistrations = 600;
        int registrationsPollInterval = 60;

        Assertions.assertDoesNotThrow(() -> generate_registrations_positions_then_send(devicesQuantity,
                positionsPerDevice, timeToCommitFirstRegistrations, registrationsPollInterval));

        long sentRegistrationsQuantity = (long) devicesQuantity - RegistrationAck.getTimeoutsQuantity();

        await()
                .atMost(Duration.ofSeconds(10_800))
                .with()
                .pollInterval(Duration.ofSeconds(60))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.POSITIONS, positionsPerDevice),
                        greaterThanOrEqualTo(sentRegistrationsQuantity)
                );
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    void send_positions_without_registration(int positionsQuantity) {

        Assertions.assertDoesNotThrow(() -> gpsDataSender.sendPositions(positionsQuantity, Constants.FIXED_MAC,
                Constants.POSITION_ENDPOINT));
        checkPositionsQuantityPerAddress(0, positionsQuantity);
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void send_empty_position_no_registration(int positionsQuantity) {

        Assertions.assertDoesNotThrow(() -> gpsDataSender.sendEmptyPositions(positionsQuantity));
        checkPositionsQuantityPerAddress(0, positionsQuantity);
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void send_wrong_registration(int registrationsQuantity) {

        Assertions.assertDoesNotThrow(() -> gpsDataSender.sendWrongRegistrations(registrationsQuantity));
        checkRegistrationsQuantity(registrationsQuantity);
    }

    private void generate_registrations_positions_then_send(int devicesQuantity, int positionsPerDevice,
                                                            int commitRegisterationsTime, int registrationsPollInterval)
            throws URISyntaxException, ExecutionException, InterruptedException {

        gpsDataSender.sendRegisterAndItsPositions(devicesQuantity, positionsPerDevice, Constants.REGISTER_ENDPOINT,
                Constants.POSITION_ENDPOINT, Constants.POSITION_ENDPOINT_2, commitRegisterationsTime,
                registrationsPollInterval);
    }
}