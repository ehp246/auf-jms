package me.ehp246.aufjms.core.configuration;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import me.ehp246.aufjms.api.endpoint.ExecutorProvider;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class ExecutorConfiguration {
    /**
     * The executor must implement CallerRunsPolicy.
     *
     * @param poolSize
     * @return
     */
    private ThreadPoolTaskExecutor newPooledExecutor(final int poolSize) {
        if (poolSize <= 0) {
            return null;
        }

        final var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(poolSize);
        threadPoolTaskExecutor.setQueueCapacity(-1);
        threadPoolTaskExecutor.setThreadNamePrefix("AufJms-Executor-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    public ExecutorProvider executorProvider() {
        // TODO: all in-line for now.
        return n -> null;
    }
}
