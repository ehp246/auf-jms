package org.ehp246.aufjms.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.ehp246.aufjms.api.endpoint.ExecutionModel;

/**
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface Executing {
	String[] value() default {};

	ExecutionModel execution() default ExecutionModel.DEFAULT;
}
