package com.gps.position.forwarder.rest;

import com.gps.position.forwarder.service.QueueProvider;
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
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    private final BlockingQueue<String> positionsQueue = QueueProvider.getPositionsQueue();

    @RequestMapping(value = "position", method = RequestMethod.POST, consumes = "application/json")
    private ResponseEntity<Object> registerPosition(@RequestBody String positionJson) {

        ZonedDateTime dt = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));

        boolean added = positionsQueue.offer(positionJson);

        if (added) {
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
