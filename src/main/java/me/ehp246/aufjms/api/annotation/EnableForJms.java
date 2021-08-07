package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.core.configuration.PooledExecutorConfiguration;
import me.ehp246.aufjms.core.endpoint.AtEndpointConfigurer;
import me.ehp246.aufjms.core.endpoint.AtEndpointFactory;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableBinder;
import me.ehp246.aufjms.core.endpoint.EnableForJmsRegistrar;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 *
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ EnableForJmsRegistrar.class, AtEndpointFactory.class, AtEndpointConfigurer.class,
        PooledExecutorConfiguration.class, DefaultExecutableBinder.class, JsonByJackson.class })
public @interface EnableForJms {
    At[] value() default @At;

    @Retention(RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface At {
        /**
         * Destination name of the incoming message.
         *
         * @return
         */
        String value() default "";

        Class<?>[] scan() default {};

        String connection() default "";
    }
}
