package me.ehp246.aufjms.api.inbound;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import me.ehp246.aufjms.api.annotation.ForJmsType;

/**
 * Indicates how an instance of a {@linkplain ForJmsType}-annotated class should
 * be instantiated.
 *
 * @author Lei Yang
 * @since 1.0
 * @see ForJmsType#scope()
 */
public enum InstanceScope {
    /**
     * Indicates that a bean of the class exists in {@linkplain ApplicationContext}
     * and the invocation instance should be retrieved by
     * {@linkplain ApplicationContext#getBean(Class)}. The creation of the bean is
     * up to {@linkplain ApplicationContext} and the bean definition.
     */
    BEAN,
    /**
     * Indicates that for each incoming message, a new instance of the class is to
     * be initiated, invoked, then discarded.
     * <p>
     * The instance is created via
     * {@linkplain AutowireCapableBeanFactory#createBean(Class)}.
     */
    MESSAGE
}
