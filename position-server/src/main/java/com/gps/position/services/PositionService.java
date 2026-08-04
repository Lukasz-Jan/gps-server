package com.gps.position.services;


import com.gps.shared.documents.Account;
import com.gps.shared.messages.request.dto.data.Position;
import com.gps.shared.messages.request.dto.PositionDto;
import com.gps.shared.messages.response.PositionResult;
import com.gps.shared.messages.utils.Utils;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class PositionService {

    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);
    private static final String POSITIONS_LIST = "list";
    private static final String MAC_ADDR = "macAddress";
    private final MongoTemplate mongo;
    private final ResponseService responseService;
    private final Utils utils = new Utils();

    @Autowired
    public PositionService(MongoTemplate mongo, ResponseService responseService) {
        this.mongo = mongo;
        this.responseService = responseService;
    }

    public void save(String json) {

        Optional<PositionDto> positionDto = utils.deserializeToPosition(json);

        positionDto.ifPresentOrElse(posDto -> {

                    Position positionData = new Position(posDto.latidude(), posDto.longitude(),
                            posDto.timestamp());

                    UpdateResult updateResult = mongo
                            .updateFirst(query(where(MAC_ADDR)
                                            .is(posDto.macAddress())),
                                    createUpdate(positionData),
                                    Account.class);

                    sendResult(updateResult, posDto);
                },
                () -> logger.debug("Not valid position message: {} , position not added ", json)
        );
    }

    private void sendResult(UpdateResult update, PositionDto position) {

        if (update.wasAcknowledged()) {
            if (update.getModifiedCount() > 0) {
                responseService.send(position.macAddress(), PositionResult.OK);
                logger.info("Position for {}  added", position.macAddress());
            } else {
                responseService.send(position.macAddress(), PositionResult.FAILED);
                logger.error("Position for {}  not added", position.macAddress());
            }
        }
    }

    private Update createUpdate(Position position) {
        return new Update().push(POSITIONS_LIST).each(position);
    }
}
