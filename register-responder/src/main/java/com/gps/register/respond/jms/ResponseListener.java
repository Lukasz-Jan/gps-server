package com.gps.register.respond.jms;

import com.gps.register.respond.services.ResponderSrv;
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
public class ResponseListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ResponseListener.class);
    private final ResponderSrv responderService;

    @Autowired
    public ResponseListener(ResponderSrv responderService) {
        this.responderService = responderService;
    }

    @JmsListener(destination = "${registerResponseQueue}")
    @Override
    public void onMessage(Message jmsMessage) {

        Optional<String> registerText = Optional.empty();

        if (jmsMessage instanceof TextMessage txtMessage) {
            try {
                registerText = Optional.ofNullable(txtMessage.getText());
            } catch (JMSException e) {
                logger.error("JMS exception for forwarder response");
            }
        }
        registerText
                .ifPresentOrElse(jmsMsg -> {

                            logger.debug("received : \n{}", jmsMsg);
                            responderService.send(jmsMsg);
                        }
                        ,
                        () -> logger.error("error receiving from registration response queue")
                );
    }
}
