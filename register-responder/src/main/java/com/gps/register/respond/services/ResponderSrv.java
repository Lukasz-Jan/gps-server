package com.gps.register.respond.services;

import com.gps.shared.messages.response.RegisterResponse;
import com.gps.shared.messages.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResponderSrv {

    private static final Logger logger = LoggerFactory.getLogger(ResponderSrv.class);
    private final Utils utils = new Utils();

    public void send(String jmsMsg) {

        Optional<RegisterResponse> registerResponse = utils.deserializeJmsMsg(jmsMsg);

        registerResponse.ifPresentOrElse(response -> {
                    logger.debug("Sending register ACK to device, address {} ", response.macAddress());
                },
                () -> logger.error("Error processing response from response queue")
        );
    }
}
