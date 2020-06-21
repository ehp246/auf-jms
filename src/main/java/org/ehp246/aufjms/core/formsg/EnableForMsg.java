package org.ehp246.aufjms.core.formsg;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.ehp246.aufjms.activemq.PrefixedNameResolver;
import org.ehp246.aufjms.core.configuration.ActionExecutorConfiguration;
import org.ehp246.aufjms.core.configuration.ConnectionConfiguration;
import org.ehp246.aufjms.core.endpoint.AtEndpointFactory;
import org.ehp246.aufjms.core.endpoint.ForMsgRegistrar;
import org.ehp246.aufjms.core.endpoint.MsgEndpointConfigurer;
import org.ehp246.aufjms.core.jackson.JacksonConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ConnectionConfiguration.class, ForMsgRegistrar.class, AtEndpointFactory.class, MsgEndpointConfigurer.class,
		ActionExecutorConfiguration.class, JacksonConfiguration.class, PrefixedNameResolver.class })
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
