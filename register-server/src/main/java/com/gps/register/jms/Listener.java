package com.gps.register.jms;

import com.gps.register.services.RegisterService;
import com.gps.register.services.ResponseService;
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
    private final RegisterService registerService;


    @Autowired
    public Listener(RegisterService registerService, ResponseService responseService) {
        this.registerService = registerService;
    }

    @JmsListener(destination = "${registerQueue}")
    @Override
    public void onMessage(Message jmsMessage) {

        Optional<String> jsonRegisterOpt = Optional.empty();

        if (jmsMessage instanceof TextMessage txtMessage) {
            try {
                jsonRegisterOpt = Optional.ofNullable(txtMessage.getText());
            } catch (JMSException e) {
                logger.error("JMS exception for register msg: \n {}", txtMessage);
            }
        }

        jsonRegisterOpt
                .ifPresent(t -> {
                            logger.debug("received message: \n{}", t);
                            registerService.register(t);
                        }
                );
    }
}
