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
public @interface OfTtl {
	long value() default 0;
}
