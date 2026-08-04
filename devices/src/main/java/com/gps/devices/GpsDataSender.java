package com.gps.devices;

import com.gps.devices.message.RegisterRequestCreator;
import com.gps.devices.message.RegisterRequestDataWrapper;
import com.gps.devices.message.factory.PositionRequestFactory;
import com.gps.devices.message.transmit.Sender;
import com.gps.shared.Constants;

import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GpsDataSender {

    private static final RegisterRequestCreator registerBuilder = new RegisterRequestCreator();

    private static final PositionRequestFactory positionBuilder = new PositionRequestFactory();

    private static final Sender sender = new Sender();

    public void sendRegisterAndItsPositions(int no) throws URISyntaxException, ExecutionException,
            InterruptedException {

        HttpRequest registerRequest = registerBuilder.createRegisterRequest(Constants.FIXED_MAC);
        List<HttpRequest> positionsRequests = positionBuilder.createPositionsRequests(Constants.FIXED_MAC, no);
        sender.send(registerRequest);

        sender.sendManyRequests(positionsRequests);
    }

    public List<String> sendRegisterAndItsPositions(int noOfDevices, int noOfPositionsPerDevice) throws URISyntaxException, ExecutionException,
            InterruptedException {

        List<RegisterRequestDataWrapper> registrationWrappers = registerBuilder.createRegisterRequests(noOfDevices);
        List<String> adresses = new ArrayList<>(noOfDevices);

        sender.sendMany(registrationWrappers);

        for (RegisterRequestDataWrapper registerWrapper : registrationWrappers) {
            adresses.add(registerWrapper.macAddress());
        }

        for (String address : adresses) {
            List<HttpRequest> positionsRequests = positionBuilder.createPositionsRequests(address,
                    noOfPositionsPerDevice);
            sender.sendManyRequests(positionsRequests);
        }
        sender.closeExecutors();

        return adresses;
    }

    public List<String> sendRegisterAndItsPositions(int noOfDevices, int noOfPositionsPerDevice,
                                                    String registerEndPoint,
                                                    String positionEndPoint1, String positionEndPoint2) throws URISyntaxException,
            ExecutionException,
            InterruptedException {

        List<RegisterRequestDataWrapper> registrationWrappers = registerBuilder.createRegisterRequests(noOfDevices, registerEndPoint);
        List<String> adresses = new ArrayList<>(noOfDevices);

        sender.sendMany(registrationWrappers);

        for (RegisterRequestDataWrapper registerWrapper : registrationWrappers) {
            adresses.add(registerWrapper.macAddress());
        }

        for (String address : adresses) {
            List<HttpRequest> positionsRequests = positionBuilder.createPositionsRequests(address,
                    noOfPositionsPerDevice);
            sender.sendManyRequests(positionsRequests);
        }
        sender.closeExecutors();

        return adresses;
    }



    public void sendPositions(int no, String address, String endPoint) throws URISyntaxException, ExecutionException,
            InterruptedException {
        for (int i = 0; i < no; i++) {

            HttpRequest positionsRequests = positionBuilder.createPositionRequest(address, endPoint);
            sender.send(positionsRequests);
        }
    }

    public void sendEmptyPositions(int no) throws URISyntaxException, ExecutionException,
            InterruptedException {
        for (int i = 0; i < no; i++) {

            HttpRequest positionsRequests = positionBuilder.createEmptyRequest();
            sender.send(positionsRequests);
        }
    }

    public void sendWrongRegister(int no) throws URISyntaxException, ExecutionException,
            InterruptedException {
        for (int i = 0; i < no; i++) {
            HttpRequest register = registerBuilder.createWrongRegister();
            sender.send(register);
        }
    }

    public void sendWrongPosition(int no) throws URISyntaxException, ExecutionException,
            InterruptedException {
        for (int i = 0; i < no; i++) {

            HttpRequest register = positionBuilder.createWrongPosition();
            sender.send(register);
        }
    }
}
