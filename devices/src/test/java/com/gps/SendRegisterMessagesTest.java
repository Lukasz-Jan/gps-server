package com.gps;

import com.gps.devices.message.factory.RegisterMessageFactory;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.RegisterMessage;
import com.gps.shared.messages.utils.Utils;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SendRegisterMessagesTest extends BaseTest{

    private static final Logger logger = LoggerFactory.getLogger(SendRegisterMessagesTest.class);
    private ActiveMQConnectionFactory activeMQConnectionFactory;
    private static final Utils msgUtils = new Utils();

    @BeforeAll
    public void init() {
        activeMQConnectionFactory = new ActiveMQConnectionFactory(Constants.BROKER_URL);
        jms = new JmsTemplate(activeMQConnectionFactory);
    }

    @Test
    public void send_register_once(){

        RegisterMessageFactory factory = new RegisterMessageFactory();

        IntStream.rangeClosed(1, 1).forEach(i -> {
            RegisterMessage registerMessage = factory.createRandomRegisterMessage();
            String registerJson = msgUtils.serializeRegister(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
        });
    }

    @Test
    public void having_fixed_mac_send_register_once() {

        RegisterMessageFactory factory = new RegisterMessageFactory();

        IntStream.rangeClosed(1, 1).forEach(i -> {
            RegisterMessage registerMessage = factory.createRandomRegisterMessage(Constants.FIXED_MAC);
            String registerJson = msgUtils.serializeRegister(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
            logger.info("{}", registerJson);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void send_register_n_times(int n) {

        RegisterMessageFactory factory = new RegisterMessageFactory();

        IntStream.rangeClosed(1, n).forEach(i -> {
            RegisterMessage registerMessage = factory.createRandomRegisterMessage();

            String registerJson = msgUtils.serializeRegister(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
        });
    }
}
