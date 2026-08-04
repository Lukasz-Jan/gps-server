package com.gps.devices.request.factory;

import com.gps.devices.message.factory.AbstractMessageFactory;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Objects;

public class Request {

    private static final Logger logger = LoggerFactory.getLogger(Request.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(1);
    private HttpRequest httpRequest;
    private String macAddress;

    private Request() {
    }

    private Request(Builder builder) {

        this.httpRequest = builder.httpRequest;
        this.macAddress = builder.macAddress;
    }

    public HttpRequest httpRequest() {
        return httpRequest;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public static class Builder {

        private AbstractMessageFactory messageFactory;
        private String macAddress;
        private String endPoint;
        private MediaType mediaType;
        private HttpRequest httpRequest;
        private String body;
        private boolean isRandomMessage;

        private Builder() {
        }

        public Builder(AbstractMessageFactory messageFactory) {
            this.messageFactory = messageFactory;
        }

        public Builder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setMacAddress(String macAddress) {
            this.macAddress = macAddress;
            return this;
        }

        public Builder setEndPoint(String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder isRandom(boolean createRandomMessage) {
            this.isRandomMessage = createRandomMessage;
            return this;
        }

        public Request build() {

            HttpRequest.BodyPublisher bodyPublisher = createPublisher(mediaType);
            try {

                httpRequest = HttpRequest
                        .newBuilder(new URI(endPoint))
                        .header(Constants.CONTENT_TYPE, getContentType())
                        .header("Accept", getContentType())
                        .timeout(TIMEOUT)
                        .POST(bodyPublisher)
                        .build();

                return new Request(this);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        private HttpRequest.BodyPublisher createPublisher(MediaType mediaType) {

            if (mediaType.equals(MediaType.TEXT_PLAIN)) {
                return createPublisherOfByteArray();
            } else {

                String json;
                if (Objects.nonNull(body)) {
                    json = body;
                }
                else if (isRandomMessage) {
                    Message message = messageFactory.createRandomMessage();
                    json = messageFactory.createJson(message);
                    macAddress = message.macAddress();
                } else {
                    json = messageFactory.createJson(macAddress);
                }

                logger.debug("produced json: \n{}", json);
                return HttpRequest.BodyPublishers.ofString(json);
            }
        }

        private HttpRequest.BodyPublisher createPublisherOfByteArray() {

            Message message =
                    messageFactory.createMessage(macAddress);

            byte[] bytes;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(message);
                bytes = bos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return HttpRequest.BodyPublishers.ofByteArray(bytes);
        }

        private String getContentType() {
            if (mediaType.equals(MediaType.TEXT_PLAIN)) {
                return Constants.TEXT_PLAIN;
            } else {
                return Constants.APP_JSON;
            }
        }
    }
}
