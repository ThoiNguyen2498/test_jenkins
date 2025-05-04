package sample.db;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.template.server.database.util.mongo.MongoUtil;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author huydn on 5/5/24 21:55
 */
public class TestMongoIndex {

    private static final Logger log = LogManager.getLogger("TestMongoIndex");

    public static void main(String[] args) {
        Config.loadConfig();

        MongoDatabase mongoDB = MongoUtil.getDatabase(Config.serverConfig.getMongodb());
        MongoCollection<AuthToken> collection = mongoDB.getCollection("auth_token", AuthToken.class);

        collection.listIndexes().forEach(index -> {
            log.info("======= index: {}", index);
        });

        collection.createIndex(Indexes.ascending("workspace_id"));
        collection.createIndex(Indexes.ascending("parent_id"));
        collection.createIndex(Indexes.ascending("account_id"));
    }

}
