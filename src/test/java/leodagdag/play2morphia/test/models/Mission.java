package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import leodagdag.play2morphia.Model;
import org.bson.types.ObjectId;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 15:44
 */

@Entity
public class Mission extends Model {

    @Id
    public ObjectId id;

    public String code;

    public String name;

    public static Finder<ObjectId, Mission> find() {
        return new Finder<ObjectId, Mission>(ObjectId.class, Mission.class);
    }

    public static Mission byCode(String code) {
        return find().field("code").equal(code).get();
    }
}
