package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import jakarta.jms.Connection;
import jakarta.jms.MessageProducer;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;
import me.ehp246.aufjms.core.dispatch.ByJmsProxyFactory;
import me.ehp246.aufjms.core.dispatch.DefaultDispatchMethodParser;
import me.ehp246.aufjms.core.dispatch.EnableByJmsBeanFactory;
import me.ehp246.aufjms.core.dispatch.EnableByJmsRegistrar;

/**
 * Enables client-side capabilities of Auf JMS.
 * <p>
 * E.g., {@linkplain ByJms}-annotated interfaces scanning and registration.
 * <p>
 * By default, the package and the sub-packages of the annotated class will be
 * scanned.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufJmsConfiguration.class, EnableByJmsRegistrar.class, EnableByJmsBeanFactory.class,
        ByJmsProxyFactory.class, DefaultDispatchMethodParser.class })
public @interface EnableByJms {
    /**
     * Specifies the packages to scan for {@linkplain ByJms} interfaces.
     * <p>
     * By default, all sub-packages under {@linkplain EnableByJms}-annotated class
     * are scanned.
     */
    Class<?>[] scan() default {};

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
     * The value can be overridden by higher-priority sources from
     * {@linkplain ByJms} proxies. The default is no delay.
     * <p>
     * Supports Spring property placeholder.
     */
    String delay() default "";

    /**
     * Specifies whether to register {@linkplain JmsDispatchFn} beans backed by a
     * {@linkplain Connection} retrieved from the named connection factories.
     * <p>
     * The values are passed to {@linkplain ConnectionFactoryProvider#get(String)}
     * to create the beans.
     * <p>
     * The bean name is of the form <code>'jmsDispatchFn-${index}'</code> where the
     * index starts at <code>0</code>. E.g., {@code jmsDispatchFn-0},
     * {@code jmsDispatchFn-1}, and etc.
     * <p>
     * Does not support Spring property placeholder.
     */
    String[] dispatchFns() default {};

    /**
     * Specifies the destination where the replies to out-going requests are
     * expected to arrive.
     * <p>
     * It turns on the support of request/reply pattern.
     * <p>
     * Default is without request/reply support.
     *
     * @see <a href=
     *      'https://docs.oracle.com/cd/E19316-01/820-6424/aerby/index.html'>The
     *      Request-Reply Pattern</a>
     */
    To requestReplyTo() default @To("");
}
