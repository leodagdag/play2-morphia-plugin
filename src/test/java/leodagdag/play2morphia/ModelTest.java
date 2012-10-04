package leodagdag.play2morphia;

import com.google.code.morphia.query.Query;
import leodagdag.play2morphia.models.Post;
import leodagdag.play2morphia.utils.TestConfig;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class ModelTest extends AbstractTest{

    @Before
    public void setUp(){
        createEmptyCollection(Post.class);
        assertThat(Post.find().asList().size()).isEqualTo(0);
    }
    @Test
    public void testCreate() {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                Post post = new Post();
                post.title = "fake post";
                post.insert();
                ObjectId id = post.id;

                assertThat(Post.find().byId(id).id).isEqualTo(post.id);
                assertThat(Post.find().byId(id).title).isEqualTo(post.title);
                assertThat(Post.find().asList().size()).isEqualTo(1);
            }
        });
    }

    @Test
    public void testUpdate() {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                Post post = new Post();
                post.title = "fake post";
                post.insert();

                post.title = "real title";
                post.update();
                assertThat(Post.find().byId(post.id).title).isEqualTo(post.title);
            }
        });
    }

    @Test
    public void testDelete() {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                Post post = new Post();
                post.title = "fake post";
                post.insert();
                assertThat(Post.find().asList().size()).isEqualTo(1);

                post.delete();
                assertThat(Post.find().asList().size()).isEqualTo(0);
            }
        });
    }

    @Test
    public void testFindWithOr() {
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                Post alpha1= new Post();
                alpha1.title = "post1";
                alpha1.type = "ALPHA";
                alpha1.insert();

                Post beta1= new Post();
                beta1.title = "post1";
                beta1.type = "BETA";
                beta1.insert();

                Post beta2= new Post();
                beta2.title = "post1";
                beta2.type = "BETA";
                beta2.insert();

                Post gamma1= new Post();
                gamma1.title = "post1";
                gamma1.type = "GAMMA";
                gamma1.insert();

                Post zeta1= new Post();
                zeta1.title = "post1";
                zeta1.type = "ZETA";
                zeta1.insert();

                assertThat(Post.find().asList().size()).isEqualTo(5);
                Query<Post> q = MorphiaPlugin.ds().createQuery(Post.class);
                q.or(
                        q.criteria("type").equal("BETA"),
                        q.criteria("type").equal("GAMMA")
                );
                assertThat(q.countAll()).isEqualTo(3);


                List<String> criterias = Arrays.asList("BETA", "GAMMA");
                assertThat(Post.find().field("type").in(criterias).countAll()).isEqualTo(3);

            }
        });
    }
}
