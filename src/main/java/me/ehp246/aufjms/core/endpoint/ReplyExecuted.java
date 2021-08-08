package me.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.function.Consumer;

import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.exception.ExecutionThrown;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.MsgPortProvider;
import me.ehp246.aufjms.api.jms.MsgSupplier;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * @author Lei Yang
 *
 */
public class ReplyExecuted implements Consumer<ExecutedInstance> {
    private final MsgPortProvider portProvider;

    public ReplyExecuted(final MsgPortProvider portProvider) {
        super();
        this.portProvider = portProvider;
    }

    @Override
    public void accept(final ExecutedInstance instance) {
        final JmsMsg msg = instance.getMsg();
        if (msg.replyTo() == null) {
            return;
        }

        portProvider.get(msg::replyTo).accept(new MsgSupplier() {
            private final InvocationOutcome<?> outcome = instance.getOutcome();

            @Override
            public String getType() {
                return msg.type();
            }

            @Override
            public String getCorrelationId() {
                return msg.correlationId();
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
                return null;// msg.ttl();
            }

            @Override
            public String getGroupId() {
                return msg.groupId();
            }

            @Override
            public String getInvoking() {
                return msg.invoking();
            }
        });
    }

}
