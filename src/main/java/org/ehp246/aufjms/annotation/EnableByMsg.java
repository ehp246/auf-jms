package org.ehp246.aufjms.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.ehp246.aufjms.core.bymsg.ProxyFactory;
import org.ehp246.aufjms.core.bymsg.ProxyRegistrar;
import org.ehp246.aufjms.core.bymsg.ReqResConfiguration;
import org.ehp246.aufjms.core.configuration.ConnectionConfiguration;
import org.ehp246.aufjms.core.jackson.JacksonConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Enables
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ConnectionConfiguration.class, ReqResConfiguration.class, ProxyRegistrar.class, ProxyFactory.class,
		JacksonConfiguration.class })
public @interface EnableByMsg {
	Class<?>[] scanBasePackageClasses() default {};
}
