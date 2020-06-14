package org.ehp246.aufjms.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.ehp246.aufjms.core.bymsg.ByMsgFactory;
import org.ehp246.aufjms.core.bymsg.ByMsgRegistrar;
import org.ehp246.aufjms.core.bymsg.ReplyToNameSupplierFactory;
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
@Import({ ConnectionConfiguration.class, ReqResConfiguration.class, ByMsgRegistrar.class, ByMsgFactory.class,
		ReplyToNameSupplierFactory.class, JacksonConfiguration.class })
public @interface EnableByMsg {
	Class<?>[] scan() default {};

	String replyTo() default "";
}
