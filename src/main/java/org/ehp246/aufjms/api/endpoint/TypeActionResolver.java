package org.ehp246.aufjms.api.endpoint;

import java.util.List;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface TypeActionResolver {
	List<ResolvedTypeAction> resolve(String type);
}
