package org.ehp246.aufjms.api.endpoint;

import java.util.concurrent.CompletableFuture;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ActionExecutor {
	CompletableFuture<ExecutedInstance> submit(BoundInstance task);
}
