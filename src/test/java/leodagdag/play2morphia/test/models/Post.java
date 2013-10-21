package leodagdag.play2morphia.test.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import leodagdag.play2morphia.Model;
import org.bson.types.ObjectId;

@Entity
public class Post extends Model {

    @Id
    public ObjectId id;

    public String title;

    public String type;

    //public Blob picture;

    public static Finder<ObjectId, Post> find() {
        return new Finder<ObjectId, Post>(ObjectId.class, Post.class);
    }


}
