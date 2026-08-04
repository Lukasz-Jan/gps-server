package com.gps.jms;

import com.gps.devices.message.factory.PositionMessageFactory;
import com.gps.devices.message.factory.RegistrationMessageFactory;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.Message;
import com.gps.shared.messages.utils.Utils;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.jms.core.JmsTemplate;
import com.gps.shared.messages.request.dto.tests.RegistrationDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SendRegistersAndPositionsTest extends JmsTestBase {

    private static final PositionMessageFactory positionFactory =
            new PositionMessageFactory();
    private static final RegistrationMessageFactory registerFactory =
            new RegistrationMessageFactory();

    private final Utils msgUtils = new Utils();
    private final List<String> macAdresseses = new ArrayList<>();
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @BeforeAll
    void init() {
        activeMQConnectionFactory = new ActiveMQConnectionFactory(Constants.BROKER_URL);
    }

    @Test
    void send_register_and_position_once() {

        jms = new JmsTemplate(activeMQConnectionFactory);
        assertNotNull(jms);

        IntStream.rangeClosed(1, 1).forEach(i -> {

            Message registerMessage = registerFactory.createRandomMessage();

            macAdresseses.add(registerMessage.macAddress());

            String registerJson = msgUtils.serializeMessage(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
        });

        macAdresseses.forEach(mac -> {
            String positionMessage = positionFactory.createPositionMessageJson(mac);
            convertAndSend(Constants.POSITION_QUE, positionMessage);
        });
    }

    @Test
    void send_register_and_positions_fixed_mac() {

        jms = new JmsTemplate(activeMQConnectionFactory);
        assertNotNull(jms);

        IntStream.rangeClosed(1, 1).forEach(i -> {

            RegistrationDto registrationDto = registerFactory.createRandomRegisterMessage(Constants.FIXED_MAC);
            macAdresseses.add(registrationDto.macAddress());
            String registerJson = msgUtils.serializeRegister(registrationDto);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
        });

        macAdresseses.forEach(mac -> {
            for (int i = 0; i < 10; i++) {
                String positionMessage = positionFactory.createPositionMessageJson(Constants.FIXED_MAC);
                convertAndSend(Constants.POSITION_QUE, positionMessage);

            }
        });
    }

    @Test
    void send_register_and_many_positions() {

        jms = new JmsTemplate(activeMQConnectionFactory);
        assertNotNull(jms);

        IntStream.rangeClosed(1, 1).forEach(i -> {

            RegistrationDto registerMessage = (RegistrationDto) registerFactory.createRandomMessage();
            macAdresseses.add(registerMessage.macAddress());
            String registerJson = msgUtils.serializeMessage(registerMessage);
            assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE, registerJson));
        });

        for (int i = 0; i < 10; i++) {
            String positionMessage =
                    positionFactory.createPositionMessageJson(macAdresseses.get(0));

            assertDoesNotThrow(() -> convertAndSend(Constants.POSITION_QUE, positionMessage));
        }
    }

    @Test
    void send_registers_and_many_positions() {

        jms = new JmsTemplate(activeMQConnectionFactory);

        for (int i = 0; i < 100; i++) {

            RegistrationDto registerMessage = (RegistrationDto) registerFactory.createRandomMessage();
            macAdresseses.add(registerMessage.macAddress());
            String registerJson = msgUtils.serializeMessage(registerMessage);
            assertDoesNotThrow(() -> convertAndSend(Constants.REGISTER_QUE, registerJson));
        }

        macAdresseses.forEach(mac -> {

            for (int i = 0; i < 100; i++) {
                String positionMessage =
                        positionFactory.createPositionMessageJson(mac);
                convertAndSend(Constants.POSITION_QUE, positionMessage);
            }
        });
    }

    @AfterAll
    void end() {
        macAdresseses.clear();
    }
}
