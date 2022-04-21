package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Queue;

import javax.jms.Connection;
import javax.jms.Topic;

import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByJms {
    /**
     * Specifies the destination name and type, i.e., {@linkplain Queue} vs
     * {@linkplain Topic} for out-bound messages.
     */
    To value();

    /**
     * Specifies an optional bean name by which the proxy interface can be injected.
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

    /**
     * Specifies the connection factory name with which the interface retrieves a
     * {@linkplain Connection} from
     * {@linkplain ConnectionFactoryProvider#get(String)}.
     */
    String connectionFactory() default "";

    @interface To {
        /**
         * Specifies the destination name for the proxy interface.
         * <p>
         * Supports Spring property placeholder.
         */
        String value();

        /**
         * Specifies the destination type.
         * <p>
         * Defaults to {@linkplain Queue}.
         */
        DestinationType type() default DestinationType.QUEUE;
    }
}
