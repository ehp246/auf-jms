package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Queue;

import jakarta.jms.Connection;
import jakarta.jms.MessageProducer;
import jakarta.jms.Topic;
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
     * Specifies a value for {@linkplain MessageProducer#setTimeToLive(long)} that
     * applies to all out-bound messages from the application.
     * <p>
     * The value can be overridden by higher-priority sources from
     * {@linkplain ByJms} proxies. The default is no TTL.
     * <p>
     * Supports Spring property placeholder.
     */
    String ttl() default "";

    /**
     * Specifies a value for {@linkplain MessageProducer#setDeliveryDelay(long)}
     * that applies to all out-bound messages from the application.
     * <p>
     * This value overwrites {@linkplain EnableByJms#delay()}.
     * <p>
     * The default is no delay.
     * <p>
     * Supports Spring property placeholder.
     */
    String delay() default "";

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
