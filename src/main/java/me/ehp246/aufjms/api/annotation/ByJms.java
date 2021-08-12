package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByJms {
    /**
     * Defines the destination name for the interface proxy.
     */
    String value() default "";

    /**
     * Defines an optional bean name by which the proxy interface can be injected.
     * <p>
     * The default name is {@link Class#getSimpleName()}.
     * 
     * @return the bean name of the proxy interface.
     */
    String name() default "";

    String ttl() default "";

    /**
     * The bean name of the JMS {@link Connection} to use for this proxy.
     */
    String connection() default "";

    /**
     * Defines the name of the {@link Destination} for
     * {@link Message#setJMSReplyTo(javax.jms.Destination)}.
     * <p>
     * The default value follows {@link EnableByJms#replyTo()}.
     */
    String replyTo() default "";
}
