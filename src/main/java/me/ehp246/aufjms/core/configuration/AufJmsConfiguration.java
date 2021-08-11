package me.ehp246.aufjms.core.configuration;

import javax.jms.Connection;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.jms.ConnectionProvider;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsConfiguration {
    @Bean
    public PropertyResolver propertyResolver(final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }
}
