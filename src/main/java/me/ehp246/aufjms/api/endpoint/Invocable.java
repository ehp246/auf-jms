package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface Invocable extends AutoCloseable {
    Object instance();

    Method method();

    default InvocationModel invocationModel() {
        return InvocationModel.DEFAULT;
    }

    /**
     * The {@linkplain AutoCloseable} will be invoked by
     * {@linkplain InboundEndpoint} after the {@linkplain Invoking} method completes
     * normally or fails throwing an exception.
     * <p>
     * The API is intended for best-effort clean-up purpose. Exception will be
     * logged and suppressed so that it does not impact further execution of the
     * {@linkplain ForJmsType}.
     */
    @Override
    default void close() throws Exception {
    }
}
