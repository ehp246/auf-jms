package org.ehp246.aufjms.core.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.ActionInstanceResolver;
import org.ehp246.aufjms.api.endpoint.ExecutableInstance;
import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.MsgDispatcher;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.Msg;

/**
 * 
 * @author Lei Yang
 *
 */
public class DefaultMsgDispatcher implements MsgDispatcher {
	private final ActionInstanceResolver actionResolver;
	private final ActionExecutor actionExecutor;
	private final List<Consumer<ExecutedInstance>> postPerforms = new ArrayList<>();

	public DefaultMsgDispatcher(final ActionInstanceResolver actionResolver, final ActionExecutor actionExecutor) {
		super();
		this.actionResolver = actionResolver;
		this.actionExecutor = actionExecutor;
	}

	public DefaultMsgDispatcher addPostConsumer(Consumer<ExecutedInstance> postPerform) {
		this.postPerforms.add(postPerform);
		return this;
	}

	@Override
	public void dispatch(final Msg msg) {
		final List<ResolvedInstance> actions;
		try {
			actions = this.actionResolver.get(msg);
			if (actions == null || actions.size() == 0) {
				return;
			}
		} catch (Exception e) {
			return;
		}

		actions.stream().forEach(instance -> this.actionExecutor.submit(new ExecutableInstance() {

			@Override
			public Msg getMsg() {
				return msg;
			}

			@Override
			public ResolvedInstance getResolvedInstance() {
				return instance;
			}

			@Override
			public List<Consumer<ExecutedInstance>> postPerforms() {
				return postPerforms;
			}

		}));
	}
}
