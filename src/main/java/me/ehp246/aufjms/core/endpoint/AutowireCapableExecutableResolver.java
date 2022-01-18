package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.InvokableResolver;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * Resolves an Executable instance by the given registry to a bean/object
 * created by the given bean factory.
 *
 * @author Lei Yang
 *
 */
public final class AutowireCapableExecutableResolver implements ExecutableResolver {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final InvokableResolver typeResolver;

    public AutowireCapableExecutableResolver(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final InvokableResolver resolver) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.typeResolver = resolver;
    }


    @Override
    public Executable resolve(final JmsMsg msg) {
        Objects.requireNonNull(msg);

        final var registered = this.typeResolver.resolve(msg);
        if (registered == null) {
            return null;
        }

        final Object executableInstance = registered.getScope().equals(InstanceScope.BEAN)
                ? autowireCapableBeanFactory.getBean(registered.getInstanceType())
                : autowireCapableBeanFactory.createBean(registered.getInstanceType());

        return new Executable() {

            @Override
            public Method getMethod() {
                return registered.getMethod();
            }

            @Override
            public Object getInstance() {
                return executableInstance;
            }

            @Override
            public InvocationModel getInvocationModel() {
                return registered.getInvocationModel();
            }

            @Override
            public Consumer<ExecutedInstance> executionConsumer() {
                return null;
            }

        };

    }
}
