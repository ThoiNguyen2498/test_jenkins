package com.cogover.template.server.network.pub_sub.db_data_change;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.cache.CacheUtil;
import com.cogover.kafka.KafkaConsumer;
import com.cogover.kafka.KafkaConsumerHandler;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

@Log4j2
public class DatabaseDataChangeListener implements KafkaConsumerHandler {

    private final Map<String, Integer> allowTables = Map.of(
            "account", 1,
            "workspace", 1,
            "workspace_account", 1,
            "department", 1,
            "department_personnel", 1,
            "department_position", 1,
            "position", 1,
            "auth_token", 1,
            "trusted_device", 1
    );
    private final Map<String, Integer> allowDbs = Map.of(
            DatabaseDataChange.Database.MYSQL, 1,
            DatabaseDataChange.Database.MONGO, 1
    );
    private final Map<String, Integer> deleteCacheTypes = Map.of(
            DatabaseDataChange.Type.UPDATE, 1,
            DatabaseDataChange.Type.INSERT, 1,
            DatabaseDataChange.Type.DELETE, 1
    );

    DatabaseDataChangeListener() {
    }

    @Override
    public void process(KafkaConsumer consumer, ConsumerRecords<String, String> records) {
        records.forEach(cRecord -> {
            String topic = cRecord.topic();
            String key = cRecord.key();
            String value = cRecord.value();
            log.info("+++++++++ onEvent from Kafka, topic: {}, key: {}, value: {}", topic, key, value);

            if (!topic.equals(Config.serverConfig.getKafka().getDbDataChangeTopic())) {
                log.error("Invalid topic: {}", topic);
                return;
            }

            try {
                JSONObject msg = new JSONObject(value);
                String database = msg.getString("database");//mysql,...
                String table = msg.getString("table");//account, workspace, workspace_account,
                String type = msg.getString("type");//update, insert, delete
                JSONObject data = msg.getJSONObject("data");
                String recordId = data.optString("id", "");
                String accountId = data.optString("account_id", null);

                if (!allowDbs.containsKey(database)) {
                    log.error("Invalid database: {}", database);
                    return;
                }
                if (!allowTables.containsKey(table)) {
                    log.error("Invalid table: {}", table);
                    return;
                }
                if (!deleteCacheTypes.containsKey(type)) {
                    log.error("Invalid type: {}", type);
                    return;
                }

//                //xoa cache
                this.deleteCache(table, recordId, accountId);
//                CacheUtil.delete(table, recordId);
//
//                //xoa cache theo tag
//                CacheUtil.deleteByTag(recordId);
            } catch (JSONException e) {
                log.error("Invalid JSON: {}", value);
            }
        });
    }

    private void deleteCache(String table, String recordId, String accountId) {
        if (recordId != null && !recordId.isEmpty()) {
            //xoa cache theo key
            CacheUtil.delete(table, recordId);

            //xoa cache theo tag
            CacheUtil.deleteByTag(recordId);
        }

        //neu table: auth_token
        if (table.equals("auth_token")) {
            if (recordId != null && !recordId.isEmpty()) {
                CacheUtil.deleteByTag(AuthToken.COLLECTION_NAME + ":parent_id:" + recordId);
            }

            if (accountId != null && !accountId.isEmpty()) {
                CacheUtil.deleteByTag(AuthToken.COLLECTION_NAME + ":account_id:" + accountId);
            }
        }

    }
}
