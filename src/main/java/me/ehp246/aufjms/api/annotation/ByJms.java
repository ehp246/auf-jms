package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.jms.Connection;
import jakarta.jms.MessageProducer;
import jakarta.jms.Topic;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * Indicates that the annotated interface should be implemented by Auf JMS as a
 * message producer and made available for injection.
 *
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
     * Specifies a bean name by which the interface can be injected.
     * <p>
     * The default is from {@link Class#getSimpleName()} with the first letter in
     * lower-case.
     *
     * @return the bean name of the proxy interface.
     * @see {@linkplain Qualifier}
     */
    String name() default "";

    /**
     * Specifies a value for {@linkplain MessageProducer#setTimeToLive(long)} that
     * applies to out-bound messages.
     * <p>
     * The value overwrites {@linkplain EnableByJms#ttl()}.
     * <p>
     * The value can be overridden by higher-priority sources, e.g.,
     * {@linkplain OfTtl}.
     * <p>
     * The default is to follow {@linkplain EnableByJms#ttl()}.
     * <p>
     * Supports Spring property placeholder.
     */
    String ttl() default "";

    /**
     * Specifies a value for {@linkplain MessageProducer#setDeliveryDelay(long)}
     * that applies to out-bound messages.
     * <p>
     * This value overwrites {@linkplain EnableByJms#delay()}.
     * <p>
     * The default is to follow {@linkplain EnableByJms#delay()}.
     * <p>
     * Supports Spring property placeholder.
     */
    String delay() default "";

    /**
     * Specifies the return destination for out-bound messages.
     */
    To replyTo() default @To("");

    /**
     * Specifies the connection factory name by which the interface retrieves a
     * {@linkplain Connection} from
     * {@linkplain ConnectionFactoryProvider#get(String)}.
     */
    String connectionFactory() default "";

    /**
     * Defines a destination.
     */
    @Target({})
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
