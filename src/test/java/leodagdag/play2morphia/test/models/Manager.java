package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.Entity;
import org.bson.types.ObjectId;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 13:23
 */
@Entity
public class Manager extends User {

    public String trigramme;

    public static Finder<ObjectId, Manager> find() {
        return new Finder<ObjectId, Manager>(ObjectId.class, Manager.class);
    }

}
