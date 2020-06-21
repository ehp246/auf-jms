/**
 * 
 */
package org.ehp246.aufjms.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @author Lei Yang
 *
 */
public @interface ByMsg {
	/**
	 * To name for the outgoing message.
	 * 
	 * @return
	 */
	String value();
}
