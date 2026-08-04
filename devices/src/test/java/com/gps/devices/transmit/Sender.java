package com.gps.devices.transmit;

import com.gps.devices.message.RegistrationRequestDataWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;

public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private static final HttpClient client = HttpClient.newHttpClient();
    private final ExecutorService executors = Executors.newFixedThreadPool(4);

    public void send(HttpRequest request) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> voidCompletableFuture = client

                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(httpResponse -> {
                            logger.info("{}",  httpResponse.statusCode());
                        }
                );

        voidCompletableFuture.get();
    }


    public void sendRegistersRequests(List<HttpRequest> requests) throws ExecutionException, InterruptedException {

        for(HttpRequest request: requests) {
            CompletableFuture<HttpResponse<String>> completableFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            Future<Boolean> submit = executors.submit(new PositionAck(completableFuture));
            submit.get();
        }
    }

    public void sendRegistrations(List<RegistrationRequestDataWrapper> requests) {

        for(RegistrationRequestDataWrapper requestWrapper: requests) {

            HttpRequest httpRequest = requestWrapper.request();

            HttpRequest.BodyPublisher bodyPublisher = httpRequest.bodyPublisher().get();
            var bodySubscriber = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
            var flowSubscriber = new StringSubscriber(bodySubscriber);
            bodyPublisher.subscribe(flowSubscriber);

            CompletableFuture<HttpResponse<String>> completableFuture = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            RegistrationAck registrationAck = new RegistrationAck(completableFuture, flowSubscriber);

            executors.submit(registrationAck);
        }
    }

    public void closeExecutors() {
        executors.close();
    }
}
