package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;
import me.ehp246.aufjms.core.configuration.ExecutorConfiguration;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableBinder;
import me.ehp246.aufjms.core.endpoint.InboundEndpointFactory;
import me.ehp246.aufjms.core.endpoint.InboundEndpointRegistrar;
import me.ehp246.aufjms.core.endpoint.InboundListenerConfigurer;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufJmsConfiguration.class, InboundEndpointRegistrar.class, InboundEndpointFactory.class,
        InboundListenerConfigurer.class, ExecutorConfiguration.class, DefaultExecutableBinder.class })
public @interface EnableForJms {
    Inbound[] value();

    @Retention(RUNTIME)
    @interface Inbound {
        /**
         * Destination of the incoming messages.
         */
        At value();

        Class<?>[] scan() default {};

        String concurrency() default "0";

        /**
         * The bean name of the endpoint.
         */
        String name() default "";

        /**
         * Specifies whether the listener should be started automatically.
         * <p>
         * Supports Spring property.
         */
        String autoStartup() default "true";

        boolean shared() default true;

        boolean durable() default true;

        /**
         * Defines the subscription name to be used with a Topic consumer.
         * <p>
         * Only applicable to Topic's.
         * <p>
         * Supports Spring property.
         */
        String subscriptionName() default "";
    }
}
