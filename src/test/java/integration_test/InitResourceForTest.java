package integration_test;

import com.cogover.template.server.Start;
import com.cogover.template.server.config.Config;
import com.cogover.template.server.config.DbConfigLoader;
import com.cogover.template.server.util.RedisUtil;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 21/4/25 14:22
 */
@Log4j2
public class InitResourceForTest {

    static {
        Config.loadConfig();
        DbConfigLoader.loadDbConfig();
        RedisUtil.initWithVault();

        Start.initTracer();
    }

    public static void init() {
        log.info("Init resource for test...");
    }

}
