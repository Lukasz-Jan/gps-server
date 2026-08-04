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

public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    protected MongoTemplate mongo = new MongoTemplate(new SimpleMongoClientDatabaseFactory(Constants.CONNECTION_MONGO));

    protected static Stream<Arguments> noOfDevicesAndPositions_1() {
        return Stream.of(Arguments.of(10, 20));
    }

    protected static Stream<Arguments> noOfDevicesAndPositions_2() {
        return Stream.of(Arguments.of(100, 30));
    }

    protected static Stream<Arguments> noOfDevicesAndPositions_2_5() {
        return Stream.of(Arguments.of(1_000, 1_00));
    }

    protected static Stream<Arguments> noOfDevicesAndPositions_3() {
        return Stream.of(Arguments.of(4_000, 1_00));
    }

    protected static Stream<Arguments> noOfDevicesAndPositions_4() {
        return Stream.of(Arguments.of(21_000, 1_00));
    }

    protected void checkCollection(int noOfDevices, int positionsPerDevice) {

        MongoCollection<Document> accounts = mongo.getCollection(Constants.GPS_DATA_COLLECTION);
        long noOfLists = accounts.countDocuments(new Document("list", new Document("$size", positionsPerDevice)));

        logger.debug("noOfLists: " + noOfLists);

        assertEquals(noOfDevices, noOfLists);
    }
}
