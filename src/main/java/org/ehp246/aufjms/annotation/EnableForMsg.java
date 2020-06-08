package org.ehp246.aufjms.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.ehp246.aufjms.core.configuration.ActionExecutorConfiguration;
import org.ehp246.aufjms.core.configuration.ConnectionConfiguration;
import org.ehp246.aufjms.core.endpoint.EndpointConfigurer;
import org.ehp246.aufjms.core.endpoint.EndpointFactory;
import org.ehp246.aufjms.core.endpoint.EndpointRegistrar;
import org.springframework.context.annotation.Import;

/**
 * 
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ConnectionConfiguration.class, EndpointRegistrar.class, EndpointFactory.class,
		EndpointConfigurer.class, ActionExecutorConfiguration.class })
public @interface EnableForMsg {
	Endpoint[] value() default @Endpoint;

	@Retention(RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface Endpoint {
		/**
		 * Destination name of the incoming message.
		 * 
		 * @return
		 */
		String value() default "";

		Class<?>[] scanBasePackageClasses() default {};
	}
}
