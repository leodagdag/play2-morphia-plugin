package models;

import com.mongodb.gridfs.GridFSDBFile;
import javassist.*;
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
                ClassPool classPool = ClassPool.getDefault();
                ClassPath cp = new ClassClassPath(Post.class);
                classPool.appendClassPath(cp);
                try {
                    CtClass ctPost = classPool.getCtClass(Post.class.getName());
                    for (CtField ctField : ctPost.getDeclaredFields()) {
                        for (Object o : ctField.getAnnotations()) {
                            System.out.println(ctField.getSignature() + ":" + o.toString());
                        }
                    }
                    for (CtMethod ctMethod : ctPost.getDeclaredMethods()) {
                        System.out.println(ctMethod.toString());
                        ctMethod.getMethodInfo().getCodeAttribute();
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                File picture = new File("public/images/test/test.jpg");
                Post post = new Post();
                post.title = "fake post";
                String mimetype = MimeTypes.forFileName(picture.getName()).get();
                Blob blob = new Blob(picture, mimetype);
                post.picture = blob;
                post.insert();
                ObjectId id = post.id;

                post = Post.find.byId(id);
                GridFSDBFile gridFSDBFile = post.picture.getGridFSFile();
                assertThat(gridFSDBFile).isNotNull();

                assertThat(post.title).isEqualTo("fake post");
                assertThat(Post.find.byId(post.id).id).isEqualTo(post.id);
                assertThat(Post.find.byId(post.id).title).isEqualTo(post.title);
                assertThat(Post.find.asList().size()).isGreaterThan(0);
                post.title = "real title";
                post.update();
                assertThat(Post.find.byId(post.id).title).isEqualTo(post.title);
            }
        });
    }
}
