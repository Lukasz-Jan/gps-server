package com.gps.devices.message;

import com.gps.devices.message.factory.RegisterMessageFactory;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.RegisterMessage;
import com.gps.shared.messages.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RegisterRequestCreator {

    private static final Logger logger = LoggerFactory.getLogger(RegisterRequestCreator.class);
    private static final Utils msgUtils = new Utils();

    private final RegisterMessageFactory registerMessageFactory = new RegisterMessageFactory();

    public List<RegisterRequestDataWrapper> createRegisterRequests(int noOfDevices) throws URISyntaxException {

        List<RegisterRequestDataWrapper> registerRequests = new ArrayList<>();

        for (int i = 0; i < noOfDevices; i++) {
            RegisterMessage registerMessage = registerMessageFactory.createRandomRegisterMessage();
            String registerJson = msgUtils.serializeRegister(registerMessage);

            HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(registerJson);
            HttpRequest registerRequest =
                    HttpRequest
                            .newBuilder(new URI(Constants.REGISTER_ENDPOINT))
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .timeout(Duration.ofSeconds(1))
                            .POST(bodyPublisherString)
                            .build();
            registerRequests.add(new RegisterRequestDataWrapper(registerMessage.macAddress(), registerRequest));
        }
        return registerRequests;
    }

    public List<RegisterRequestDataWrapper> createRegisterRequests(int noOfDevices, String registerEndPoint) throws URISyntaxException {

        List<RegisterRequestDataWrapper> registerRequests = new ArrayList<>();

        for (int i = 0; i < noOfDevices; i++) {
            RegisterMessage registerMessage = registerMessageFactory.createRandomRegisterMessage();
            String registerJson = msgUtils.serializeRegister(registerMessage);

            HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(registerJson);
            HttpRequest registerRequest =
                    HttpRequest
                            .newBuilder(new URI(registerEndPoint))
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .timeout(Duration.ofSeconds(1))
                            .POST(bodyPublisherString)
                            .build();
            registerRequests.add(new RegisterRequestDataWrapper(registerMessage.macAddress(), registerRequest));
        }
        return registerRequests;
    }

    public HttpRequest createRegisterRequest(String address) throws URISyntaxException {

        RegisterMessage registerMessage = registerMessageFactory.createRandomRegisterMessage(address);
        String registerJson = msgUtils.serializeRegister(registerMessage);

        HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(registerJson);

        return HttpRequest.newBuilder(new URI(Constants.REGISTER_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(1))
                .POST(bodyPublisherString).build();
    }

    public HttpRequest createWrongRegister() throws URISyntaxException {

        HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString("Hello");

        return HttpRequest.newBuilder(new URI(Constants.REGISTER_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(1))
                .POST(bodyPublisherString).build();
    }
}
