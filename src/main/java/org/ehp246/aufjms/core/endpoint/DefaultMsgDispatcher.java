package org.ehp246.aufjms.core.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.BoundInstance;
import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.endpoint.ExecutingInstanceResolver;
import org.ehp246.aufjms.api.endpoint.MsgDispatcher;
import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.ehp246.aufjms.api.jms.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Lei Yang
 *
 */
public class DefaultMsgDispatcher implements MsgDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMsgDispatcher.class);

	private final ExecutingInstanceResolver actionResolver;
	private final ActionExecutor actionExecutor;
	private final List<Consumer<ExecutedInstance>> postPerforms = new ArrayList<>();

	public DefaultMsgDispatcher(final ExecutingInstanceResolver actionResolver, final ActionExecutor actionExecutor) {
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
		LOGGER.trace("Dispatching {}", msg.getType());

		final ResolvedInstance instance;
		try {
			instance = this.actionResolver.resolve(msg);
			if (instance == null) {
				LOGGER.info("Un-matched message type: {}", msg.getType());

				return;
			}
		} catch (Exception e) {
			LOGGER.error("Resolution failed: {}", e.getMessage());

			return;
		}

		LOGGER.trace("Submitting {}", msg.getType());

		this.actionExecutor.submit(new BoundInstance() {

			@Override
			public Msg getMsg() {
				return msg;
			}

			@Override
			public ResolvedInstance getResolvedInstance() {
				return instance;
			}
		});
	};
}
