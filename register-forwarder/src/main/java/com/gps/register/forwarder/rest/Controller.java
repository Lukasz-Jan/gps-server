package com.gps.register.forwarder.rest;

import com.gps.register.forwarder.service.QueueProvider;
import com.gps.shared.Constants;
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


    @RequestMapping(value = "register", method = RequestMethod.POST, consumes = "application/json")
    private ResponseEntity<Object> register(@RequestBody String json) {

        ZonedDateTime dt = ZonedDateTime.now(ZoneId.of(Constants.WARSAW_TIME));
        boolean added = requestQueue.offer(json);

        if (added) {
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
