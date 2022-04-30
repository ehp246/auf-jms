package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.Connection;
import javax.jms.MessageProducer;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;
import me.ehp246.aufjms.core.dispatch.ByJmsBeanFactory;
import me.ehp246.aufjms.core.dispatch.DefaultInvocationDispatchBuilder;
import me.ehp246.aufjms.core.dispatch.EnableByJmsBeanFactory;
import me.ehp246.aufjms.core.dispatch.EnableByJmsRegistrar;

/**
 * Enables {@link ByJms}-annotated proxy interfaces scanning.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufJmsConfiguration.class, EnableByJmsRegistrar.class, EnableByJmsBeanFactory.class, ByJmsBeanFactory.class,
        DefaultInvocationDispatchBuilder.class })
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
     * Specifies whether to register {@linkplain JmsDispatchFn} beans backed by a
     * {@linkplain Connection} retrieved from the named connection factories.
     * <p>
     * The values are passed to {@linkplain ConnectionFactoryProvider#get(String)}
     * as connection name to create the bean.
     * <p>
     * The bean name is of the form <code>JmsDistpchFn-${index}</code> where the
     * index starts at 0. E.g., {@code JmsDispatchFn-0}, {@code JmsDispatchFn-1},
     * and etc.
     * <p>
     * Does not support Spring property placeholder.
     */
    String[] dispatchFns() default {};
}
