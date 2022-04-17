package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.endpoint.CompletedInvocationConsumer;
import me.ehp246.aufjms.api.endpoint.FailedInvocationInterceptor;

/**
 * Supports <code>null</code>.
 * 
 * @author Lei Yang
 *
 */
record InvocationListenersSupplier(CompletedInvocationConsumer completedConsumer,
        FailedInvocationInterceptor failedInterceptor) {

}
