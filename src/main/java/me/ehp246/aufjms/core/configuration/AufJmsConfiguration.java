package me.ehp246.aufjms.core.configuration;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.endpoint.MsgConsumer;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.dispatch.DefaultDispatchFnProvider;
import me.ehp246.aufjms.core.dispatch.DispatchLogger;
import me.ehp246.aufjms.core.endpoint.NoopConsumer;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Import({ JsonByJackson.class, DefaultDispatchFnProvider.class })
public final class AufJmsConfiguration {
    @Bean("2392a7ae-3e11-4eeb-bd8c-cf54f5a1fa4b")
    public DispatchLogger dispatchLogger(
            @Value("${" + AufJmsConstants.DISPATCH_LOGTER + ":false}") final boolean enabled) {
        return enabled ? new DispatchLogger() : null;
    }

    @Bean("2744a1e7-9576-4f2e-8c56-6623247155e7")
    public PropertyResolver propertyResolver(final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }

    @Bean("90462ee7-99cd-4ce9-b299-89c983a8b069")
    public ConnectionFactoryProvider connectionFactoryProvider(final BeanFactory beanFactory) {
        return name -> {
            if (name == null || name.isBlank()) {
                return beanFactory.getBean(ConnectionFactory.class);
            }
            return beanFactory.getBean(name, ConnectionFactory.class);
        };
    }

    @Bean("44fc3968-7eba-47a3-a7b4-54e2b365d027")
    public MsgConsumer noopConsumer() {
        return new NoopConsumer();
    }
}
