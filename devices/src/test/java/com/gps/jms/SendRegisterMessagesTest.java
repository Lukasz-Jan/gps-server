package com.gps.jms;

import com.gps.devices.message.factory.RegistrationMessageFactory;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.Message;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SendRegisterMessagesTest extends JmsTestBase {

    private static final Logger logger = LoggerFactory.getLogger(SendRegisterMessagesTest.class);
    private ActiveMQConnectionFactory activeMQConnectionFactory;
    private static final Utils msgUtils = new Utils();

    @BeforeAll
    void init() {
        activeMQConnectionFactory = new ActiveMQConnectionFactory(Constants.BROKER_URL);
        jms = new JmsTemplate(activeMQConnectionFactory);
    }

    @Test
    void send_register_once() {

        RegistrationMessageFactory factory = new RegistrationMessageFactory();

        IntStream.rangeClosed(1, 1).forEach(i -> {

            Message registerMessage = factory.createRandomMessage();
            String registerJson = msgUtils.serializeMessage(registerMessage);
            assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE, registerJson));
        });
    }

    @Test
    void having_fixed_mac_send_register_once() {

        RegistrationMessageFactory factory = new RegistrationMessageFactory();
        assertNotNull(factory);

        IntStream.rangeClosed(1, 1).forEach(i -> {
            com.gps.shared.messages.request.dto.tests.RegistrationDto registerMessage = factory.createRandomRegisterMessage(Constants.FIXED_MAC);
            String registerJson = msgUtils.serializeRegister(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
            assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE, registerJson));
            logger.info("{}", registerJson);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    void send_register_n_times(int n) {

        RegistrationMessageFactory factory = new RegistrationMessageFactory();

        IntStream.rangeClosed(1, n).forEach(i -> {
            Message registerMessage = factory.createRandomMessage();

            String registerJson = msgUtils.serializeMessage(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
            assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE, registerJson));
        });
    }
}
