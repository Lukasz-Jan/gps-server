package com.gps.register.services;

import com.gps.shared.Constants;
import com.gps.shared.documents.Account;
import com.gps.shared.messages.request.dto.RegistrationDto;
import com.gps.shared.messages.response.RegisterResult;
import com.gps.shared.messages.utils.Utils;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
public class RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private final MongoTemplate mongo;
    private final ResponseService responseService;
    private final Utils utils;

    @Autowired
    public RegisterService(MongoTemplate mongo, ResponseService responseService,
            Utils utils) {
        this.mongo = mongo;
        this.responseService = responseService;
        this.utils = utils;
    }

    public void register(String json) {

        Optional<RegistrationDto> message = utils.deserialize(json, RegistrationDto.class);

        message
                .ifPresentOrElse(regDto -> {
                            UpdateResult upsertResult = null;
                            try {
                                upsertResult = mongo.upsert(createQuery(regDto), createUpdate(regDto), Account.class);
                            } catch (DataAccessException e) {
                                logger.error("database exception during registration upsert", e);
                            }
                            sendResult(upsertResult, regDto);
                        }
                        ,
                        () -> sendParsingFailure(json)
                );
    }

    private Query createQuery(RegistrationDto registerMessage) {
        return new Query()
                .addCriteria(Criteria.where("macAddress").is(registerMessage.macAddress()));
    }

    private Update createUpdate(RegistrationDto registerMessage) {
        return new Update()
                .setOnInsert("macAddress", registerMessage.macAddress())
                .set("deviceName", registerMessage.name())
                .set("owner", registerMessage.owner())
                .set("deviceType", registerMessage.deviceType())
                .setOnInsert("list", Collections.emptyList());
    }

    private void sendResult(UpdateResult updateResult, RegistrationDto msg) {

        if (Objects.nonNull(updateResult)) {
            if (updateResult.wasAcknowledged()) {

                logger.debug("modified count: {} ", updateResult.getModifiedCount());
                logger.debug("matched count:  {} ", updateResult.getMatchedCount());
                logger.debug("upserted id:    {} ", updateResult.getUpsertedId());

                if (Objects.nonNull(updateResult.getUpsertedId())) {

                    logger.info("device {} registered", msg.macAddress());
                    responseService.sendResponse(msg.macAddress(), RegisterResult.OK);
                } else if (modified(updateResult)) {

                    logger.info("device {} modified", msg.macAddress());
                    responseService.sendResponse(msg.macAddress(), RegisterResult.MODIFIED);
                } else if (notModified(updateResult)) {

                    logger.info("device {} not modified", msg.macAddress());
                    responseService.sendResponse(msg.macAddress(), RegisterResult.NOT_MODIFIED);
                }
                else {

                    logger.info("unexpected result for {}", msg.macAddress());
                    sendFailure(msg.macAddress());
                }
            } else {
                logger.error("register for {} not acknowledged in db", msg.macAddress());
                sendFailure(msg.macAddress());
            }
        }
        else {
            logger.error("register for {} db failure", msg.macAddress());
            sendFailure(msg.macAddress());
        }
    }

    private boolean notModified(UpdateResult updateResult) {
        return (updateResult.getModifiedCount() == 0 && updateResult.getMatchedCount() > 0);
    }

    private boolean modified(UpdateResult updateResult) {
        return (updateResult.getModifiedCount() > 0 && updateResult.getMatchedCount() > 0);
    }

    private void sendParsingFailure(String json) {
        logger.error("Registration message not parsable {}", json);
        responseService.sendResponse(Constants.UNPARSED_SENTINEL, RegisterResult.FAILED);
    }

    private void sendFailure(String address) {
        logger.error("Registration for {} failed", address);
        responseService.sendResponse(address, RegisterResult.FAILED);
    }
}
