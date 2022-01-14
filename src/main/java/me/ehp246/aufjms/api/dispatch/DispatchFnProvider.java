package me.ehp246.aufjms.api.dispatch;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface DispatchFnProvider {
    DispatchFn get(String connectionFactoryName);
}
