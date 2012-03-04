package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Test;

public class ModelTest {

	@Test
    public void findById() {
        running(fakeApplication(), new Runnable() {
           public void run() {
        	   Post post = new Post();
        	   post.title = "fake post";
        	   post.insert();
               assertThat(post.title).isEqualTo("fake post");
               assertThat(Post.find.byId(post.id).id).isEqualTo(post.id);
               assertThat(Post.find.byId(post.id).title).isEqualTo(post.title);
               
               post.title = "real title";
               post.update();
               assertThat(Post.find.byId(post.id).title).isEqualTo(post.title);
               
           }
        });
    }
	
}
