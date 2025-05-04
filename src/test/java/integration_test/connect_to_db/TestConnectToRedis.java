package integration_test.connect_to_db;

import com.cogover.template.server.util.RedisUtil;
import integration_test.InitResourceForTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author huydn on 19/5/24 11:22
 */
@Log4j2
class TestConnectToRedis {

    @BeforeEach
    void setUp() {
        InitResourceForTest.init();
    }

    @Test
    void testConnect() {
        log.info("Test connect to Redis...");
        RedisUtil.initWithVault();

        String key = "test-key-98989892223";
        String value = "test-value";
        RedisUtil.set(key, value, 30);
        String value2 = RedisUtil.get(key);
        //expect value2 = value
        assertEquals(value, value2);
    }

}
