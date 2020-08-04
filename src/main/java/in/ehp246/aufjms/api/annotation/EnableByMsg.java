package in.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import in.ehp246.aufjms.core.bymsg.ByMsgFactory;
import in.ehp246.aufjms.core.bymsg.ByMsgRegistrar;
import in.ehp246.aufjms.core.bymsg.ReplyEndpointConfiguration;
import in.ehp246.aufjms.core.bymsg.ReplyToNameSupplierFactory;
import in.ehp246.aufjms.core.configuration.ConnectionConfiguration;
import in.ehp246.aufjms.core.configuration.JsonMessageConfiguration;
import in.ehp246.aufjms.core.configuration.JsonProviderSelector;
import in.ehp246.aufjms.core.configuration.PooledExecutorConfiguration;
import in.ehp246.aufjms.core.endpoint.MsgEndpointConfigurer;

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
