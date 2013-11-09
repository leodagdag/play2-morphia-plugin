package leodagdag.play2morphia;

import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.QueryImpl;
import java.util.List;

import static leodagdag.play2morphia.MorphiaPlugin.ds;

public class Model {

    public void _post_Load() {
    }

    /*
    * Search Methods
    */
    public static class Finder<I, T extends Model> extends QueryImpl<T> {

        private final Class<I> idType;
        private final Class<T> type;

        public Finder(Class<I> idType, Class<T> type) {
            super(type, MorphiaPlugin.ds().getCollection(type), MorphiaPlugin.ds());
            this.idType = idType;
            this.type = type;
        }

        public T byId(Object id) {
            return MorphiaPlugin.ds().createQuery(type).field(Mapper.ID_KEY).equal(id).get();
        }

        public List<T> all() {
            return MorphiaPlugin.ds().find(type).asList();
        }
    }

    /*
      * Life methods
      */
    @SuppressWarnings("unchecked")
    public <T extends Model> T insert() {
        ds().save(this);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Model> T update() {
        ds().save(this);
        return (T) this;
    }

    public void delete() {
        ds().delete(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends Model> T refresh() {
        return (T) ds().get(this);
    }


}
