package com.gps.register.services;

import com.gps.shared.messages.response.RegisterResult;
import com.gps.shared.messages.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseService.class);

    private final String responseQueName;

    private final JmsTemplate jmsTemplate;

    private final Utils utils = new Utils();

    @Autowired
    public ResponseService(@Value("${registerResponseQueue}") String respQueName, JmsTemplate jmsTemplate) {
        this.responseQueName = respQueName;
        this.jmsTemplate = jmsTemplate;
        logger.info("register response que name: {}", respQueName);
    }

    public void sendResponse(String macAddress, RegisterResult result) {

        String responseToResponseQue =
                utils.serializeToRegisterResponse(macAddress, result);
        jmsTemplate.convertAndSend(responseQueName, responseToResponseQue);
        logger.debug("register ack for mac {} - {}", macAddress, result);
    }
}
