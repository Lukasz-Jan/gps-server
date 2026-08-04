package com.gps.shared.mongo.client.settings;

import com.gps.shared.Constants;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

public class ClientConfigurationExt extends AbstractMongoClientConfiguration {

    protected static final Logger logger = LoggerFactory.getLogger(ClientConfigurationExt.class);
    protected final String mongoUrl;

    public ClientConfigurationExt(String mongoUrl) {
        this.mongoUrl = mongoUrl;
    }

    @Override
    public boolean autoIndexCreation() {
        logger.info("autoIndexCreation");
        return true;
    }

    @Override
    protected String getDatabaseName() {
        return Constants.DATABASE_NAME;
    }

    @Override
    protected MongoClientSettings mongoClientSettings() {

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(mongoUrl));
        configureClientSettings(builder);
        return builder.build();
    }
}
