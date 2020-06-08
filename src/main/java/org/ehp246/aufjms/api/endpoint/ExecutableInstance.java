package org.ehp246.aufjms.api.endpoint;

import java.util.List;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ExecutableInstance {
	Msg getMsg();

	ResolvedInstance getResolvedInstance();

	List<Consumer<ExecutedInstance>> postPerforms();
}
