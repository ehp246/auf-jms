package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.core.byjms.ByJmsFactory;
import me.ehp246.aufjms.core.byjms.ByMsgRegistrar;
import me.ehp246.aufjms.core.byjms.ReplyEndpointConfiguration;
import me.ehp246.aufjms.core.byjms.ReplyToNameSupplierFactory;
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
@Import({ ConnectionConfiguration.class, ReplyEndpointConfiguration.class, ByMsgRegistrar.class, ByJmsFactory.class,
        ReplyToNameSupplierFactory.class, MsgEndpointConfigurer.class, PooledExecutorConfiguration.class,
        JsonMessageConfiguration.class, JsonProviderSelector.class })
public @interface EnableByMsg {
    Class<?>[] scan() default {};

    String replyTo() default "";
}
