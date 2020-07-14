package org.ehp246.aufjms.api.endpoint;

import java.util.List;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ForMsgRegistry {
	void register(ForMsgInvokingDefinition invokingDefinition);

	List<ForMsgInvokingDefinition> getRegistered();
}
