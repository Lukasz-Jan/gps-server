package com.gps.shared.messages.utils;

import com.gps.shared.messages.request.Message;
import com.gps.shared.messages.request.dto.PositionDto;
import com.gps.shared.messages.request.dto.tests.RegistrationDto;
import com.gps.shared.messages.response.PositionResponse;
import com.gps.shared.messages.response.PositionResult;
import com.gps.shared.messages.response.RegisterResponse;
import com.gps.shared.messages.response.RegisterResult;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

public class Utils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serializeMessage(Message message) {
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(message);
    }

    public String serializeRegister(RegistrationDto registerMessage) {
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

    public Optional<PositionDto> deserializeToPosition(String json) {
        try {
            PositionDto positionDto = objectMapper.readValue(json, PositionDto.class);
            return Optional.ofNullable(positionDto);
        } catch (JacksonException e) {
            return Optional.empty();
        }
    }

    public Optional<com.gps.shared.messages.request.dto.RegistrationDto> deserializeJsonMsg(String json, Class<com.gps.shared.messages.request.dto.RegistrationDto> cl) {
        try {
            com.gps.shared.messages.request.dto.RegistrationDto registerMessage = objectMapper.readValue(json, cl);
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
