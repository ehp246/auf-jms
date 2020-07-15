package org.ehp246.aufjms.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.ehp246.aufjms.core.configuration.ConnectionConfiguration;
import org.ehp246.aufjms.core.configuration.JsonMessageConfiguration;
import org.ehp246.aufjms.core.configuration.JsonProviderSelector;
import org.ehp246.aufjms.core.configuration.PooledExecutorConfiguration;
import org.ehp246.aufjms.core.endpoint.AtEndpointFactory;
import org.ehp246.aufjms.core.endpoint.EnableForMsgRegistrar;
import org.ehp246.aufjms.core.endpoint.MsgEndpointConfigurer;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ConnectionConfiguration.class, EnableForMsgRegistrar.class, AtEndpointFactory.class,
		MsgEndpointConfigurer.class, PooledExecutorConfiguration.class, JsonMessageConfiguration.class,
		JsonProviderSelector.class })
public @interface EnableForMsg {
	At[] value() default @At;

	@Retention(RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface At {
		/**
		 * Destination name of the incoming message.
		 *
		 * @return
		 */
		String value() default "";

		Class<?>[] scan() default {};
	}
}
