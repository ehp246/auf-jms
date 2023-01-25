package me.ehp246.aufjms.core.inbound;

import java.lang.reflect.Method;
import java.util.Objects;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocableFactory;
import me.ehp246.aufjms.api.inbound.InvocableTypeRegistry;
import me.ehp246.aufjms.api.inbound.InvocationModel;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Creates {@linkplain Invocable} instance by
 * {@linkplain AutowireCapableBeanFactory}.
 *
 * @author Lei Yang
 *
 */
final class AutowireCapableInvocableFactory implements InvocableFactory {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final InvocableTypeRegistry registry;

    public AutowireCapableInvocableFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final InvocableTypeRegistry registry) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.registry = registry;
    }

    @Override
    public Invocable get(final JmsMsg msg) {
        Objects.requireNonNull(msg);

        final var registered = this.registry.resolve(msg);
        if (registered == null) {
            return null;
        }

        final Object instance = registered.scope().equals(InstanceScope.BEAN)
                ? autowireCapableBeanFactory.getBean(registered.instanceType())
                : autowireCapableBeanFactory.createBean(registered.instanceType());

        return new Invocable() {

            @Override
            public Object instance() {
                return instance;
            }

            @Override
            public Method method() {
                return registered.method();
            }

            @Override
            public InvocationModel invocationModel() {
                return registered.model();
            }

            @Override
            public void close() throws Exception {
                if (registered.scope() == InstanceScope.BEAN) {
                    return;
                }
                autowireCapableBeanFactory.destroyBean(instance);
            }
        };
    }
}
