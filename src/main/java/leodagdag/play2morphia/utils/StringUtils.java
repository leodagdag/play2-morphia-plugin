package leodagdag.play2morphia.utils;

/**
 * User: leodagdag
 * Date: 03/10/12
 * Time: 12:54
 */
public class StringUtils {
    /**
     * Re-implementation of org.apache.commons.lang.StringUtils.isBlank() to remove dependency
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }


}
