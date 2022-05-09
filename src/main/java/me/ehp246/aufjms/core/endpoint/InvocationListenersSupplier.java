package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.InvocationListener.OnCompleted;
import me.ehp246.aufjms.api.endpoint.InvocationListener.OnFailed;

/**
 * Supports <code>null</code>.
 * 
 * @author Lei Yang
 *
 */
record InvocationListenersSupplier(OnCompleted completedListener,
        OnFailed failedInterceptor) {
    InvocationListenersSupplier() {
        this(null, null);
    }
}
