package leodagdag.play2morphia.utils;

import play.Configuration;
import play.Logger;

public class MorphiaLogger {

    public static boolean isDebugEnabled() {
        return Logger.isDebugEnabled();
    }

    public static boolean isWarnEnabled() {
        return Logger.isWarnEnabled();
    }

    public static boolean isErrorEnabled() {
        return Logger.isErrorEnabled();
    }

    public static void debug(Configuration morphiaConf) {
        if (isDebugEnabled()) {
            if (morphiaConf != null) {
                debug("Config by morphiaConf");
                for (String key : morphiaConf.keys()) {
                    debug("%s=%s", key, morphiaConf.getString(key));
                }
                debug("Config by ConfigKey");
                for (ConfigKey key : ConfigKey.values()) {
                    debug("%s=%s", key, morphiaConf.getString(key.getKey()));
                }
            }
        }
    }

    public static void debug(String msg, Object... args) {
        if (isDebugEnabled()) {
            Logger.debug(format(msg, args));
        }
    }

    public static void debug(Throwable t, String msg, Object... args) {
        if (isDebugEnabled()) {
            Logger.debug(format(msg, args), t);
        }
    }

    public static void warn(String msg, Object... args) {
        Logger.warn(format(msg, args));
    }

    public static void warn(Throwable t, String msg, Object... args) {
        Logger.warn(format(msg, args), t);
    }

    public static void error(String msg, Object... args) {
        Logger.error(format(msg, args));
    }

    public static void error(Throwable t, String msg, Object... args) {
        Logger.error(format(msg, args), t);
    }

    private static String format(String msg, Object... args) {
        return String.format("MorphiaPlugin> %s", String.format(msg, args));
    }

}
