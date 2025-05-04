package com.cogover.template.server.network.pub_sub.db_data_change;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.config.ServerConfig;
import com.cogover.kafka.KafkaConsumer;
import com.cogover.kafka.config.ConsumerConfig;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class DatabaseDataChangeConsumer {

    private static KafkaConsumer consumer;

    private DatabaseDataChangeConsumer() {
    }

    public static void start(ServerConfig.Kafka kafkaConfig) {
        ConsumerConfig config = new ConsumerConfig();
        config.connString = kafkaConfig.getUri();
        config.username = kafkaConfig.getUsername();
        config.password = kafkaConfig.getPassword();
        config.enableAuth = kafkaConfig.isEnableAuth();
        config.clientId = "Consumer-" + Config.NODE_ID;
        //mỗi AuthServer cần 1 groupID khác nhau để đều nhận dc event
        config.groupId = "Consumer-Group-" + Config.NODE_ID;

        config.enableCommitOffsetMode = true;

        List<String> topics = new ArrayList<>();
        topics.add(kafkaConfig.getDbDataChangeTopic());

        log.info("Start listening to Kafka topic: {}", kafkaConfig.getDbDataChangeTopic());

        consumer = new KafkaConsumer(config, topics, new DatabaseDataChangeListener());
        consumer.start();
    }

    public static void stop() {
        if (consumer != null) {
            consumer.stop();
        }
    }

}
