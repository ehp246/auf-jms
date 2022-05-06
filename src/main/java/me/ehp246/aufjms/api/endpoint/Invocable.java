package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface Invocable {
    Object instance();

    Method method();

    /**
     * If specified, the {@linkplain AutoCloseable} will be invoked by
     * {@linkplain InboundEndpoint} after the {@linkplain Invoking} method has
     * completed either normally or with an exception.
     * <p>
     * The API is intended for best-effort clean-up purpose. Exception is logged and
     * ignored. It does not impact further execution of the {@linkplain ForJmsType}.
     */
    default AutoCloseable closeable() {
        return null;
    }

    default InvocationModel invocationModel() {
        return InvocationModel.DEFAULT;
    }
}
