package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;
import me.ehp246.aufjms.core.configuration.ExecutorConfiguration;
import me.ehp246.aufjms.core.endpoint.AtEndpointFactory;
import me.ehp246.aufjms.core.endpoint.AtEndpointListenerConfigurer;
import me.ehp246.aufjms.core.endpoint.AtEndpointRegistrar;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableBinder;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufJmsConfiguration.class, AtEndpointRegistrar.class, AtEndpointFactory.class, AtEndpointListenerConfigurer.class,
        ExecutorConfiguration.class, DefaultExecutableBinder.class, JsonByJackson.class })
public @interface EnableForJms {
    At[] value() default @At;

    @Retention(RUNTIME)
    @interface At {
        /**
         * Destination name of the incoming message.
         *
         * @return
         */
        String value() default "";

        String connection() default "";

        Class<?>[] scan() default {};

        String concurrency() default "0";

        String name() default "";
    }
}
