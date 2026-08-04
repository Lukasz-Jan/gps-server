package com.gps.message.factory;

import com.gps.devices.message.factory.PositionMessageFactory;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PositionMessageFactoryTest {



    @Test
    void produce_random_register_messages() {

        PositionMessageFactory factory = new PositionMessageFactory();

        IntStream.rangeClosed(1, 100).forEach(i -> {
            String positionMessage = factory.createRandomPositionMessage();

            assertNotNull(positionMessage);
        });
    }
}