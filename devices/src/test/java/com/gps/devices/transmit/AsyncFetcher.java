package com.gps.devices.transmit;

import com.gps.shared.Constants;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.Callable;

public class AsyncFetcher<T> implements Callable<T> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncFetcher.class);
    private final MongoTemplate dbClient;
    private final FetchMode fetchMode;
    private final int requiredQuantity;

    public AsyncFetcher(MongoTemplate dbClient, FetchMode fetchMode, int fixedPositionsPerAddress) {
        this.dbClient = dbClient;
        this.fetchMode = fetchMode;
        this.requiredQuantity = fixedPositionsPerAddress;
    }

    @Override
    public T call() {
        if(fetchMode.equals(FetchMode.DEVICES)) {
            return (T) getQuantityOfCommittedDevices();
        }
        else {
            return (T) getQuantityOfFullPositionsLists();
        }
    }

    private Long getQuantityOfFullPositionsLists() {

        MongoCollection<Document> accounts = dbClient.getCollection(Constants.GPS_DATA_COLLECTION);
        long listsWithAllPositionsSize = accounts.countDocuments(new Document("list",
                new Document("$size", requiredQuantity)));

        logger.info("quantity of devices with all positions: {} ", listsWithAllPositionsSize);

        return listsWithAllPositionsSize;
    }

    private Long getQuantityOfCommittedDevices() {

        MongoCollection<Document> accounts = dbClient.getCollection(Constants.GPS_DATA_COLLECTION);
        long committedDevicesSize = accounts.countDocuments();
        logger.info("registered devices quantity: {} ", committedDevicesSize);
        return committedDevicesSize;
    }

}
