package leodagdag.play2morphia.test.models;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
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
