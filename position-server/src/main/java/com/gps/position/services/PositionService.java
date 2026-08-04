package com.gps.position.services;

import com.gps.shared.documents.Account;
import com.gps.shared.messages.request.dto.data.Position;
import com.gps.shared.messages.request.dto.PositionDto;
import com.gps.shared.messages.response.PositionReceiveResult;
import com.gps.shared.messages.utils.Utils;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class PositionService {

    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);
    private static final String POSITIONS_LIST = "list";
    private static final int MAX_POSITIONS = 1000;
    private static final String MAC_ADDR = "macAddress";
    // invalid payload contract
    private static final String FAIL_SENTINEL = "JSON_UNPARSED";
    private final MongoTemplate mongo;
    private final ResponseService responseService;
    private final Utils utils;

    @Autowired
    public PositionService(MongoTemplate mongo, ResponseService responseService, Utils utils) {
        this.mongo = mongo;
        this.responseService = responseService;
        this.utils = utils;
    }

    public void save(String json) {

        Optional<PositionDto> positionDto = utils.deserializeToPosition(json);

        positionDto.ifPresentOrElse(posDto -> {

                    Position positionData = mapToPosition(posDto);

                    UpdateResult updateResult = null;
                    try {
                        updateResult = mongo
                                .updateFirst(
                                        query(where(MAC_ADDR).is(posDto.macAddress())),
                                        createUpdate(positionData),
                                        Account.class
                                );
                    } catch (DataAccessException e) {
                        logger.error("add position exc", e);
                    }
                    sendResult(updateResult, posDto);
                },
                () -> sendFailure(json,"")
        );
    }

    private void sendResult(UpdateResult updateResult, PositionDto position) {

        if (Objects.nonNull(updateResult)) {

            if (updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0) {
                responseService.send(position.macAddress(), PositionReceiveResult.OK);
                logger.debug("Position for {} added", position.macAddress());
            } else {
                logger.error("matched count {}", updateResult.getMatchedCount());
                logger.error("modified count {}", updateResult.getModifiedCount());
                sendFailure("", position.macAddress());
            }
        } else {
            sendFailure("", position.macAddress());
        }
    }

    private Update createUpdate(Position position) {
        return new Update()
                .push(POSITIONS_LIST)
                .slice(-MAX_POSITIONS)
                .each(position);
    }

    private void sendFailure(String json, String address) {

        if (!address.isEmpty()) {
            logger.error("Position for {} not added", address);
            responseService.send(address, PositionReceiveResult.FAILED);
        } else {
            logger.error("Not valid position message: {} , position not added ", json);
            responseService.send(FAIL_SENTINEL, PositionReceiveResult.FAILED);
        }
    }

    private Position mapToPosition(PositionDto posDto) {
        return new Position(posDto.latitude(), posDto.longitude(),
                posDto.timestamp());
    }
}
