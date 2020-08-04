package in.ehp246.aufjms.core.util;

/**
 * @author Lei Yang
 *
 */
public interface Strings {
	static String ifBlank(String str, String def) {
		return str == null || str.isBlank() ? def : str;
	}
}
