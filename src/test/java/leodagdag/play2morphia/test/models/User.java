package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import leodagdag.play2morphia.Model;
import org.bson.types.ObjectId;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 13:11
 */
@Entity(value = "User")
public abstract class User extends Model {

    @Id
    public ObjectId id;

    public String username;

    public String password;

    public String email;

    public String firstName;

    public String lasttName;

    public Role role;

    public static Finder<ObjectId, ? extends User> find() {
        return new Finder<ObjectId, User>(ObjectId.class, User.class);
    }

}
