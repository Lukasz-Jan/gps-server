package com.gps.shared.messages.utils;

import com.gps.shared.messages.request.PositionMessage;
import com.gps.shared.messages.request.RegisterMessage;
import com.gps.shared.messages.response.PositionResponse;
import com.gps.shared.messages.response.PositionResult;
import com.gps.shared.messages.response.RegisterResponse;
import com.gps.shared.messages.response.RegisterResult;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Utils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serializeRegister(RegisterMessage registerMessage) {
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(registerMessage);
    }

    public String serializePosition(PositionMessage registerMessage) {
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(registerMessage);
    }

    public String serializeToRegisterResponse(String mac, RegisterResult result) {
        RegisterResponse registerResponse = new RegisterResponse(mac, result);
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(registerResponse);
    }

    public String serializeToPositionResponse(String mac, PositionResult result) {
        PositionResponse positionResponse = new PositionResponse(mac, result);
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(positionResponse);
    }

    public Optional<RegisterResponse> deserializeJmsMsg(String jmsRegisterResponseMsg) {
        try {
            RegisterResponse registerResponse = objectMapper.readValue(jmsRegisterResponseMsg, RegisterResponse.class);
            return Optional.ofNullable(registerResponse);
        } catch (JacksonException e) {
            return Optional.empty();
        }
    }

    public Optional<PositionMessage> deserializeToPosition(String json) {
        try {
            PositionMessage positionMessage = objectMapper.readValue(json, PositionMessage.class);
            return Optional.ofNullable(positionMessage);
        } catch (JacksonException e) {
            return Optional.empty();
        }
    }

    public Optional<RegisterMessage> deserializeJsonMsg(String json, Class<RegisterMessage> cl) {
        try {
            RegisterMessage registerMessage = objectMapper.readValue(json, cl);
            return Optional.ofNullable(registerMessage);
        } catch (JacksonException e) {
            return Optional.empty();
        }
    }

    public Optional<PositionResponse> deserializeToPositionResponse(String jmsRegisterResponseMsg) {
        try {
            PositionResponse registerResponse = objectMapper.readValue(jmsRegisterResponseMsg, PositionResponse.class);
            return Optional.ofNullable(registerResponse);
        } catch (JacksonException e) {
            return Optional.empty();
        }
    }
}
