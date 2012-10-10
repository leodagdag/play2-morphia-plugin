package leodagdag.play2morphia.test.utils;

import java.util.HashMap;
import java.util.Map;

import static leodagdag.play2morphia.utils.ConfigKey.*;

/**
 * User: leo
 * Date: 04/10/12
 * Time: 20:11
 */
public class TestConfig {
    private static TestConfig ourInstance = new TestConfig();
    Map<String, String> config = new HashMap<String, String>();

    public static TestConfig getInstance() {
        return ourInstance;
    }

    private TestConfig() {
        config.put(PREFIX + "." + DB_SEEDS.getKey(), "127.0.0.1:27017");
        config.put(PREFIX + "." + DB_NAME.getKey(), "test");
        config.put(PREFIX + "." + ID_TYPE.getKey(), "Long");
        config.put(PREFIX + "." + DEFAULT_WRITE_CONCERN.getKey(), "SAFE");
        config.put(PREFIX + "." + COLLECTION_UPLOADS.getKey(), "fs");
        config.put(PREFIX + "." + LOGGER.getKey(), "true");
        // Disable unused plugin
        config.put("dbplugin", "disabled");
        config.put("evolutionplugin", "disabled");
        config.put("ehcacheplugin", "disabled");
    }

    public Map<String, String> config() {
        return config;
    }


}
