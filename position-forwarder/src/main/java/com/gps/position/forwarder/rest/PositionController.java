package com.gps.position.forwarder.rest;

import com.gps.position.forwarder.service.QueueProvider;
import com.gps.shared.messages.request.dto.PositionDto;
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
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    private final BlockingQueue<String> positionsQueue = QueueProvider.getPositionsQueue();
    protected final Utils msgUtils = new Utils();

    @PostMapping(value = "position", consumes = "application/json")
    public ResponseEntity<Object> registerPosition(@RequestBody PositionDto positionMessage) {

        logger.debug("registerMessage: \n{}", positionMessage);

        ZonedDateTime dt = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));

        String positionJson = msgUtils.serializeMessage(positionMessage);

        boolean added = positionsQueue.offer(positionJson);

        if (added) {
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.OK);
        } else {
            logger.error("Too many requests ");
            return new ResponseEntity<>(dt.toLocalDateTime(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
