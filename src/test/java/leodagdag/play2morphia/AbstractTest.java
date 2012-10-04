package leodagdag.play2morphia;

import com.mongodb.DBCollection;
import leodagdag.play2morphia.utils.ConfigKey;
import leodagdag.play2morphia.utils.TestConfig;
import play.Configuration;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * User: leo
 * Date: 04/10/12
 * Time: 20:26
 */
public class AbstractTest {

    protected void createEmptyCollection(final Class clazz) {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                Configuration morphiaConf = Configuration.root().getConfig(ConfigKey.PREFIX);
                String dbName = morphiaConf.getString(ConfigKey.DB_NAME.getKey());
                DBCollection col = MorphiaPlugin.ds(dbName).getCollection(clazz);
                col.drop();
            }
        });
    }
}
