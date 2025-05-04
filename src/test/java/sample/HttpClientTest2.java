package sample;

import com.cogover.http_client.CommonHttpRequest;
import com.cogover.http_client.HttpClient;
import com.cogover.template.server.config.Config;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author huydn on 6/8/24 00:18
 */
@Log4j2
public class HttpClientTest2 {

    private static final AtomicLong counter = new AtomicLong(0);
    private static long lastTime = 0;

    private static final List<String> URLS = List.of(
            "https://business.openapi.zalo.me/message/status?message_id=message_id&phone=phone"
    );

    public static void main(String[] args) throws Exception {
        Config.initLog4j();

        test22();
    }

    public static void test22() throws Exception {
        int numberOfRequestPerThread = 100;
        int numberOfThread = 5;

        CountDownLatch latch = new CountDownLatch(numberOfRequestPerThread * numberOfThread);
        long start = System.currentTimeMillis();

        HttpClient httpClient = new HttpClient(null);

        for (int i = 0; i < numberOfThread; i++) {
            //tao N thread
            Thread thread = new Thread(() -> {
                try {
                    test(httpClient, latch, numberOfRequestPerThread);
                } catch (URISyntaxException e) {
                    log.error("Exception", e);
                }
            });
            thread.start();
        }

        latch.await();
        long time = System.currentTimeMillis() - start;
        log.info("done 2: {}ms", time);
    }

    private static void test(HttpClient httpClient, CountDownLatch latch, int numberOfRequest) throws URISyntaxException {
        for (int i = 0; i < numberOfRequest; i++) {
            String url = URLS.get(i % URLS.size());
            URI uri = new URI(url);

            FutureCallback<SimpleHttpResponse> callback = new FutureCallback<>() {

                @Override
                public void completed(SimpleHttpResponse response) {
                    latch.countDown();

                    long count = counter.incrementAndGet();
                    if (count % 50 == 0) {
                        long time = System.currentTimeMillis() - lastTime;
                        log.info("++++++++++ count: {}, url: {}, Status: {}, time: {}ms", count, url, response.getCode(), time);
                        lastTime = System.currentTimeMillis();
                    }
                }

                @Override
                public void failed(Exception ex) {
                    counter.incrementAndGet();
                    latch.countDown();
                    log.warn("+++++++ failed: {}, url: {}", ex.getMessage(), url);
                }

                @Override
                public void cancelled() {
                    counter.incrementAndGet();
                    latch.countDown();
                    log.warn("+++++++ cancelled");
                }
            };

            SimpleHttpRequest request = SimpleHttpRequest.create("POST", uri);
            CommonHttpRequest commonHttpRequest = new CommonHttpRequest(url, uri, request);
            httpClient.asyncSend(commonHttpRequest, callback);
        }
    }

}
