package me.ehp246.aufjms.core.configuration;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.endpoint.ExecutorProvider;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class ExecutorConfiguration {
    @Bean
    public ExecutorProvider executorProvider() {
        // All in-line for now.
        return n -> null;
    }
}
