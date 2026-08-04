package com.gps.devices.message.factory;

import com.gps.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PositionRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(PositionRequestFactory.class);
    private final PositionMessageFactory positionProducer = new PositionMessageFactory();

    public List<HttpRequest> createPositionsRequests(String address, int no) throws URISyntaxException {
        List<HttpRequest> positionRequests = new ArrayList<>();

        for(int i = 0; i < no; i++) {

            String positionMessage = positionProducer.createPositionMessage(address);

            HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(positionMessage);

            HttpRequest position = HttpRequest.newBuilder(new URI(selectEndpoint(i)))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(3))
                    .POST(bodyPublisherString).build();
            positionRequests.add(position);
        }
        return positionRequests;
    }

    public List<HttpRequest> createPositionsRequests(String address, int no, String positionEndPoint1, String positionEndPoint2) throws URISyntaxException {
        List<HttpRequest> positionRequests = new ArrayList<>();

        for(int i = 0; i < no; i++) {

            String positionMessage = positionProducer.createPositionMessage(address);

            HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(positionMessage);

            HttpRequest position = HttpRequest.newBuilder(new URI(selectEndpoint(i, positionEndPoint1, positionEndPoint2)))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(3))
                    .POST(bodyPublisherString).build();
            positionRequests.add(position);
        }
        return positionRequests;
    }

    public List<HttpRequest> createPositionsRequests(String address) throws URISyntaxException {
        List<HttpRequest> positionRequests = new ArrayList<>();

        for(int i = 0; i < 10; i++) {

            String positionMessage = positionProducer.createPositionMessage(address);

            HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(positionMessage);

            HttpRequest position = HttpRequest.newBuilder(new URI(Constants.POSITION_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(1))
                    .POST(bodyPublisherString).build();
            positionRequests.add(position);
        }
        return positionRequests;
    }

    public HttpRequest createPositionRequest(String address, String endPoint) throws URISyntaxException {

            String positionMessage = positionProducer.createPositionMessage(address);

            HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(positionMessage);

            return HttpRequest.newBuilder(new URI(endPoint))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(1))
                    .POST(bodyPublisherString)
                    .build();
    }

    public HttpRequest createEmptyRequest() throws URISyntaxException {

        HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString("");

        return HttpRequest.newBuilder(new URI(Constants.POSITION_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(1))
                .POST(bodyPublisherString)
                .build();
    }

    public HttpRequest createWrongPosition() throws URISyntaxException {

        HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString("error position");

        return HttpRequest.newBuilder(new URI(Constants.POSITION_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(1))
                .POST(bodyPublisherString)
                .build();
    }

    private String selectEndpoint(int i, String positionEndPoint1, String positionEndPoint2) {
        if(i%2 == 0) {
            return positionEndPoint1;
        } else return positionEndPoint2;
    }

    private String selectEndpoint(int i) {
        if(i%2 == 0) {
            return Constants.POSITION_ENDPOINT;
        } else return Constants.POSITION_ENDPOINT_2;
    }
}
