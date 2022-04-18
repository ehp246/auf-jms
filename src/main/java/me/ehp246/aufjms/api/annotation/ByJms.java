package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByJms {
    To value();

    /**
     * Defines an optional bean name by which the proxy interface can be injected.
     * <p>
     * The default name is {@link Class#getSimpleName()}.
     * 
     * @return the bean name of the proxy interface.
     */
    String name() default "";

    /**
     * Supports Spring property placeholder.
     */
    String ttl() default "";

    To replyTo() default @To("");

    String connectionFactory() default "";

    @interface To {
        /**
         * Defines the destination name for the proxy interface.
         * <p>
         * Supports Spring property.
         */
        String value();

        DestinationType type() default DestinationType.QUEUE;
    }
}
