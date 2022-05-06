package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.CompletedInvocationListener;
import me.ehp246.aufjms.api.endpoint.FailedInvocationInterceptor;

/**
 * Supports <code>null</code>.
 * 
 * @author Lei Yang
 *
 */
record InvocationListenersSupplier(CompletedInvocationListener completedListener,
        FailedInvocationInterceptor failedInterceptor) {

}
