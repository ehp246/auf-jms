package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.jms.JMSContext;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.ContextProvider;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByJms {
    At value() default @At;

    /**
     * The name of the {@linkplain JMSContext} used to send {@linkplain JmsDispatch}
     * messages. Provided by the {@linkplain ContextProvider} bean.
     */
    String context() default "";

    /**
     * Defines an optional bean name by which the proxy interface can be injected.
     * <p>
     * The default name is {@link Class#getSimpleName()}.
     * 
     * @return the bean name of the proxy interface.
     */
    String name() default "";

    /**
     * Spring Property supported.
     */
    String ttl() default "";

    At replyTo() default @At;

    @interface At {
        /**
         * Defines the destination name for the proxy interface.
         */
        String value() default "";

        DestinationType type() default DestinationType.QUEUE;
    }
}
