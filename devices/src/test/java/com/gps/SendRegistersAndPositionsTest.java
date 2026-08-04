package com.gps;

import com.gps.devices.message.factory.PositionMessageFactory;
import com.gps.devices.message.factory.RegisterMessageFactory;
import com.gps.shared.Constants;
import com.gps.shared.messages.request.RegisterMessage;
import com.gps.shared.messages.utils.Utils;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SendRegistersAndPositionsTest extends BaseTest {

    private static final PositionMessageFactory positionFactory =
            new PositionMessageFactory();
    private static final RegisterMessageFactory registerFactory =
            new RegisterMessageFactory();

    private final Utils msgUtils = new Utils();
    private final List<String> macAdresseses = new ArrayList<>();
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @BeforeAll
    public void init() {
        activeMQConnectionFactory = new ActiveMQConnectionFactory(Constants.BROKER_URL);
    }

    @Test
    public void send_register_and_position_once() {

        try {
            jms = new JmsTemplate(activeMQConnectionFactory);

            IntStream.rangeClosed(1, 1).forEach(i -> {

                RegisterMessage registerMessage = registerFactory.createRandomRegisterMessage();
                macAdresseses.add(registerMessage.macAddress());
                String registerJson = msgUtils.serializeRegister(registerMessage);
                convertAndSend(Constants.REGISTER_QUE, registerJson);
            });

            macAdresseses.forEach(mac -> {
                String positionMessage = positionFactory.createPositionMessage(mac);
                convertAndSend(Constants.POSITION_QUE, positionMessage);

            });
        } catch (Exception e) {
            fail("should not be here");
        }
    }

    @Test
    public void send_register_and_positions_fixed_mac() {

        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1).forEach(i -> {

            RegisterMessage registerMessage = registerFactory.createRandomRegisterMessage(Constants.FIXED_MAC);
            macAdresseses.add(registerMessage.macAddress());
            String registerJson = msgUtils.serializeRegister(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
        });

        macAdresseses.forEach(mac -> {
            for (int i = 0; i < 10; i++) {
                String positionMessage = positionFactory.createPositionMessage(Constants.FIXED_MAC);
                convertAndSend(Constants.POSITION_QUE, positionMessage);

            }
        });
    }

    @Test
    public void send_register_and_many_positions() {

        jms = new JmsTemplate(activeMQConnectionFactory);

        IntStream.rangeClosed(1, 1).forEach(i -> {

            RegisterMessage registerMessage = registerFactory.createRandomRegisterMessage();
            macAdresseses.add(registerMessage.macAddress());
            String registerJson = msgUtils.serializeRegister(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
        });

        for (int i = 0; i < 10; i++) {
            String positionMessage =
                    positionFactory.createPositionMessage(macAdresseses.get(0));
            convertAndSend(Constants.POSITION_QUE, positionMessage);

        }
    }

    @Test
    public void send_registers_and_many_positions() {

        jms = new JmsTemplate(activeMQConnectionFactory);

        for (int i = 0; i < 100; i++) {
            RegisterMessage registerMessage = registerFactory.createRandomRegisterMessage();
            macAdresseses.add(registerMessage.macAddress());
            String registerJson = msgUtils.serializeRegister(registerMessage);
            convertAndSend(Constants.REGISTER_QUE, registerJson);
        }

        macAdresseses.forEach(mac -> {

            for (int i = 0; i < 100; i++) {
                String positionMessage =
                        positionFactory.createPositionMessage(mac);
                convertAndSend(Constants.POSITION_QUE, positionMessage);
            }
        });
    }

    @AfterAll
    public void end() {
        macAdresseses.clear();
    }
}
