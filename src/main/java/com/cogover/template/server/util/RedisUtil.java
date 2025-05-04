package com.cogover.template.server.util;

import com.cogover.common.Pair;
import com.cogover.vault.utils.VaultUtil;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

/**
 * @author huydn on 2/5/24 12:42
 */
@Log4j2
public class RedisUtil {

    private static RedissonClient redisson;
    private static Config config;

    private RedisUtil() {
    }

    public static void main(String[] args) {
        com.cogover.template.server.config.Config.initLog4j();
        //RedisUtil.init();
        RedisUtil.initWithAddrAndPassword("test-system.cogover.net:30021", "str123ingee6789okpro");

        RAtomicLong rr = RedisUtil.redisson.getAtomicLong("test122");
        rr.expire(Duration.ofSeconds(10));
        System.out.println(rr);
        System.out.println(rr.get());
        System.out.println(rr.addAndGet(1));
    }

    public static void init() {
        try {
            config = getConfig("./config/redis.yaml", null, null);
            tryConnect();
        } catch (Exception ex) {
            //bat buoc phai de "Exception"
            log.error(ex, ex);
            redisson = null;
        }
    }

    public static void initWithVault() {
        JSONObject moduleJson = VaultUtil.getModuleAddr("redis");

        String redisAddr = VaultUtil.getModuleAddr("redis", true);
        log.info("initWithVault RedisUtil, Addr={}", redisAddr);
        initWithAddrAndPassword(redisAddr, moduleJson.getString("password"));
    }

    public static void initWithAddrAndPassword(String addr, String password) {
        try {
            config = getConfig("./config/redis.yaml", addr, password);
            tryConnect();
        } catch (Exception ex) {
            //bat buoc phai de "Exception"
            log.error(ex, ex);
            redisson = null;
        }
    }

    private static void tryConnect() {
        while (true) {
            //thu init lien tuc, tranh truong hop Redis chua Start
            try {
                redisson = Redisson.create(config);
            } catch (Exception ex) {
                //bat buoc phai de "Exception"
                log.error(ex, ex);
                redisson = null;
            }

            if (redisson != null) {
                break;
            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                log.error(ex, ex);
            }
            log.info("Init RedisUtil... DONE!");
        }
    }

    public static Config getConfig(String filePath, String replaceAddr, String password) throws IOException {
        log.info("getConfig, filePath={}", filePath);
        Config config = Config.fromYAML(new File(filePath));

        if (replaceAddr != null) {
            if (!replaceAddr.startsWith("redis://")) {
                replaceAddr = "redis://" + replaceAddr;
            }

            config.useSingleServer().setAddress(replaceAddr);
            log.info("replaceAddr, set new Addr={}", replaceAddr);
        }

        if (password != null) {
            config.useSingleServer().setPassword(password);
        }

        return config;
    }

    public static void set(String key, String value, int timeoutInSeconds) {
        try {
            RBucket<String> bucket = redisson.getBucket(key, new StringCodec(CharsetUtil.UTF_8));
            if (timeoutInSeconds <= 0) {
                bucket.set(value);
            } else {
                bucket.set(value, Duration.ofSeconds(timeoutInSeconds));
            }
        } catch (Exception e) {
            log.error("Set cache err: {}", key, e);
        }
    }

    public static void set(String key, JSONObject value, int timeout) {
        try {
            RBucket<String> bucket = redisson.getBucket(key, new StringCodec(CharsetUtil.UTF_8));
            if (timeout <= 0) {
                bucket.set(value.toString());
            } else {
                bucket.set(value.toString(), Duration.ofSeconds(timeout));
            }
        } catch (Exception e) {
            log.error("Set cache err: {}", key, e);
        }
    }

    public static String get(String key) {
        try {
            RBucket<String> bucket = redisson.getBucket(key, new StringCodec(CharsetUtil.UTF_8));
            return bucket.get();
        } catch (Exception e) {
            log.error(e, e);
        }
        return "";
    }

    public static JSONObject getJson(String key) {
        try {
            RBucket<String> bucket = redisson.getBucket(key, new StringCodec(CharsetUtil.UTF_8));
            String valueString = bucket.get();
            if (valueString == null || valueString.isEmpty()) {
                return null;
            }

            return new JSONObject(valueString);
        } catch (Exception e) {
            log.error(e, e);
        }

        return null;
    }

    public static boolean delete(String key) {
        RBucket<String> bucket = redisson.getBucket(key);
        return bucket.delete();
    }

    public static void setExpire(String key, int seconds) {
        redisson.getBucket(key).expire(Duration.ofSeconds(seconds));
    }


    public static String getAndSet(String key, String value, int timeout) {
        try {
            RBucket<String> bucket = redisson.getBucket(key, new StringCodec(CharsetUtil.UTF_8));
            return bucket.getAndSet(value, Duration.ofSeconds(timeout));
        } catch (Exception e) {
            log.error(e, e);
        }
        return "";
    }

}
