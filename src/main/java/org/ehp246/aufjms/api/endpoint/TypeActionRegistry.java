package org.ehp246.aufjms.api.endpoint;

import java.util.List;

/**
 * 
 * @author Lei Yang
 *
 */
public interface TypeActionRegistry {
	void register(TypeActionDefinition actionDefinition);

	List<TypeActionDefinition> getRegistered();
}
