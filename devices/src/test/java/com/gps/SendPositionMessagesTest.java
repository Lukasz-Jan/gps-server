package com.gps;

import com.gps.devices.message.factory.PositionMessageFactory;
import com.gps.shared.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SendPositionMessagesTest extends BaseTest{

    private static final Logger logger = LoggerFactory.getLogger(SendPositionMessagesTest.class);

    private static final PositionMessageFactory factory =
            new PositionMessageFactory();

    private final String POSITION_QUE = "positionQueue";
    private ActiveMQConnectionFactory activeMQConnectionFactory;


    @BeforeAll
    public void init() {
        activeMQConnectionFactory = new ActiveMQConnectionFactory(Constants.BROKER_URL);
    }

    @Test
    public void send_position_once_for_random_mac() {
        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1).forEach(i -> {
            String positionMessage = factory.createPositionMessage(factory.createMac());
            convertAndSend(POSITION_QUE, positionMessage);
             logger.info("{}", positionMessage);
        });
    }

    @Test
    public void having_mac_given_send_position_once() {
        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1).forEach(i -> {
            String positionMessage = factory.createPositionMessage(Constants.FIXED_MAC);
            convertAndSend(POSITION_QUE, positionMessage);
            logger.info("{}", positionMessage);
        });
    }

    @Test
    public void having_fixed_mac_send_position_once() {
        jms = new JmsTemplate(activeMQConnectionFactory);
        IntStream.rangeClosed(1, 1).forEach(i -> {
            String positionMessage = factory.createPositionMessage(Constants.FIXED_MAC);
            convertAndSend(POSITION_QUE, positionMessage);
             logger.info("{}", positionMessage);
        });
    }

    @Test
    public void having_fixed_mac_send_many_positions() {
        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1_000).forEach(i -> {
            String positionMessage = factory.createPositionMessage(Constants.FIXED_MAC);
            convertAndSend(POSITION_QUE, positionMessage);
            logger.debug("{}", positionMessage);
        });
    }

}
