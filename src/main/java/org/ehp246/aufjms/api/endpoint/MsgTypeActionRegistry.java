package org.ehp246.aufjms.api.endpoint;

import java.util.List;

/**
 * 
 * @author Lei Yang
 *
 */
public interface MsgTypeActionRegistry {
	void register(ForMsgExecutableDefinition actionDefinition);

	List<ForMsgExecutableDefinition> getRegistered();
}
