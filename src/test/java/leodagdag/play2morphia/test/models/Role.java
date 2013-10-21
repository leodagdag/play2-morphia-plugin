package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import leodagdag.play2morphia.Model;
import org.bson.types.ObjectId;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 14:54
 */
@Entity
public class Role extends Model {

    @Id
    public ObjectId id;

    public RoleType roleType;

    public static Finder<ObjectId, Role> find() {
        return new Finder<ObjectId, Role>(ObjectId.class, Role.class);
    }

    public static Role as(RoleType roleType) {
        return find().field("roleType").equal(roleType).get();
    }
}
