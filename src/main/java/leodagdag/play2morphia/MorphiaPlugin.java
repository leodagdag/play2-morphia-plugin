package leodagdag.play2morphia;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

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

public class MorphiaPlugin extends Plugin {

	public static final String VERSION = "0.1";

	private static Mongo _mongo = null;
	private static Morphia _morphia = null;
	private static Datastore _ds = null;
	private final Application application;

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
				debug("mapping class: %1$s", clazz);
				_morphia.map(Class.forName(clazz, true, application.classloader()));
			}
			debug("End of initalize Morphia", "");

		} catch (UnknownHostException e) {
			error("Problem connecting MongoDB", e);
			throw new RuntimeException(e);
		} catch (MongoException e) {
			error("Problem connecting MongoDB", e);
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			error("Problem connecting MongoDB", e);
			throw new RuntimeException(e);
		}
	}

	public static Datastore ds() {
		return _ds;
	}

	public static DB db(){
		return ds().getDB();
	}
	
	
	//
	// Internal Logger
	//
	public static void debug(String msg, Object... args) {
		Logger.debug(msg_(msg, args));
	}

	public static void debug(Throwable t, String msg, Object... args) {
		Logger.debug(msg_(msg, args), t);
	}

	private static String msg_(String msg, Object... args) {
		return String.format("MorphiaPlugin-" + VERSION + "> %1$s", String.format(msg, args));
	}

	public static void error(String msg, Object... args) {
		Logger.error(msg_(msg, args));
	}

	public static void error(Throwable t, String msg, Object... args) {
		Logger.error(msg_(msg, args), t);
	}
}
