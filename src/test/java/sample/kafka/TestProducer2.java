package sample.kafka;

import com.cogover.template.server.config.Config;
import com.cogover.kafka.KafkaProducer;
import com.cogover.kafka.config.ProducerConfig;
import org.json.JSONObject;

public class TestProducer2 {

    public static void main(String[] args) {
        Config.loadConfig();

        ProducerConfig config = new ProducerConfig();
        config.connString = Config.serverConfig.getKafka().getUri();
        config.clientId = "Producer-2";
        config.username = Config.serverConfig.getKafka().getUsername();
        config.password = Config.serverConfig.getKafka().getPassword();

        KafkaProducer producer = new KafkaProducer(config);
        for (int i = 0; i < 1; i++) {
            JSONObject data = new JSONObject();
            data.put("id", "cbahjcbshacb");//data.put("id", "WS_STRINGEE");
            //
            JSONObject msg = new JSONObject();
            msg.put("database", "mysql");
            msg.put("table", "workspace_account");
            msg.put("data", data);
            msg.put("type", "update");

            producer.publish(
                    Config.serverConfig.getKafka().getDbDataChangeTopic(),
                    System.currentTimeMillis() + "",
                    msg.toString(),
                    true
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
