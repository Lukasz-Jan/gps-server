package com.gps.devices.request;

import com.gps.devices.message.factory.AbstractMessageFactory;
import com.gps.devices.message.factory.PositionMessageFactory;
import com.gps.devices.message.factory.RegistrationMessageFactory;
import com.gps.devices.message.factory.exceptions.NoMacAddress;
import com.gps.devices.request.factory.Request;
import com.gps.devices.request.factory.RequestFactory;
import com.gps.shared.Constants;
import org.springframework.http.MediaType;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestProvider {

    private final PositionMessageFactory positionFactory = new PositionMessageFactory();

    public HttpRequest createRequest(
            String address, MediaType mediaType, String endPoint, AbstractMessageFactory factory) {

        return RequestFactory.createHttpRequest(factory,
                address, endPoint, mediaType);
    }


    public List<HttpRequest> createHttpRequests(
            String address, MediaType mediaType, int quantity,
            AbstractMessageFactory factory) {

        List<HttpRequest> requests = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            String endpoint = selectEndpoint(factory.getEndpoints(), i);
            HttpRequest request = RequestFactory.createHttpRequest(factory, address, endpoint, mediaType);
            requests.add(request);
        }
        return requests;
    }

    public List<Request> createRequests(MediaType mediaType, int quantity, AbstractMessageFactory factory) {

        List<Request> requests = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {

            String endpoint = selectEndpoint(factory.getEndpoints(), i);
            Request request = RequestFactory.createRequest(factory, endpoint, mediaType);
            requests.add(request);
        }
        return requests;
    }

    public List<String> fetchAdresses(List<Request> requests) throws NoMacAddress {

        List<String> adresses = new ArrayList<>();

        for (Request request : requests) {
            if (Objects.isNull(request.getMacAddress()) || request.getMacAddress().isEmpty())
                throw new NoMacAddress("no address to construct message");
            adresses.add(request.getMacAddress());
        }
        return adresses;
    }


    public HttpRequest createPositionRequest(String address, String endPoint) throws URISyntaxException {

        String positionMessage = positionFactory.createPositionMessageJson(address);

        HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(positionMessage);

        return HttpRequest.newBuilder(new URI(endPoint))
                .header(Constants.CONTENT_TYPE, Constants.APP_JSON)
                .header(Constants.ACCEPT_HEADER, Constants.APP_JSON)
                .timeout(Duration.ofSeconds(1))
                .POST(bodyPublisherString)
                .build();
    }

    public HttpRequest createEmptyRequest() throws URISyntaxException {

        HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString("");

        return HttpRequest.newBuilder(new URI(Constants.POSITION_ENDPOINT))
                .header(Constants.CONTENT_TYPE, Constants.APP_JSON)
                .header(Constants.ACCEPT_HEADER, Constants.APP_JSON)
                .timeout(Duration.ofSeconds(1))
                .POST(bodyPublisherString)
                .build();
    }

    public HttpRequest createRegistrationWithBadBody(String body) throws URISyntaxException {

        HttpRequest.BodyPublisher bodyPublisherString = HttpRequest.BodyPublishers.ofString(body);

        return HttpRequest.newBuilder(new URI(Constants.REGISTER_ENDPOINT))
                .header(Constants.CONTENT_TYPE, Constants.APP_JSON)
                .header(Constants.ACCEPT_HEADER, Constants.APP_JSON)
                .timeout(Duration.ofSeconds(1))
                .POST(bodyPublisherString)
                .build();
    }

    public HttpRequest createRegistrationRequestWithNullAddress() {

        RegistrationMessageFactory registrationMessageFactory = new RegistrationMessageFactory();

        return RequestFactory.createHttpRequest(registrationMessageFactory, null,
                registrationMessageFactory.getEndpoints().getFirst(), MediaType.APPLICATION_JSON);
    }


    private String selectEndpoint(List<String> endpoints, int i) {

        int divider = endpoints.size();
        int res = i % divider;
        return endpoints.get(res);
    }
}
