package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.core.bymsg.ByMsgFactory;
import me.ehp246.aufjms.core.bymsg.ByMsgRegistrar;
import me.ehp246.aufjms.core.bymsg.ReplyEndpointConfiguration;
import me.ehp246.aufjms.core.bymsg.ReplyToNameSupplierFactory;
import me.ehp246.aufjms.core.configuration.ConnectionConfiguration;
import me.ehp246.aufjms.core.configuration.JsonMessageConfiguration;
import me.ehp246.aufjms.core.configuration.JsonProviderSelector;
import me.ehp246.aufjms.core.configuration.PooledExecutorConfiguration;
import me.ehp246.aufjms.core.endpoint.MsgEndpointConfigurer;

/**
 * Enables
 *
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ConnectionConfiguration.class, ReplyEndpointConfiguration.class, ByMsgRegistrar.class, ByMsgFactory.class,
		ReplyToNameSupplierFactory.class, MsgEndpointConfigurer.class, PooledExecutorConfiguration.class,
		JsonMessageConfiguration.class, JsonProviderSelector.class })
public @interface EnableByMsg {
	Class<?>[] scan() default {};

	String replyTo() default "";
}
