package com.gps.register.services;

import com.gps.shared.documents.Account;
import com.gps.shared.messages.request.RegisterMessage;
import com.gps.shared.messages.response.RegisterResult;
import com.gps.shared.messages.utils.Utils;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Optional;

@Service
public class RegisterService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
    private static final String ACCOUNTS_COLLECTION = "gpsAccounts";
    private final MongoTemplate mongo;
    private final ResponseService responseService;
    private final Utils utils = new Utils();

    @Autowired
    public RegisterService(MongoTemplate mongo, ResponseService responseService) {
        this.mongo = mongo;
        this.responseService = responseService;
    }

    public void register(String json) {

        Optional<RegisterMessage> messageOpt = utils.deserializeJsonMsg(json, RegisterMessage.class);

        messageOpt.ifPresentOrElse(msg -> {

                    UpdateResult upsert = mongo.upsert(createQuery(msg),
                            createUpdate(msg), Account.class);

                    logger.debug("Register for {}  added to {} collection", msg.macAddress(), ACCOUNTS_COLLECTION);

                    sendResult(upsert, msg);
                }
                ,
                () -> logger.error("Not valid message {}  failed, not registered", json)
        );


    }

    private void sendResult(UpdateResult upsert, RegisterMessage registerMessage) {

        if (upsert.wasAcknowledged()) {
            if (upsert.getModifiedCount() > 0) {
                responseService.sendResponse(registerMessage.macAddress(), RegisterResult.ALREADY_REGISTERED);
            } else {
                responseService.sendResponse(registerMessage.macAddress(), RegisterResult.OK);
            }
        } else {
            responseService.sendResponse(registerMessage.macAddress(), RegisterResult.FAILED);
        }
    }

    private Query createQuery(RegisterMessage registerMessage) {
        return new Query().addCriteria(Criteria.where("macAddress").is(registerMessage.macAddress()));
    }

    private Update createUpdate(RegisterMessage registerMessage) {
        return new Update()
                .setOnInsert("macAddress", registerMessage.macAddress())
                .setOnInsert("deviceName", registerMessage.name())
                .setOnInsert("owner", registerMessage.owner())
                .setOnInsert("deviceType", registerMessage.deviceType())
                .setOnInsert("list", Collections.EMPTY_LIST);
    }
}
