package me.ehp246.aufjms.core.formsg;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.ExecutableTypeResolver;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocationModel;
import me.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import me.ehp246.aufjms.api.jms.Received;

/**
 * Resolves an Action by the given registry to a bean/object created by the
 * given bean factory.
 *
 * @author Lei Yang
 *
 */
public class AutowireCapableInstanceResolver implements ExecutableResolver {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final ExecutableTypeResolver typeResolver;
    private Consumer<ExecutedInstance> executedConsumer;

    public AutowireCapableInstanceResolver(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final ExecutableTypeResolver resolver, final Consumer<ExecutedInstance> executedConsumer) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.typeResolver = resolver;
        this.executedConsumer = executedConsumer;
    }

    public AutowireCapableInstanceResolver withPostExecutionConsumer(final Consumer<ExecutedInstance> consumer) {
        this.executedConsumer = consumer;
        return this;
    }

    @Override
    public ResolvedExecutable resolve(final Received msg) {
        Objects.requireNonNull(msg);

        final var registered = this.typeResolver.resolve(msg);
        if (registered == null) {
            return null;
        }

        final Object executableInstance = registered.getScope().equals(InstanceScope.BEAN)
                ? autowireCapableBeanFactory.getBean(registered.getInstanceType())
                : autowireCapableBeanFactory.createBean(registered.getInstanceType());

        return new ResolvedExecutable() {

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
            public Consumer<ExecutedInstance> postExecution() {
                return executedConsumer;
            }

        };

    }
}
