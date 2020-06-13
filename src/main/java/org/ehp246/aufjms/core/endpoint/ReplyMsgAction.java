package org.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.jms.MessageSupplier;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.api.jms.MessagePipe;
import org.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * @author Lei Yang
 *
 */
public class ReplyMsgAction implements Consumer<ExecutedInstance> {
	private final MessagePipe pipe;

	public ReplyMsgAction(final MessagePipe pipe) {
		super();
		this.pipe = pipe;
	}

	@Override
	public void accept(ExecutedInstance instance) {
		final Msg msg = instance.getMq();
		if (msg.getReplyTo() == null) {
			return;
		}

		pipe.take(new MessageSupplier() {
			private final InvocationOutcome<?> outcome = instance.getOutcome();

			@Override
			public String getType() {
				return msg.getType();
			}

			@Override
			public String getTo() {
				// TODO
				return null;// msg.getReplyTo();
			}

			@Override
			public String getCorrelationId() {
				return msg.getCorrelationId();
			}

			@Override
			public List<?> getBodyValue() {
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
