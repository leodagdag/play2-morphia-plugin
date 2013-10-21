package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;
import org.bson.types.ObjectId;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 13:12
 */
@Entity
public class Employee extends User {

    public String trigramme;

    @Reference
    public Manager manager;

    public static Finder<ObjectId, Employee> find() {
        return new Finder<ObjectId, Employee>(ObjectId.class, Employee.class);
    }

}
