package leodagdag.play2morphia.utils;

import leodagdag.play2morphia.MorphiaPlugin;
import play.Logger;

public class MorphiaLogger {

	public static void debug(String msg, Object... args) {
		Logger.debug(msg_(msg, args));
	}

	public static void debug(Throwable t, String msg, Object... args) {
		Logger.debug(msg_(msg, args), t);
	}

	public static void error(String msg, Object... args) {
		Logger.error(msg_(msg, args));
	}

	public static void error(Throwable t, String msg, Object... args) {
		Logger.error(msg_(msg, args), t);
	}

	private static String msg_(String msg, Object... args) {
		return String.format("MorphiaPlugin-" + MorphiaPlugin.VERSION + "> %1$s", String.format(msg, args));
	}
	
}
