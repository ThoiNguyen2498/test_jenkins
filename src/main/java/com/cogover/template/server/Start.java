package com.cogover.template.server;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.config.DbConfigLoader;
import com.cogover.template.server.config.OpenTelemetryConfig;
import com.cogover.template.server.network.pub_sub.publisher.Producer;
import com.cogover.template.server.network.rpc_server.server.RpcServerFactory;
import com.cogover.template.server.util.RedisUtil;
import com.cogover.template.server.worker.SampleJob;
import com.cogover.template.server.worker.SampleUser;
import com.cogover.template.server.worker.SampleWorker;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.log4j.Log4j2;

import io.opentelemetry.api.OpenTelemetry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huydn on 01/04/2024 14:42
 */
@Log4j2
public class Start {

    private static final int SAMPLE_NUMBER_OF_WORKERS = 3;
    private static final int SAMPLE_NUMBER_OF_USERS = 10;

    //khai bao 1 lan va dung cho ca ung dung
    public static Tracer tracer;

    public static void main(String[] args) throws Exception {
        Config.loadConfig();
        DbConfigLoader.loadDbConfig();
        RedisUtil.initWithVault();

        initTracer();

        //start listen from Kafka
        Producer.start();
        //DatabaseDataChangeConsumer.start(Config.serverConfig.getKafka());

        //sample worker: start threadpool worker
        for (int i = 0; i < SAMPLE_NUMBER_OF_WORKERS; i++) {
            SampleWorker sampleWorker = new SampleWorker("SampleWorker-" + i);
            sampleWorker.start();
        }
        samplePutJob();

        //Chuan bi moi thu OK thi moi start RPC servers: de bat dau lang nghe request tu client
        RpcServerFactory.startAllServers();

        log.info("Template Server is started! NodeId: {}", Config.NODE_ID);
    }

    public static void initTracer() {
        //OpenTelemetry: init SDK
        String url = "http://localhost:4317";
        OpenTelemetry openTelemetry = OpenTelemetryConfig.initOpenTelemetry(url, true);
        //test
        tracer = openTelemetry.getTracer("io.opentelemetry.example.JaegerExample");
        //testSpan();
    }

    private static void testSpan() {
        for (int i = 0; i < 10000; i++) {
            Span parentSpan = tracer.spanBuilder("Request: /v1/test_service/" + i).startSpan();
            try (Scope ignored = parentSpan.makeCurrent()) {
                //ham xu ly thu 1
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }

                subTest(1);
                subTest(2);

                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                }
            } catch (Throwable t) {
                parentSpan.recordException(t);
                throw t;
            } finally {
                parentSpan.end();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    private static void subTest(int i) {
        Span childSpan = tracer.spanBuilder("child_" + i).startSpan();
        try (Scope ignored = childSpan.makeCurrent()) {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
            }
        } finally {
            childSpan.end();
        }
    }

    private static void samplePutJob() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        //giả sử có SAMPLE_NUMBER_OF_USERS Users
        List<SampleUser> users = new ArrayList<>();
        for (int i = 0; i < SAMPLE_NUMBER_OF_USERS; i++) {
            SampleUser user = new SampleUser("User-" + i);
            users.add(user);
        }

        Thread thread = new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                SampleUser user = users.get(i % SAMPLE_NUMBER_OF_USERS);
                //chọn Woker để chạy theo từng user để đảm bảo các job đến cùng 1 user sẽ được chạy đúng thứ tự
                SampleJob job = new SampleJob(user);
                //nếu muốn chạy task ở đây luôn có thể setTask và bỏ qua việc implement run() trong SampleJob
                //job.setTask(() -> {
                //    //running...
                //});

                SampleWorker.execute(job);
                try {
                    Thread.sleep(50000);
                } catch (InterruptedException e) {
                }
            }
        });
        thread.start();
    }

}
