package com.cogover.template.server.network.pub_sub.publisher;

import com.cogover.template.server.config.Config;
import com.cogover.kafka.KafkaProducer;
import com.cogover.kafka.config.ProducerConfig;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

@Log4j2
public class Producer {

    private static KafkaProducer kafkaProducer;

    private Producer() {
    }

    public static void start() {
        ProducerConfig config = new ProducerConfig();
        config.connString = Config.serverConfig.getKafka().getUri();
        config.clientId = "Producer-" + Config.NODE_ID;
        config.username = Config.serverConfig.getKafka().getUsername();
        config.password = Config.serverConfig.getKafka().getPassword();
        config.enableAuth = Config.serverConfig.getKafka().isEnableAuth();

        kafkaProducer = new KafkaProducer(config);
    }

    public static void send(JSONObject msg) {
        kafkaProducer.publish(
                Config.serverConfig.getKafka().getDbDataChangeTopic(),
                System.currentTimeMillis() + "",
                msg.toString(),
                true
        );

        log.info("Sent to Kafka, topic: {}, value: {}", Config.serverConfig.getKafka().getDbDataChangeTopic(), msg.toString());
    }

    /*
     * value {
     *     "database": "mysql",
     *     "table": "account",
     *     "type": "update",
     *     "data": {
     *         "id": 1,
     *         "timezone": "Asia\/Yangon"
     *     }
     * }
     */
    public static void sendDbDataChange(String database, String table, String type, JSONObject data) {
        JSONObject msg = new JSONObject();
        msg.put("database", database);
        msg.put("table", table);
        msg.put("type", type);
        msg.put("data", data);

        send(msg);
    }
}
