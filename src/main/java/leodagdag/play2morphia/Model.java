package leodagdag.play2morphia;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Transient;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.QueryImpl;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.BeanWrapperImpl;
import play.Logger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static leodagdag.play2morphia.MorphiaPlugin.ds;
import static play.libs.F.Tuple;

public class Model {
    private static String fieldName;

    // -- Magic to dynamically access the @Id property

    @Transient
    private Tuple<Method, Method> _idGetSet;

    private Tuple<Method, Method> _idAccessors() {
        if (_idGetSet == null) {
            try {
                Class<?> clazz = this.getClass();
                while (clazz != null) {
                    for (Field f : clazz.getDeclaredFields()) {
                        if (f.isAnnotationPresent(Id.class)) {
                            PropertyDescriptor idProperty = new BeanWrapperImpl(this).getPropertyDescriptor(f.getName());
                            _idGetSet = Tuple(idProperty.getReadMethod(), idProperty.getWriteMethod());
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
                if (_idGetSet == null) {
                    throw new RuntimeException("No @com.google.code.morphia.annotations.Id field found in class [" + this.getClass() + "]");
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return _idGetSet;
    }

    /**
     * Id getter
     *
     * @return
     */
    private Object _getId() {
        try {
            return _idAccessors()._1.invoke(this);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Id setter
     *
     * @param id
     */
    private void _setId(Object id) {
        try {
            _idAccessors()._2.invoke(this, id);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void _post_Load() {
        loadBlobs();
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
        saveBlobs();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Model> T update(Object id) {
        _setId(id);
        ds().save(this);
        saveBlobs();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Model> T update() {
        ds().save(this);
        saveBlobs();
        return (T) this;
    }

    public void delete() {
        deleteBlobs();
        ds().delete(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends Model> T refresh() {
        return (T) ds().get(this);
    }

    /*
      * Blobs management
      */
    private void saveBlobs() {
        try {
            List<Field> blobFields = new ArrayList<Field>();
            Field[] fields = this.getClass().getFields();
            for (Field field : fields) {
                if (field.getType().equals(Blob.class)) {
                    blobFields.add(field);
                }
            }
            if (blobFields.isEmpty()) {
                return;
            }
            for (Field blobField : blobFields) {
                Blob blob = (Blob) blobField.get(this);
                String bloblFieldName = computeBlobFileName(blobField.getName());
                Blob.delete(bloblFieldName); // <= ... we delete it from database
                if (null != blob) { // <= If blob is not null
                    GridFSDBFile gridFSFile = blob.getGridFSFile();
                    if (null != gridFSFile) {
                        gridFSFile.put("name", bloblFieldName);
                        gridFSFile.save();
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Error during save blob", e);
        }
    }

    private void deleteBlobs() {
        try {
            List<Field> blobFields = new ArrayList<Field>();
            Field[] fields = this.getClass().getFields();
            for (Field field : fields) {
                if (field.getClass().equals(Blob.class)) {
                    blobFields.add(field);
                }
            }
            if (blobFields.isEmpty()) {
                return;
            }
            for (Field blobField : blobFields) {
                String bloblFieldName = computeBlobFileName(blobField.getName());
                Blob.delete(bloblFieldName); // <= ... we delete it ONLY from
                // Database
            }
        } catch (Exception e) {
            Logger.error("Error during save blob", e);
        }
    }

    protected void loadBlobs() {
        try {
            Class<?> clazz = this.getClass();
            List<Field> blobFields = new ArrayList<Field>();
            while (clazz != null) {
                for (Field f : clazz.getDeclaredFields()) {
                    if (f.getType().equals(Blob.class)) {
                        blobFields.add(f);
                    }
                }
                clazz = clazz.getSuperclass();
            }
            if (!blobFields.isEmpty()) {
                for (Field blobField : blobFields) {
                    String fileName = computeBlobFileName(blobField.getName());
                    Blob b = new Blob(fileName);
                    if (b.exists()) {
                        blobField.set(this, b);
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String computeBlobFileName(String fieldName) {
        return String.format("%s_%s_%s", getClass().getSimpleName(), _getId(), fieldName);
    }
}
