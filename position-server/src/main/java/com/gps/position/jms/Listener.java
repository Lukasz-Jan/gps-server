package com.gps.position.jms;

import com.gps.position.services.PositionService;
import com.gps.position.services.ResponseService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Listener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(Listener.class);
    private final PositionService postionSaver;

    @Autowired
    public Listener(PositionService postionService, ResponseService responseService) {
        this.postionSaver = postionService;
    }

    @JmsListener(destination = "${positionQueue}")
    @Override
    public void onMessage(Message jmsMessage) {

        Optional<String> positionOpt = Optional.empty();

        if (jmsMessage instanceof TextMessage txtMessage) {
            try {
                positionOpt = Optional.ofNullable(txtMessage.getText());
            } catch (JMSException e) {
                logger.error("JMS exception for position msg: \n {}", txtMessage);
            }
        }

        positionOpt.ifPresent(postionSaver::save);
    }}
