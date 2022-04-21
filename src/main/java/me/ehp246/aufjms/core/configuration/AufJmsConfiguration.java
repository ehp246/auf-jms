package me.ehp246.aufjms.core.configuration;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.dispatch.DefaultDispatchFnProvider;
import me.ehp246.aufjms.core.dispatch.DispatchLogger;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Import({ JsonByJackson.class, DispatchLogger.class, DefaultDispatchFnProvider.class })
public final class AufJmsConfiguration {
    @Bean
    public PropertyResolver propertyResolver(final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }

    @Bean
    public ConnectionFactoryProvider connectionFactoryProvider(final BeanFactory beanFactory) {
        return name -> {
            if (name == null || name.isBlank()) {
                return beanFactory.getBean(ConnectionFactory.class);
            }
            return beanFactory.getBean(name, ConnectionFactory.class);
        };
    }
}
