package me.ehp246.aufjms.core.formsg;

import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.MsgEndpoint;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableTypeResolver;

/**
 *
 * @author Lei Yang
 *
 */
public final class AtEndpointFactory {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public AtEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    public MsgEndpoint newMsgEndpoint(final String destination, final Set<String> scanPackages) {
        return new MsgEndpoint() {
            private final ExecutableResolver resolver = new AutowireCapableInstanceResolver(autowireCapableBeanFactory,
                    newForMsgRegistry(scanPackages));

            @Override
            public String getDestinationName() {
                return destination;
            }

            @Override
            public ExecutableResolver getResolver() {
                return resolver;
            }

        };
    }

    private DefaultExecutableTypeResolver newForMsgRegistry(final Set<String> scanPackages) {
        return new DefaultExecutableTypeResolver().register(new ForMsgScanner(scanPackages).perform().stream());
    }
}
