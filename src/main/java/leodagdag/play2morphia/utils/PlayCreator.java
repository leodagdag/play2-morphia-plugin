package leodagdag.play2morphia.utils;

import org.mongodb.morphia.mapping.DefaultCreator;
import com.mongodb.DBObject;
import play.Play;

public class PlayCreator extends DefaultCreator {

    @Override
    protected ClassLoader getClassLoaderForClass(String clazz, DBObject object) {
        return Play.application().classloader();
    }
}
