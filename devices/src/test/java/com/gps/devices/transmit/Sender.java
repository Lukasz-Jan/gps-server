package com.gps.devices.transmit;


import com.gps.devices.request.factory.Request;
import com.gps.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private static final HttpClient client = HttpClient.newHttpClient();
    private final ExecutorService executors = Executors.newFixedThreadPool(4);

    public void send(HttpRequest request) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> voidCompletableFuture = client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(httpResponse -> {
                            logger.debug("{}", httpResponse.statusCode());
                        }
                );
        voidCompletableFuture.get();
    }

    public int sendAndGetStatus(HttpRequest request) throws ExecutionException, InterruptedException, TimeoutException {

        CompletableFuture<Integer> integerCompletableFuture =
                client
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(resp -> resp.statusCode());

        return integerCompletableFuture.get(Constants.REGISTER_TIMEOUT, TimeUnit.SECONDS);
    }

    public void sendHttpRequests(List<HttpRequest> requests, MessageType msgType) {

        for (HttpRequest httpRequest : requests) {

            HttpRequest.BodyPublisher bodyPublisher = httpRequest.bodyPublisher().get();
            var bodySubscriber = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
            var flowSubscriber = new StringSubscriber(bodySubscriber);
            bodyPublisher.subscribe(flowSubscriber);

            CompletableFuture<HttpResponse<String>> completableFuture = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

            Ack ack = null;
            if (msgType.equals(MessageType.REGISTRATION)) {
                ack = new RegistrationAck(completableFuture, flowSubscriber);
            } else if (msgType.equals(MessageType.POSITION)) {
                ack = new PositionAck(completableFuture, flowSubscriber);
            }

            executors.submit(ack);
        }
    }


    public void sendRequest(List<Request> requests, MessageType msgType) {

        for (Request request : requests) {

            HttpRequest httpRequest = request.httpRequest();

            HttpRequest.BodyPublisher bodyPublisher = httpRequest.bodyPublisher().get();
            var bodySubscriber = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
            var flowSubscriber = new StringSubscriber(bodySubscriber);
            bodyPublisher.subscribe(flowSubscriber);

            CompletableFuture<HttpResponse<String>> completableFuture = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

            Optional<Ack> ack = Optional.empty();
            if (msgType.equals(MessageType.REGISTRATION)) {
                ack = Optional.ofNullable(new RegistrationAck(completableFuture, flowSubscriber));
            } else if (msgType.equals(MessageType.POSITION)) {
                ack = Optional.ofNullable(new PositionAck(completableFuture, flowSubscriber));
            }


            executors.submit(ack.orElseThrow());
        }
    }


    public void closeExecutors() {
        executors.close();
    }
}
