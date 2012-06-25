package models;

import com.mongodb.gridfs.GridFSDBFile;
import leodagdag.play2morphia.Blob;
import org.bson.types.ObjectId;
import org.junit.Test;
import play.api.libs.MimeTypes;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class ModelTest {

    @Test
    public void findById() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                File picture = new File("public/images/test/test.jpg");
                Post post = new Post();
                post.title = "fake post";
                String mimetype = MimeTypes.forFileName(picture.getName()).get();
                Blob blob = new Blob(picture, mimetype);
                post.picture = blob;
                post.insert();
                ObjectId id = post.id;

                post = Post.find().byId(id);
                GridFSDBFile gridFSDBFile = post.picture.getGridFSFile();
                assertThat(gridFSDBFile).isNotNull();

                assertThat(post.title).isEqualTo("fake post");
                assertThat(Post.find().byId(post.id).id).isEqualTo(post.id);
                assertThat(Post.find().byId(post.id).title).isEqualTo(post.title);
                assertThat(Post.find().asList().size()).isGreaterThan(0);
                post.title = "real title";
                post.update();
                assertThat(Post.find().byId(post.id).title).isEqualTo(post.title);
            }
        });
    }
}
