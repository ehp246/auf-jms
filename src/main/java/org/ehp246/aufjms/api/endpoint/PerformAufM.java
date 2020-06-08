package org.ehp246.aufjms.api.endpoint;

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
public @interface PerformAufM {
	String[] type() default {};

	ExecutionModel execution() default ExecutionModel.DEFAULT;
}
