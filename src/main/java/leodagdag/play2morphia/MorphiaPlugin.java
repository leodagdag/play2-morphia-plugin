package leodagdag.play2morphia;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import leodagdag.play2morphia.utils.MorphiaLogger;

import play.Configuration;
import play.Application;
import play.Logger;
import play.Plugin;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.code.morphia.validation.MorphiaValidation;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;

public class MorphiaPlugin extends Plugin {

	public static final String VERSION = "0.0.1";

	private static Mongo _mongo = null;
	private static Morphia _morphia = null;
	private static Datastore _ds = null;
	private final Application application;
	private static GridFS gridfs;

	public MorphiaPlugin(Application application) {
		this.application = application;
	}

	@Override
	public void onStart() {
		// Register SLF4JLogrImplFactory as Logger
		// @see http://nesbot.com/2011/11/28/play-2-morphia-logging-error
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

		try {
			// TODO read config from application.conf
			Configuration morphiaConf = Configuration.root().getConfig("morphia");
			if (morphiaConf != null) {
				for (String key : morphiaConf.keys()) {
					MorphiaLogger.debug("Key: %s", key);
				}
			}
			// Connect to MongoDB
			_mongo = new Mongo();
			_morphia = new Morphia();
			// Configure validation
			MorphiaValidation morphiaValidation = new MorphiaValidation();
			morphiaValidation.applyTo(_morphia);
			// Create datastore
			_ds = _morphia.createDatastore(_mongo, "persistance");
			// Register all models.Class
			Set<String> classes = new HashSet<String>();
			classes.addAll(application.getTypesAnnotatedWith("models", com.google.code.morphia.annotations.Entity.class));
			classes.addAll(application.getTypesAnnotatedWith("models", com.google.code.morphia.annotations.Embedded.class));
			for (String clazz : classes) {
				MorphiaLogger.debug("mapping class: %1$s", clazz);
				_morphia.map(Class.forName(clazz, true, application.classloader()));
			}
			MorphiaLogger.debug("End of initalize Morphia", "");

		} catch (UnknownHostException e) {
			MorphiaLogger.error("Problem connecting MongoDB", e);
			throw new RuntimeException(e);
		} catch (MongoException e) {
			MorphiaLogger.error("Problem connecting MongoDB", e);
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			MorphiaLogger.error("Problem connecting MongoDB", e);
			throw new RuntimeException(e);
		}
	}

	public static Datastore ds() {
		return _ds;
	}

	public static GridFS gridFs() {
		return gridfs;
	}

	public static DB db() {
		return ds().getDB();
	}

}
