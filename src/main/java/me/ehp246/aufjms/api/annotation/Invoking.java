package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface Invoking {
	String value() default "";
}
