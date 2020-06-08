package org.ehp246.aufjms.api.endpoint;

import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ActionRegistration {
	ResolvedTypeAction match(Msg msg);
	
	default InstanceScope getScope() {
		return InstanceScope.MESSAGE;
	}
	
	default ExecutionModel getExecutionModel() {
		return ExecutionModel.DEFAULT;
	}
}
