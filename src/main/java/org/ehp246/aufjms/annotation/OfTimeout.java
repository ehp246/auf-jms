package org.ehp246.aufjms.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface OfTimeout {
	/**
	 * Timeout in milliseconds waiting for a reply. It has no impact on the TTL of
	 * the request message.
	 * 
	 * @return
	 */
	long value() default 0;
}
