package org.ehp246.aufjms.api.endpoint;

import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ActionRegistration {
	ResolvedInstanceType match(Msg msg);
	
	default InstanceScope getScope() {
		return InstanceScope.MESSAGE;
	}
	
	default InvocationModel getExecutionModel() {
		return InvocationModel.DEFAULT;
	}
}
