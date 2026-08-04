package com.gps.position.respond.services;

import com.gps.shared.messages.response.PositionResponse;
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

        Optional<PositionResponse> positionResponse = utils.deserializeToPositionResponse(jmsMsg);

        positionResponse.ifPresentOrElse(response -> {
                    logger.debug("Sending position ACK to device, address {} ...", response.macAddress());
                },
                () -> logger.error("Error processing position from responsePosition queue")
        );
    }
}
