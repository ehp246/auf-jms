package org.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.ehp246.aufjms.api.jms.MessageSupplier;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * @author Lei Yang
 *
 */
public class ReplyExecutedAction implements Consumer<ExecutedInstance> {
	private final MessagePortProvider portBinder;

	public ReplyExecutedAction(final MessagePortProvider portProvider) {
		super();
		this.portBinder = portProvider;
	}

	@Override
	public void accept(ExecutedInstance instance) {
		final Msg msg = instance.getMsg();
		if (msg.getReplyTo() == null) {
			return;
		}

		portBinder.get(msg::getReplyTo).accept(new MessageSupplier() {
			private final InvocationOutcome<?> outcome = instance.getOutcome();

			@Override
			public String getType() {
				return msg.getType();
			}

			@Override
			public String getCorrelationId() {
				return msg.getCorrelationId();
			}

			@Override
			public List<?> getBodyValues() {
				return outcome.hasReturned() ? List.of(instance.getOutcome().getReturned())
						: List.of(outcome.getThrown());
			}

			@Override
			public boolean isException() {
				return outcome.hasThrown();
			}

			@Override
			public Long getTtl() {
				return msg.getTtl();
			}

			@Override
			public String getGroupId() {
				return msg.getGroupId();
			}

		});
	}

}
