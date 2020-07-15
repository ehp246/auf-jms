package org.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.function.Consumer;

import org.ehp246.aufjms.api.endpoint.ExecutedInstance;
import org.ehp246.aufjms.api.exception.ExecutionThrown;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.ehp246.aufjms.api.jms.MsgSupplier;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * @author Lei Yang
 *
 */
public class ReplyExecuted implements Consumer<ExecutedInstance> {
	private final MessagePortProvider portProvider;

	public ReplyExecuted(final MessagePortProvider portProvider) {
		super();
		this.portProvider = portProvider;
	}

	@Override
	public void accept(final ExecutedInstance instance) {
		final Msg msg = instance.getMsg();
		if (msg.getReplyTo() == null) {
			return;
		}

		portProvider.get(msg::getReplyTo).accept(new MsgSupplier() {
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
				return outcome.hasReturned()
						? outcome.getReturned() != null ? List.of(outcome.getReturned()) : List.of()
						: List.of(new ExecutionThrown() {

							@Override
							public Integer getCode() {
								final var thrown = outcome.getThrown();
								if (thrown instanceof ExecutionThrown) {
									return ((ExecutionThrown) thrown).getCode();
								}
								return null;
							}

							@Override
							public String getMessage() {
								return outcome.getThrown().getMessage();
							}

						});
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

			@Override
			public String getInvoking() {
				return msg.getInvoking();
			}
		});
	}

}
