package me.ehp246.aufjms.api.endpoint;

import java.util.concurrent.Executor;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ExecutorProvider {
    Executor get(int maxPoolSize);
}
