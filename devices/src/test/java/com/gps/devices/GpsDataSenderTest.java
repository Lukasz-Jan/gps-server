package com.gps.devices;

import com.gps.shared.Constants;
import com.mongodb.MongoSocketOpenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.data.mongodb.core.query.Query;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.fail;

class GpsDataSenderTest extends BaseTest {

    private final GpsDataSender gpsDataSender = new GpsDataSender();

    @BeforeEach
    void setUp() {
        mongo.remove(new Query(), Constants.GPS_DATA_COLLECTION);
    }

    @AfterEach
    void tearDown() {
    }


    @ParameterizedTest
    @ValueSource(ints = {1000})
    void send_one_register_and_its_positions(int noOfPositions) throws InterruptedException {
        try {
            gpsDataSender.sendRegisterAndItsPositions(noOfPositions);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        checkCollection(1, noOfPositions);
    }

    @ParameterizedTest
    @MethodSource("noOfDevicesAndPositions_1")
    void send_registrations_and_their_positions(int noOfDevices, int positionsPerDevice) {

        try {
            gpsDataSender.sendRegisterAndItsPositions(noOfDevices, positionsPerDevice);

        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        checkCollection(noOfDevices, positionsPerDevice);
    }

    @ParameterizedTest
    @MethodSource("noOfDevicesAndPositions_2")
    void send_registrations_and_their_positions_2(int noOfDevices, int positionsPerDevice) {

        try {
            gpsDataSender.sendRegisterAndItsPositions(noOfDevices, positionsPerDevice,Constants.REGISTER_ENDPOINT,
                    Constants.POSITION_ENDPOINT, Constants.POSITION_ENDPOINT_2);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        checkCollection(noOfDevices, positionsPerDevice);
    }

    @ParameterizedTest
    @MethodSource("noOfDevicesAndPositions_2_5")
    void send_registrations_and_their_positions_2_5(int noOfDevices, int positionsPerDevice) {

        try {
            gpsDataSender.sendRegisterAndItsPositions(noOfDevices, positionsPerDevice,Constants.REGISTER_ENDPOINT,
                    Constants.POSITION_ENDPOINT, Constants.POSITION_ENDPOINT_2);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        checkCollection(noOfDevices, positionsPerDevice);
    }

    @ParameterizedTest
    @MethodSource("noOfDevicesAndPositions_3")
    void send_registrations_and_their_positions_large_numbers(int noOfDevices, int positionsPerDevice)  {
        try {
            gpsDataSender.sendRegisterAndItsPositions(noOfDevices, positionsPerDevice,Constants.REGISTER_ENDPOINT,
                    Constants.POSITION_ENDPOINT, Constants.POSITION_ENDPOINT_2);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        sleep(4800);

        checkCollection(noOfDevices, positionsPerDevice);
    }

    @ParameterizedTest
    @MethodSource("noOfDevicesAndPositions_4")
    void send_registrations_and_their_positions_large_numbers1(int noOfDevices, int positionsPerDevice)  {
        try {
            gpsDataSender.sendRegisterAndItsPositions(noOfDevices, positionsPerDevice,Constants.REGISTER_ENDPOINT,
                    Constants.POSITION_ENDPOINT, Constants.POSITION_ENDPOINT_2);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        sleep(4800);

        checkCollection(noOfDevices, positionsPerDevice);
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    void having_mac_send_positions(int noOfPositions)  {
        try {
            gpsDataSender.sendPositions(noOfPositions, Constants.FIXED_MAC, Constants.POSITION_ENDPOINT);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }
        checkCollection(0, noOfPositions);
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void send_empty_position(int noOfPositions) {
        try {
            gpsDataSender.sendEmptyPositions(noOfPositions);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        checkCollection(0, noOfPositions);
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void send_wrong_register(int no) {
        try {
            gpsDataSender.sendWrongRegister(no);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        checkCollection(0, 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void send_wrong_position(int no) {
        try {
            gpsDataSender.sendWrongPosition(no);
        } catch (ExecutionException e) {
            fail("fail ExecutionException connection, timeout");
        } catch (MongoSocketOpenException e) {
            fail("fail MongoSocketOpenException");
        } catch (URISyntaxException | InterruptedException exception) {
            fail();
        }

        checkCollection(0, 0);
    }

    private void sleep(int sec) {
        try {
            Thread.sleep(Duration.ofSeconds(4800));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}