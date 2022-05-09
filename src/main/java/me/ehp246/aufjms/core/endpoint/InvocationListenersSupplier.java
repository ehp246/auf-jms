package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.InvocationListener.CompletedListener;
import me.ehp246.aufjms.api.endpoint.InvocationListener.FailedInterceptor;

/**
 * Supports <code>null</code>.
 * 
 * @author Lei Yang
 *
 */
record InvocationListenersSupplier(CompletedListener completedListener,
        FailedInterceptor failedInterceptor) {
    InvocationListenersSupplier() {
        this(null, null);
    }
}
