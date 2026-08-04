package com.gps.devices.transmit;

import com.gps.devices.message.RegisterRequestCreator;
import com.gps.devices.message.RegistrationRequestDataWrapper;
import com.gps.devices.message.factory.PositionRequestFactory;
import com.gps.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


public class GpsDataSender {

    private static final Logger logger = LoggerFactory.getLogger(GpsDataSender.class);

    private static final RegisterRequestCreator registerBuilder = new RegisterRequestCreator();

    private static final PositionRequestFactory positionBuilder = new PositionRequestFactory();

    private static final Sender sender = new Sender();

    private static final double REGISTRATION_FACTOR = 0.7;

    private final MongoTemplate dbClient;

    public GpsDataSender(MongoTemplate dbClient) {
        this.dbClient = dbClient;
    }

    public void sendRegisterAndItsPositions(int no) throws URISyntaxException, ExecutionException,
            InterruptedException {

        HttpRequest registerRequest = registerBuilder.createRegisterRequest(Constants.FIXED_MAC);
        List<HttpRequest> positionsRequests = positionBuilder.createPositionsRequests(Constants.FIXED_MAC, no);

        sender.send(registerRequest);

        sender.sendRegistersRequests(positionsRequests);
    }

    public void sendRegisterAndItsPositions(int devicesQuantity, int positionsQuantityPerDevice,
                                            String registerEndPoint, String positionEndPoint1,
                                            String positionEndPoint2, int timeToCommitInitialRegistrations,
                                            int registrationsPollInterval
                                            )
            throws URISyntaxException, ExecutionException, InterruptedException {

        long initialRegistrationsQuantity = (long)(REGISTRATION_FACTOR * devicesQuantity);

        List<RegistrationRequestDataWrapper> registrationWrappers = registerBuilder.createRegisterRequests(devicesQuantity, registerEndPoint);
        List<String> adresses = new ArrayList<>(devicesQuantity);

        sender.sendRegistrations(registrationWrappers);

        logger.info("Registrations sent.");

        await()
                .atMost(Duration.ofSeconds(timeToCommitInitialRegistrations))
                .pollInterval(Duration.ofSeconds(registrationsPollInterval))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.DEVICES, positionsQuantityPerDevice),
                        greaterThanOrEqualTo(initialRegistrationsQuantity)
                );

        logger.info("Initial registrations committed, sending positions...");

        for (RegistrationRequestDataWrapper registerWrapper : registrationWrappers) {
            adresses.add(registerWrapper.macAddress());
        }

        for (String address : adresses) {

            List<HttpRequest> positionsRequests = positionBuilder.createPositionsRequests(address,
                    positionsQuantityPerDevice, positionEndPoint1, positionEndPoint2);
            sender.sendRegistersRequests(positionsRequests);
        }

        logger.info("Positions sent");

        sender.closeExecutors();
    }


    public void sendPositions(int no, String address, String endPoint) throws URISyntaxException, ExecutionException,
            InterruptedException {
        for (int i = 0; i < no; i++) {
            HttpRequest positionsRequests = positionBuilder.createPositionRequest(address, endPoint);
            sender.send(positionsRequests);
        }
    }

    public void sendEmptyPositions(int quantity) throws URISyntaxException, ExecutionException,
            InterruptedException {
        for (int i = 0; i < quantity; i++) {
            HttpRequest positionsRequests = positionBuilder.createEmptyRequest();
            sender.send(positionsRequests);
        }
    }

    public void sendWrongRegistrations(int quantity) throws URISyntaxException, ExecutionException,
            InterruptedException {

        for (int i = 0; i < quantity; i++) {
            HttpRequest register = registerBuilder.createWrongRegister();
            sender.send(register);
        }
    }
}
