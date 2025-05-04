package integration_test.connect_to_db;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.template.server.database.util.mongo.MongoUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import integration_test.InitResourceForTest;
import lombok.extern.log4j.Log4j2;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author huydn on 19/5/24 11:22
 */
@Log4j2
class TestConnectToMongo {

    @BeforeEach
    void setUp() {
        InitResourceForTest.init();
    }

    @Test
    void testConnect() {
        log.info("Test connect to Mongo...");

        try {
            MongoDatabase mongoDB = MongoUtil.getDatabase(
                    Config.serverConfig.getMongodb().getConnectionString(),
                    Config.serverConfig.getMongodb().getDatabase()
            );
            MongoCollection<AuthToken> collection = mongoDB.getCollection(AuthToken.COLLECTION_NAME, AuthToken.class);

            Bson filter = Filters.eq("device_id", "device-abc-123");
            AuthToken token2 = collection.find(filter).first();
            log.info("++++++++++ AuthToken: {}", token2);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            fail();
        }
    }

}
