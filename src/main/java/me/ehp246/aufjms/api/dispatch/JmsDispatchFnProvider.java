package me.ehp246.aufjms.api.dispatch;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface JmsDispatchFnProvider {
    DispatchFn get(String connectionName);
}
