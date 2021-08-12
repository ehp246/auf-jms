package me.ehp246.aufjms.core.configuration;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.spi.PropertyResolver;

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
