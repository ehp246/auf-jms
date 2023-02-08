package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Import;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import me.ehp246.aufjms.api.exception.UnknownTypeException;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;
import me.ehp246.aufjms.api.inbound.MsgConsumer;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;
import me.ehp246.aufjms.core.configuration.ExecutorConfiguration;
import me.ehp246.aufjms.core.inbound.AnnotatedInboundEndpointRegistrar;
import me.ehp246.aufjms.core.inbound.DefaultInvocableBinder;
import me.ehp246.aufjms.core.inbound.InboundEndpointFactory;
import me.ehp246.aufjms.core.inbound.InboundEndpointListenerConfigurer;

/**
 * Enables the server-side capabilities of Auf JMS.
 * <p>
 * Mostly to declare {@linkplain Inbound} endpoints and scanning and
 * registration of {@linkplain ForJmsType} classes for the endpoints.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufJmsConfiguration.class, AnnotatedInboundEndpointRegistrar.class, InboundEndpointFactory.class,
        InboundEndpointListenerConfigurer.class, ExecutorConfiguration.class, DefaultInvocableBinder.class })
public @interface EnableForJms {
    /**
     * Specifies the destinations to listen for incoming messages and their
     * configurations.
     */
    Inbound[] value();

    /**
     * Specifies the bean name of {@linkplain MsgConsumer} type to receive any
     * message that no matching {@linkplain Invocable} can be found for its
     * {@linkplain Message#getJMSType()}.
     * <p>
     * The default value specifies a no-operation bean that logs the un-matched
     * message by {@linkplain Logger#atDebug()}. This means un-matched messages are
     * to be expected and acknowledged to the broker without triggering the
     * dead-lettering process.
     * <p>
     * If the value is an empty string, an un-matched message will result an
     * {@linkplain UnknownTypeException} thus trigger the dead-lettering process.
     * <p>
     * The setting applies to all {@linkplain InboundEndpoint}'s.
     * <p>
     * Supports Spring property placeholder.
     */
    String defaultConsumer() default "44fc3968-7eba-47a3-a7b4-54e2b365d027";

    @Target({})
    @interface Inbound {
        /**
         * Destination of the incoming messages.
         */
        From value();

        /**
         * Specifies the packages to scan for {@linkplain ForJmsType} classes for this
         * endpoint.
         * <p>
         * By default, the package and the sub-packages of the annotated class will be
         * scanned.
         */
        Class<?>[] scan() default {};

        /**
         * Registers the specified {@linkplain ForJmsType}-annotated classes
         * individually.
         */
        Class<?>[] register() default {};

        /**
         * Not implemented.
         */
        String concurrency() default "0";

        /**
         * The bean name of the endpoint. Must be unique if specified.
         * <p>
         * The default name would be in the form of <code>'inboundEndpoint-${n}'</code>
         * where <code>'n'</code> is the index from {@linkplain EnableForJms#value()}
         * starting at <code>0</code>.
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

        /**
         * Defines the session mode for the endpoint.
         * <p>
         * Defaults to {@linkplain Session#SESSION_TRANSACTED}.
         *
         * @see Connection#createSession(int)
         */
        int sessionMode() default Session.SESSION_TRANSACTED;

        /**
         * Specifies the name to pass to {@linkplain ConnectionFactoryProvider} to
         * eventually retrieve a {@linkplain Connection}.
         */
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
         * If a {@linkplain RuntimeException} happens from the bean during execution,
         * the {@linkplain Message} will follow broker's dead-lettering process.
         * <p>
         * Supports Spring property placeholder.
         */
        String invocationListener() default "";

        @Target({})
        @interface From {
            /**
             * Defines the destination name.
             * <p>
             * Supports Spring property placeholder.
             */
            String value();

            /**
             * Specifies the {@linkplain From#value()} is a {@linkplain Queue} or
             * {@linkplain Topic}.
             */
            DestinationType type() default DestinationType.QUEUE;

            /**
             * Specifies the JMS message selector expression (or <code>null</code> if none)
             * for this listener.
             * <p>
             * Default is none.
             * <p>
             * See the JMS specification for a detailed definition of selector expressions.
             * <p>
             * Supports Spring property placeholder.
             *
             * @see <a href=
             *      'https://jakarta.ee/specifications/messaging/3.0/jakarta-messaging-spec-3.0.html#message-selection'>Selector</a>
             */
            String selector() default "";

            /**
             * Specifies the subscription configuration.
             * <p>
             * Only applicable when {@linkplain From#type()} is
             * {@linkplain DestinationType#TOPIC}.
             *
             * @see <a href=
             *      'https://jakarta.ee/specifications/messaging/3.0/apidocs/jakarta/jms/topicsubscriber'>TopicSubscriber</a>
             */
            Sub sub() default @Sub(name = "");

            @Target({})
            @interface Sub {
                /**
                 * Specifies the subscription name to be used with a {@linkplain Topic}
                 * consumer.
                 * <p>
                 * Only applicable when {@linkplain From.Sub#shared()} and/or
                 * {@linkplain From.Sub#durable()} is <code>true</code>.
                 * <p>
                 * Supports Spring property placeholder.
                 */
                String name();

                /**
                 * Specifies whether the subscription should be shared or not.
                 */
                boolean shared() default false;

                /**
                 * Specifies whether the subscription should be durable or not.
                 */
                boolean durable() default false;
            }
        }
    }
}
