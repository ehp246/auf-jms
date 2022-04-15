package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.core.configuration.AufJmsConfiguration;
import me.ehp246.aufjms.core.dispatch.ByJmsFactory;
import me.ehp246.aufjms.core.dispatch.ByJmsRegistrar;
import me.ehp246.aufjms.core.dispatch.DefaultInvocationDispatchBuilder;

/**
 * Enables {@link ByJms}-annotated proxy interfaces scanning.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufJmsConfiguration.class, ByJmsRegistrar.class, ByJmsFactory.class, DefaultInvocationDispatchBuilder.class })
public @interface EnableByJms {
    Class<?>[] scan() default {};

    /**
     * Spring property is supported.
     */
    String ttl() default "PT0S";

    /**
     * Specifies whether to register {@linkplain JmsDispatchFn} beans with the given
     * connection factory name.
     * <p>
     * The values are passed to {@linkplain ConnectionFactoryProvider#get(String)}
     * to create the bean.
     * <p>
     * The bean name is of the form <code>JmsDistpchFn-${index}</code> where the
     * index starts at 0. E.g., {@code JmsDispatchFn-0}, {@code JmsDispatchFn-1},
     * and etc.
     */
    String[] dispatchFns() default {};
}
