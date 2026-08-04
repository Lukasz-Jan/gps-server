package com.gps.position.services;

import com.gps.shared.messages.response.PositionResult;
import com.gps.shared.messages.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseService.class);

    private final String responseQueName;
    private final JmsTemplate jmsTemplate;
    private final Utils utils = new Utils();

    @Autowired
    public ResponseService(@Value("${positionResponseQueue}") String responseQueName, JmsTemplate jms) {
        this.responseQueName = responseQueName;
        this.jmsTemplate = jms;
    }

    public void send(String macAddress, PositionResult result) {

        String responseJson =
                utils.serializeToPositionResponse(macAddress, result);

        try {
            jmsTemplate.convertAndSend(responseQueName, responseJson);
            logger.debug("position ack for mac {}:  \n{}", macAddress, responseJson);
        } catch (JmsException e) {
            logger.error(e.getMessage());
            logger.error("jms exception");
        }
    }
}
