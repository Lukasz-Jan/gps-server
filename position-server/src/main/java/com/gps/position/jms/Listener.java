package com.gps.position.jms;

import com.gps.position.services.PositionAdder;
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
    private final PositionAdder postionSaver;
    private final ResponseService responseService;

    @Autowired
    public Listener(PositionAdder postionService, ResponseService responseService) {
        this.postionSaver = postionService;
        this.responseService = responseService;
    }

    @JmsListener(destination = "${positionQueue}")
    @Override
    public void onMessage(Message jmsMessage) {

        Optional<String> position = Optional.empty();

        if (jmsMessage instanceof TextMessage txtMessage) {
            try {
                position = Optional.ofNullable(txtMessage.getText());
            } catch (JMSException e) {
                logger.debug("JMS exception for position msg: \n {}", txtMessage);
            }
        }

        postionSaver.save(position.get());
    }}
