package me.ehp246.aufjms.core.endpoint;

import java.util.Objects;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableTypeRegistry;
import me.ehp246.aufjms.api.endpoint.InvocableResolver;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Resolves an Executable instance by the given registry to a bean/object
 * created by the given bean factory.
 *
 * @author Lei Yang
 *
 */
public final class AutowireCapableInvocableResolver implements InvocableResolver {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final InvocableTypeRegistry registry;

    public AutowireCapableInvocableResolver(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final InvocableTypeRegistry registry) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.registry = registry;
    }

    @Override
    public Invocable resolve(final JmsMsg msg) {
        Objects.requireNonNull(msg);

        final var registered = this.registry.resolve(msg);
        if (registered == null) {
            return null;
        }

        final Object instance = registered.scope().equals(InstanceScope.BEAN)
                ? autowireCapableBeanFactory.getBean(registered.instanceType())
                : autowireCapableBeanFactory.createBean(registered.instanceType());

        return new InvocableRecord(instance, registered.method(),
                registered.scope() == InstanceScope.BEAN ? null
                        : () -> autowireCapableBeanFactory.destroyBean(instance),
                registered.invocationModel());
    }
}
