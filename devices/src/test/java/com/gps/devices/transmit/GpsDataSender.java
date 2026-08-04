package com.gps.devices.transmit;


import com.gps.devices.message.factory.PositionMessageFactory;
import com.gps.devices.message.factory.RegistrationMessageFactory;
import com.gps.devices.request.RequestProvider;
import com.gps.devices.message.factory.exceptions.NoMacAddress;
import com.gps.devices.request.factory.Request;
import com.gps.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GpsDataSender {

    private static final Logger logger = LoggerFactory.getLogger(GpsDataSender.class);

    private static final RequestProvider requestProvider = new RequestProvider();

    private static final Sender sender = new Sender();

    private static final double REGISTRATION_FACTOR = 0.7;

    private final MongoTemplate dbClient;

    public GpsDataSender(MongoTemplate dbClient) {
        this.dbClient = dbClient;
    }


    public void sendRegisterAndItsPositions(int positionsQuantity) throws ExecutionException,
            InterruptedException {

        HttpRequest registrationRequest = requestProvider
                .createRequest(Constants.FIXED_MAC, MediaType.APPLICATION_JSON,
                        Constants.REGISTER_ENDPOINT, new RegistrationMessageFactory());

        List<HttpRequest> positionsRequests = requestProvider.createHttpRequests(Constants.FIXED_MAC,
                MediaType.APPLICATION_JSON, positionsQuantity,
                new PositionMessageFactory());

        sender.send(registrationRequest);

        sender.sendHttpRequests(positionsRequests, MessageType.POSITION);
    }

    public void sendRegisterAndItsPositions(int devicesQuantity, int positionsQuantityPerDevice,
                                            int timeToCommitInitialRegistrations, int registrationsPollInterval) throws NoMacAddress {

        long initialRegistrationsQuantity = (long) (REGISTRATION_FACTOR * devicesQuantity);

        List<Request> registrationRequests = requestProvider.createRequests(MediaType.APPLICATION_JSON, devicesQuantity, new RegistrationMessageFactory());

        sender.sendRequest(registrationRequests, MessageType.REGISTRATION);

        logger.info("Registrations sent.");

        await()
                .atMost(Duration.ofSeconds(timeToCommitInitialRegistrations))
                .pollInterval(Duration.ofSeconds(registrationsPollInterval))
                .until(
                        new AsyncFetcher<>(dbClient, FetchMode.DEVICES, positionsQuantityPerDevice),
                        greaterThanOrEqualTo(initialRegistrationsQuantity)
                );

        logger.info("Initial registrations committed, sending positions...");


        for (String address : requestProvider.fetchAdresses(registrationRequests)) {

            List<HttpRequest> positionRequests =
                    requestProvider.createHttpRequests(address, MediaType.APPLICATION_JSON, positionsQuantityPerDevice, new PositionMessageFactory());
            sender.sendHttpRequests(positionRequests, MessageType.POSITION);
        }

        sender.closeExecutors();
    }

    public void sendPositions(int no, String address, String endPoint) throws URISyntaxException, ExecutionException,
            InterruptedException {
        for (int i = 0; i < no; i++) {
            HttpRequest positionsRequest = requestProvider.createPositionRequest(address, endPoint);
            sender.send(positionsRequest);
        }
    }

    public void sendEmptyPositions(int quantity) throws URISyntaxException, ExecutionException,
            InterruptedException {

        for (int i = 0; i < quantity; i++) {
            HttpRequest positionsRequests = requestProvider.createEmptyRequest();
            sender.send(positionsRequests);
        }
    }

    public void sendRegistrationWithWrongBody(int quantity) throws URISyntaxException, ExecutionException,
            InterruptedException, TimeoutException {

        for (int i = 0; i < quantity; i++) {
            HttpRequest registrationRequest = requestProvider.createRegistrationWithBadBody("Hello");
            int status = sender.sendAndGetStatus(registrationRequest);
            assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        }
    }

    public void sendRegistrationWithNullAddress(int quantity) throws ExecutionException,
            InterruptedException, TimeoutException {

        for (int i = 0; i < quantity; i++) {
            HttpRequest registrationRequest = requestProvider.createRegistrationRequestWithNullAddress();
            int status = sender.sendAndGetStatus(registrationRequest);
            assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        }
    }
}
