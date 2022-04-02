package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.jms.DestinationType;
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
        From value();

        Class<?>[] scan() default {};

        String concurrency() default "0";

        /**
         * The bean name of the endpoint. Must be unique if specified.
         * <p>
         * Does not support Spring property.
         */
        String name() default "";

        /**
         * Specifies whether the listener should be started automatically.
         * <p>
         * Supports Spring property.
         */
        String autoStartup() default "true";

        String connectionFactory() default "";

        String failedMsgConsumer() default "";

        @interface From {
            /**
             * Defines the destination name.
             */
            String value();

            DestinationType type() default DestinationType.QUEUE;

            /**
             * Specifies the JMS message selector expression (or null if none) for this
             * listener.
             * <p>
             * Default is none.
             * <p>
             * See the JMS specification for a detailed definition of selector expressions.
             * <p>
             * Supports Spring property.
             */
            String selector() default "";

            Sub sub() default @Sub;

            @interface Sub {
                /**
                 * Defines the subscription name to be used with a Topic consumer.
                 * <p>
                 * Only applicable when {@linkplain From#type()} is
                 * {@linkplain DestinationType#TOPIC}.
                 * <p>
                 * Supports Spring property.
                 */
                String value() default "";

                /**
                 * Specifies whether the subscription should be shared or not.
                 * <p>
                 * Defaults to <code>true</code>.
                 */
                boolean shared() default true;

                /**
                 * Specifies whether the subscription should be durable or not.
                 * <p>
                 * Defaults to <code>true</code>.
                 */
                boolean durable() default true;
            }
        }
    }
}
