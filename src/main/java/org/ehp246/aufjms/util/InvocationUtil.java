/**
 * 
 */
package org.ehp246.aufjms.util;

import java.util.concurrent.Callable;

/**
 * @author Lei Yang
 *
 */
public class InvocationUtil {

	private InvocationUtil() {
		super();
	}

	public static <V> V invoke(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
