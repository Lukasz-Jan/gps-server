package com.gps.message.factory;

import com.gps.devices.message.factory.RegisterMessageFactory;
import com.gps.shared.messages.request.RegisterMessage;
import com.gps.shared.messages.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegisterMessageFactoryTest {

    private static final Utils msgUtils = new Utils();

    @Test
    void produce_random_register_messages() {

        RegisterMessageFactory factory = new RegisterMessageFactory();

        IntStream.rangeClosed(1, 100).forEach(i -> {
            RegisterMessage registerMessage =
                    factory.createRandomRegisterMessage();

            String register = msgUtils.serializeRegister(registerMessage);

            assertNotNull(register);
        });
    }
}