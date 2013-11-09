package leodagdag.play2morphia.test;

import org.mongodb.morphia.query.Query;
import leodagdag.play2morphia.MorphiaPlugin;
import leodagdag.play2morphia.test.models.*;
import leodagdag.play2morphia.test.utils.TestConfig;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 14:00
 */
public class ModelTest extends AbstractTest {
    @Before
    public void setUp() {
        dropAllCollections();
    }

    @Test
    public void testCreate() {
        System.out.println("testCreate");
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
        System.out.println("testUpdate");
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
        System.out.println("testDelete");
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
        System.out.println("testFindWithOr");
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                Post alpha1 = new Post();
                alpha1.title = "post1";
                alpha1.type = "ALPHA";
                alpha1.insert();

                Post beta1 = new Post();
                beta1.title = "post1";
                beta1.type = "BETA";
                beta1.insert();

                Post beta2 = new Post();
                beta2.title = "post1";
                beta2.type = "BETA";
                beta2.insert();

                Post gamma1 = new Post();
                gamma1.title = "post1";
                gamma1.type = "GAMMA";
                gamma1.insert();

                Post zeta1 = new Post();
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

    @Test
    public void testInheritenceAndReference() {
        System.out.println("testInheritenceAndReference");
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {
                createRole();

                Manager manager = new Manager();
                manager.username = "marge";
                manager.password = "homer";
                manager.email = "homer@simplson.com";
                manager.role = Role.as(RoleType.GESTIONNAIRE);
                manager.insert();
                ObjectId managerId = manager.id;
                assertThat(Manager.find().countAll()).isEqualTo(1);

                Employee employee = new Employee();
                employee.username = "bart";
                employee.password = "bart";
                employee.email = "bart@simplson.com";
                employee.role = Role.as(RoleType.EMPLOYEE);
                employee.manager = manager;
                employee.insert();
                assertThat(Employee.find().countAll()).isEqualTo(1);

                Manager testManager = Manager.find().byId(managerId);
                testManager.username = "homer";
                testManager.update();
                assertThat(Employee.find().byId(employee.id).manager.id).isEqualTo(managerId);
                assertThat(Employee.find().byId(employee.id).manager.username).isEqualTo(Manager.find().byId(managerId).username);
                assertThat(Employee.find().byId(employee.id).manager.username).isNotEqualTo(manager.username);
            }

            private void createRole() {
                for (RoleType roleType : RoleType.values()) {
                    Role role = new Role();
                    role.roleType = roleType;
                    role.insert();
                }
                assertThat(Role.find().countAll()).isEqualTo(RoleType.values().length);
            }
        });
    }

    @Test
    public void testEmbeddedObject() {
        System.out.println("testEmbeddedObject");
        running(fakeApplication(TestConfig.getInstance().config()), new Runnable() {
            @Override
            public void run() {

                Employee employee = createEmployee();

                createMissions();

                Period period = new Period();
                period.start = new LocalTime(4, 0);
                period.end = new LocalTime(7, 0);
                period.mission = Mission.byCode("M0");

                // Morning
                HalfDay morning = new HalfDay();
                morning.addPeriod(period);

                // Afternoon
                HalfDay afternoon = new HalfDay();
                afternoon.mission = Mission.byCode("M1");

                Day day = new Day();
                day.date = new DateTime(2012, DateTimeConstants.MAY, 3, 0, 0);
                day.morning = morning;
                day.afternoon = afternoon;

                Cra cra = new Cra();
                cra.year = 2012;
                cra.month = new Month(DateTimeConstants.MAY, "mai");
                cra.employee = employee;
                cra.days.add(day);
                cra.insert();

                assertThat(Cra.find().field("year").equal(2012).countAll()).isEqualTo(1);
                Cra testCra = Cra.find().field("year").equal(2012).get();
                assertThat(testCra.days.size()).isEqualTo(1);
                assertThat(testCra.days.get(0).afternoon.isSpecial()).isEqualTo(Boolean.FALSE);
                assertThat(testCra.days.get(0).morning.isSpecial()).isEqualTo(Boolean.TRUE);
            }

            private void createMissions() {
                for (int i = 0; i < 5; i++) {
                    Mission m = new Mission();
                    m.code = "M" + i;
                    m.name = "Mission " + i;
                    m.insert();
                }
                assertThat(Mission.find().countAll()).isEqualTo(5);
            }

            private Employee createEmployee() {
                Employee employee = new Employee();
                employee.username = "bart";
                employee.password = "bart";
                employee.email = "bart@simplson.com";
                employee.role = Role.as(RoleType.EMPLOYEE);
                employee.insert();
                return employee;
            }
        });
    }

}
