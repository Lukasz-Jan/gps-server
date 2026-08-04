package com.gps.jms;

import com.gps.devices.message.factory.RegistrationMessageFactory;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.Message;
import com.gps.shared.messages.utils.Utils;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jms.core.JmsTemplate;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SendRegisterMessagesTest extends JmsTestBase {

    private static final Logger logger = LoggerFactory.getLogger(SendRegisterMessagesTest.class);
    private ActiveMQConnectionFactory activeMQConnectionFactory;
    private static final Utils msgUtils = new Utils();
    private MongoTemplate dbClient;

    @BeforeAll
    void init() {
        activeMQConnectionFactory = new ActiveMQConnectionFactory(Constants.BROKER_URL);
        jms = new JmsTemplate(activeMQConnectionFactory);
        dbClient = new MongoTemplate(new SimpleMongoClientDatabaseFactory(Constants.CONNECTION_MONGO));
    }

    @BeforeEach
    void setUp() {
        try {
            dbClient.remove(new Query(), Constants.GPS_DATA_COLLECTION);
        } catch (Exception e) {
            fail("no database connection");
        }
    }

    @Test
    void send_random_registers_x_times() {
        int x = 3;
        RegistrationMessageFactory factory = new RegistrationMessageFactory();
        IntStream.rangeClosed(1, x).forEach(i -> {
            Message registerMessage = factory.createRandomMessage();
            String registerJson = msgUtils.serializeMessage(registerMessage);
            assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE_NAME, registerJson));
        });
    }

    @Test
    void having_fixed_mac_send_register_and_register_update_x_times() {

        int x = 3;
        RegistrationMessageFactory factory = new RegistrationMessageFactory();
        assertNotNull(factory);
        String address = "ab:bb:aa:01:02:03";

        com.gps.shared.messages.request.dto.tests.RegistrationDto registerMsg = factory.createRandomRegisterMessage(address);
        String register = msgUtils.serializeRegister(registerMsg);
        assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE_NAME, register));
        logger.info("{}", register);

        IntStream.rangeClosed(1, x).forEach(i -> {
            com.gps.shared.messages.request.dto.tests.RegistrationDto updateMsg = factory.createRandomRegisterMessage(address);
            String registerUpdate = msgUtils.serializeRegister(updateMsg);
            assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE_NAME, registerUpdate));
            logger.info("{}", registerUpdate);
        });
    }

    @Test
    void send_same_register_x_times() {
        int x = 3;
        RegistrationMessageFactory factory = new RegistrationMessageFactory();
        assertNotNull(factory);
        String address = "ab:bb:aa:01:01:01";

        com.gps.shared.messages.request.dto.tests.RegistrationDto registerMsg = factory.createRandomRegisterMessage(address);
        String register = msgUtils.serializeRegister(registerMsg);

        IntStream.rangeClosed(1, x).forEach(i -> {
                    assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE_NAME, register));
                }
        );

        logger.info("{}", register);
    }
}
