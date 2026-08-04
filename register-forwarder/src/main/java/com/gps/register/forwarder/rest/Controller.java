package com.gps.register.forwarder.rest;

import com.gps.register.forwarder.service.QueueProvider;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.dto.RegistrationDto;
import com.gps.shared.messages.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("gps")
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final BlockingQueue<String> requestQueue = QueueProvider.getRequestQueue();
    protected final Utils msgUtils = new Utils();

    @PostMapping(value = "register", consumes = "application/json")
    public ResponseEntity<Object> register(@RequestBody RegistrationDto registerMessage) {

        logger.debug("registerMessage: \n{}", registerMessage);

        ZonedDateTime dt = ZonedDateTime.now(ZoneId.of(Constants.WARSAW_TIME));

        String jSon = msgUtils.serializeMessage(registerMessage);

        logger.debug("jSon: \n{}", jSon);

        boolean added = requestQueue.offer(jSon);

        if (added) {
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
