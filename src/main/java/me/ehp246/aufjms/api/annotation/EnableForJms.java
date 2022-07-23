package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.endpoint.InvocationListener;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;
import me.ehp246.aufjms.core.configuration.ExecutorConfiguration;
import me.ehp246.aufjms.core.endpoint.DefaultInvocableBinder;
import me.ehp246.aufjms.core.endpoint.InboundEndpointFactory;
import me.ehp246.aufjms.core.endpoint.InboundEndpointListenerConfigurer;
import me.ehp246.aufjms.core.endpoint.InboundEndpointRegistrar;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufJmsConfiguration.class, InboundEndpointRegistrar.class, InboundEndpointFactory.class,
        InboundEndpointListenerConfigurer.class, ExecutorConfiguration.class, DefaultInvocableBinder.class })
public @interface EnableForJms {
    Inbound[] value();

    String defaultConsumer() default "44fc3968-7eba-47a3-a7b4-54e2b365d027";

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
         * Does not support Spring property placeholder.
         */
        String name() default "";

        /**
         * Specifies whether the listener should be started automatically.
         * <p>
         * Supports Spring property placeholder.
         */
        String autoStartup() default "true";

        String connectionFactory() default "";

        /**
         * Specifies the bean name of the {@linkplain InvocationListener} type to
         * receive either {@linkplain Completed} or {@linkplain Failed} invocations on
         * this {@linkplain EnableForJms.Inbound}.
         * <p>
         * If the execution of a {@linkplain ForJmsType} object on this
         * {@linkplain EnableForJms.Inbound} completes normally, the
         * {@linkplain InvocationListener.OnCompleted#onCompleted(Completed)} will be
         * invoked.
         * <p>
         * If a {@linkplain RuntimeException} happens from the bean, the
         * {@linkplain Message} will follow broker's dead-lettering process.
         * <p>
         * If the execution of a {@linkplain ForJmsType} object on this
         * {@linkplain EnableForJms.Inbound} throws an exception, the
         * {@linkplain InvocationListener.OnFailed#onFailed(Failed)} will be invoked.
         * <p>
         * If the invocation of the bean completes without an exception, the
         * {@linkplain Message} will be <strong>acknowledged</strong> to the broker as a
         * success.
         * <p>
         * {@linkplain InvocationListener.OnFailed} can throw {@linkplain Exception} in
         * which case the message follows broker's dead-lettering process.
         * <p>
         * The listener bean is designed to support {@linkplain ForJmsType} objects. It
         * applies only after a matching {@linkplain ForJmsType} class has been found.
         * E.g., the bean will not be invoked for basic {@linkplain JMSException} prior
         * to {@linkplain Message#getJMSType()} matching, i.e.,
         * {@linkplain UnknownTypeException}.
         * <p>
         * Supports Spring property placeholder.
         */
        String invocationListener() default "";

        @interface From {
            /**
             * Defines the destination name.
             * <p>
             * Supports Spring property placeholder.
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
             * Supports Spring property placeholder.
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
                 * Supports Spring property placeholder.
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
