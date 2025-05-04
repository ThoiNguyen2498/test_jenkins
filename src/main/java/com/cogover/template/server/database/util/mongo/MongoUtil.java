package com.cogover.template.server.database.util.mongo;

import com.cogover.template.server.config.ServerConfig;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;

/**
 * @author huydn on 09/04/2024 14:14
 */
@Log4j2
public class MongoUtil {

    private static final Object mutex = new Object();

    /**
     * Can duy tri suot qua trinh Server chay; MongoClient ho tro connection pool
     */
    private static MongoClient mongoClient = null;

    private MongoUtil() {
    }

    public static MongoDatabase getDatabase(ServerConfig.MongoDB mongoDBConfig) {
        return getDatabase(
                mongoDBConfig.getConnectionString(),
                mongoDBConfig.getDatabase()
        );
    }

    public static MongoDatabase getDatabase(String connectionStr, String databaseName) {
        if (mongoClient != null) {
            return mongoClient.getDatabase(databaseName);
        }

        synchronized (mutex) {
            if (mongoClient == null) {//can check them lan nua
                CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
                CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

                MongoClientSettings clientSettings = MongoClientSettings
                        .builder()
                        .applyConnectionString(new ConnectionString(connectionStr))
                        .codecRegistry(codecRegistry).build();
                mongoClient = MongoClients.create(clientSettings);//ko dc close, se gay loi
            }
        }

        try {
            return mongoClient.getDatabase(databaseName);
        } catch (Exception e) {
            log.error(e, e);
        }

        return null;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

}
