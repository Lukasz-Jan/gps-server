package com.gps.position.services;

import com.gps.shared.documents.Account;
import com.gps.shared.messages.request.PositionMessage;
import com.gps.shared.messages.request.PositionMessageM;
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
public class PositionAdder {

    private static final Logger logger = LoggerFactory.getLogger(PositionAdder.class);
    private static final String POSITIONS_FIELD = "list";
    private static final String MAC_ADDR = "macAddress";
    private final MongoTemplate mongo;
    private final ResponseService responseService;
    private final Utils utils = new Utils();

    @Autowired
    public PositionAdder(MongoTemplate mongo, ResponseService responseService) {
        this.mongo = mongo;
        this.responseService = responseService;
    }

    public void save(String json) {

        Optional<PositionMessage> messageOpt = utils.deserializeToPosition(json);

        messageOpt.ifPresentOrElse(msg -> {

            PositionMessageM positionM = new PositionMessageM(msg.latidude(), msg.longitude(), msg.timestamp());
            UpdateResult updateResult = mongo
                            .updateFirst(query(where(MAC_ADDR).is(msg.macAddress())),
                                    createUpdate(positionM),
                                    Account.class);
                    sendResult(updateResult, msg);
                },
                () -> logger.error("Not valid positionmessage: {} , position not added ", json)
        );
    }

    private void sendResult(UpdateResult update, PositionMessage position) {

        if (update.wasAcknowledged()) {
            if (update.getModifiedCount() > 0) {
                responseService.send(position.macAddress(), PositionResult.OK);
                logger.debug("Position for {}  added", position.macAddress());
            } else {
                responseService.send(position.macAddress(), PositionResult.FAILED);
                logger.debug("Position for {}  not added", position.macAddress());
            }
        }
    }

    private Update createUpdate(PositionMessageM position) {
        return new Update().push(POSITIONS_FIELD).each(position);
    }
}
