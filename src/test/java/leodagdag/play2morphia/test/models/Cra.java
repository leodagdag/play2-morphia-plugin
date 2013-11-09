package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;
import com.google.common.collect.Lists;
import leodagdag.play2morphia.Model;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 15:34
 */
@Entity
public class Cra extends Model {

    public Integer year;

    public Month month;

    @Reference
    public Employee employee;

    @Embedded
    public List<Day> days = Lists.newArrayList();

    public static Finder<ObjectId, Cra> find() {
        return new Finder<ObjectId, Cra>(ObjectId.class, Cra.class);
    }
}
