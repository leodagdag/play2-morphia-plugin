package leodagdag.play2morphia.test;

import com.mongodb.DBCollection;
import leodagdag.play2morphia.MorphiaPlugin;
import leodagdag.play2morphia.test.utils.TestConfig;
import org.apache.commons.lang3.StringUtils;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * User: leo
 * Date: 04/10/12
 * Time: 20:26
 */
public abstract class AbstractTest {

    protected void createEmptyCollection(final Class clazz) {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                DBCollection col = MorphiaPlugin.db().getCollection(clazz.getSimpleName());
                col.drop();
            }
        });
    }

    protected void createEmptyCollection(final String colName) {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                DBCollection col = MorphiaPlugin.db().getCollection(colName);
                col.drop();
            }
        });
    }

    protected void dropAllCollections() {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                for (String colName : MorphiaPlugin.db().getCollectionNames()) {
                    if (!StringUtils.startsWith(colName, "system.")) {
                        MorphiaPlugin.db().getCollection(colName).drop();
                    }
                }
            }
        });
    }

}
