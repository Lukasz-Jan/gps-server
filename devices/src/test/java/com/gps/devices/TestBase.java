package com.gps.devices;

import com.gps.shared.Constants;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.params.provider.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestBase {

    protected static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    protected static Stream<Arguments> devicesAndPositionsQuantity_1() {
        return Stream.of(Arguments.of(10, 20));
    }

    protected static Stream<Arguments> devicesAndPositionsQuantity_2() {
        return Stream.of(Arguments.of(100, 30));
    }

    protected static Stream<Arguments> devicesAndPositionsQuantity_2_5() {
        return Stream.of(Arguments.of(1_000, 1_00));
    }

    protected static Stream<Arguments> devicesAndPositionsQuantity_3() {
        return Stream.of(Arguments.of(4_000, 1_00));
    }

    protected static Stream<Arguments> devicesAndPositionsQuantity_4() {
        return Stream.of(Arguments.of(51_000, 1_00));
    }

    protected MongoTemplate dbClient;

    void setUp() {
        dbClient = new MongoTemplate(new SimpleMongoClientDatabaseFactory(Constants.CONNECTION_MONGO));
    }

    protected void checkPositionsQuantityPerAddress(int noOfDevices, int positionsPerDevice) {

        MongoCollection<Document> accounts = dbClient.getCollection(Constants.GPS_DATA_COLLECTION);
        long noOfLists = accounts.countDocuments(new Document("list", new Document("$size", positionsPerDevice)));

        logger.debug("noOfLists: {} ", noOfLists);

        assertEquals(noOfDevices, noOfLists);
    }

    protected void checkRegistrationsQuantity(int devicesQuantity) {

        MongoCollection<Document> accounts = dbClient.getCollection(Constants.GPS_DATA_COLLECTION);
        long registrationsQuantity = accounts.countDocuments();
        assertEquals(devicesQuantity, registrationsQuantity);
    }
}
