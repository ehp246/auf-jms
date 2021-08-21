package me.ehp246.aufjms.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface At {
    /**
     * Defines the destination name for the proxy interface.
     */
    String value() default "";

    DestinationType type() default DestinationType.QUEUE;
}