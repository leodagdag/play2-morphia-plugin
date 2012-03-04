package leodagdag.play2morphia;

import static leodagdag.play2morphia.MorphiaPlugin.ds;
import static play.libs.F.Tuple;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

import play.Logger;
import play.libs.F.Tuple;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Transient;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.QueryImpl;
import com.mongodb.gridfs.GridFSDBFile;

public class Model {

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
					throw new RuntimeException("No @javax.persistence.Id field found in class [" + this.getClass() + "]");
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return _idGetSet;
	}

	private Object _getId() {
		try {
			return _idAccessors()._1.invoke(this);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void _setId(Object id) {
		try {
			_idAccessors()._2.invoke(this, id);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Search Method
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
				if (field.getClass().equals(Blob.class)) {
					blobFields.add(field);
				}
			}
			if (blobFields.isEmpty()) {
				return;
			}

			for (Field blobField : blobFields) {
				Blob blob = (Blob) blobField.get(this);
				String bloblFieldName = getBlobFileName(blobField.getName());
				if (blobChanged(bloblFieldName)) { // <= If Blob has changed...
					Blob.delete(bloblFieldName); // <= ... we delete it ONLY
													// from Database
				}
				if (null != blob) { // <= If blob is not null
					GridFSDBFile file = blob.getGridFSFile();
					if (null != file) {
						file.put("name", bloblFieldName);
						file.save();
					}
				}
			}

			blobFieldsTracker.clear();
		} catch (Exception e) {
			Logger.error("Error during save blob", e);
		}
	}

	private void deleteBlobs() {

	}

	private final Map<String, Boolean> blobFieldsTracker = new HashMap<String, Boolean>();

	private final boolean blobChanged(String fieldName) {
		return (blobFieldsTracker.containsKey(fieldName) && blobFieldsTracker.get(fieldName));
	}

	private final void setBlobChanged(String fieldName) {
		blobFieldsTracker.put(fieldName, true);
	}

	public String getBlobFileName(String fieldName) {
		return getBlobFileName(getClass().getSimpleName(), _getId(), fieldName);
	}

	public static String getBlobFileName(String className, Object id, String fieldName) {
		return String.format("%s_%s_%s", className, StringUtils.capitalize(fieldName), id);
	}
}
