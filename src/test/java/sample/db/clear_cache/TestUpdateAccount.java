package sample.db.clear_cache;

import com.cogover.template.server.config.Config;
import com.cogover.kafka.KafkaProducer;
import com.cogover.kafka.config.ProducerConfig;
import org.json.JSONObject;

public class TestUpdateAccount {

    public static void main(String[] args) {
        Config.loadConfig();

        ProducerConfig config = new ProducerConfig();
        config.connString = Config.serverConfig.getKafka().getUri();
        config.clientId = "Producer-2";
        config.username = Config.serverConfig.getKafka().getUsername();
        config.password = Config.serverConfig.getKafka().getPassword();

        KafkaProducer producer = new KafkaProducer(config);

        update(producer);
    }

    private static void update(KafkaProducer producer) {
        JSONObject data = new JSONObject();
        data.put("id", "ACHUY");
        //
        JSONObject msg = new JSONObject();
        msg.put("database", "mysql");
        msg.put("table", "account");
        msg.put("data", data);
        msg.put("type", "update");

        producer.publish(
                Config.serverConfig.getKafka().getDbDataChangeTopic(),
                System.currentTimeMillis() + "",
                msg.toString(),
                true
        );
    }
}
