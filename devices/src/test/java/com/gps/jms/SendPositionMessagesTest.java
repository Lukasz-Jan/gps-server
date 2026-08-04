package com.gps.jms;

import com.gps.devices.message.factory.PositionMessageFactory;
import com.gps.shared.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SendPositionMessagesTest extends JmsTestBase {

    private static final Logger logger = LoggerFactory.getLogger(SendPositionMessagesTest.class);

    private static final PositionMessageFactory factory =
            new PositionMessageFactory();

    private static final String POSITION_QUE = "positionQueue";
    private ActiveMQConnectionFactory activeMQConnectionFactory;


    @BeforeAll
    void init() {
        activeMQConnectionFactory = new ActiveMQConnectionFactory(Constants.BROKER_URL);
    }

    @Test
    void send_position_once_for_random_mac() {
        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1).forEach(i -> {
            String positionMessage = factory.createPositionMessageJson(factory.createRandomMac());
            Assertions.assertDoesNotThrow(() -> convertAndSend(POSITION_QUE, positionMessage));
            logger.info("{}", positionMessage);
        });
    }

    @Test
    void having_fixed_mac_send_position_once() {
        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1).forEach(i -> {
            String positionMessage = factory.createPositionMessageJson(Constants.FIXED_MAC);
            Assertions.assertDoesNotThrow(() -> convertAndSend(POSITION_QUE, positionMessage));
            logger.info("{}", positionMessage);
        });
    }

    @Test
    void having_fixed_mac_send_many_positions() {
        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1_000).forEach(i -> {

            String positionMessage = factory.createPositionMessageJson(Constants.FIXED_MAC);
            Assertions.assertNotNull(positionMessage);
            Assertions.assertDoesNotThrow(() -> convertAndSend(POSITION_QUE, positionMessage));
            logger.debug("{}", positionMessage);
        });
    }

}
