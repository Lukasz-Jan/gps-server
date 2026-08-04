package com.gps.devices.message.transmit;

import com.gps.devices.message.RegisterRequestDataWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private static final HttpClient client = HttpClient.newHttpClient();
    private final ExecutorService executors = Executors.newFixedThreadPool(10);

    public void send(HttpRequest request) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> voidCompletableFuture = client

                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(httpResponse -> {
                            logger.info("{}",  httpResponse.statusCode());
                        }
                );

        voidCompletableFuture.get();
    }


    public void sendManyRequests(List<HttpRequest> requests) throws InterruptedException, ExecutionException {

        for(HttpRequest request: requests) {
            CompletableFuture<HttpResponse<String>> completableFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            Future<Boolean> submit = executors.submit(new Receiver(completableFuture));
            submit.get();
        }

    }

    public void sendMany(List<RegisterRequestDataWrapper> requests) throws InterruptedException,
            ExecutionException {

        for(RegisterRequestDataWrapper request: requests) {
            CompletableFuture<HttpResponse<String>> completableFuture = client.sendAsync(request.request(),
                    HttpResponse.BodyHandlers.ofString());
            Future<Boolean> submit = executors.submit(new Receiver(completableFuture));
            submit.get();
        }

    }


    public void closeExecutors() {
        executors.close();
    }
}
