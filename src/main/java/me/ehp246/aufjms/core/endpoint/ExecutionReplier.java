package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;

/**
 * @author Lei Yang
 *
 */
public final class ExecutionReplier {
    private final JmsDispatchFn dispatchFn;

    public ExecutionReplier(final JmsDispatchFn dispatchFn) {
        super();
        this.dispatchFn = dispatchFn;
    }

    public void apply(final ExecutedInstance executed) {
        final var msg = executed.getMsg();
        final var replyTo = msg.replyTo();

        if (replyTo == null) {
            return;
        }

        this.dispatchFn.send(new JmsDispatch() {
            @Override
            public String type() {
                return msg.type();
            }

            @Override
            public String correlationId() {
                return msg.correlationId();
            }

            @Override
            public At to() {
                return new AtQueue() {

                    @Override
                    public String name() {
                        return null;
                    }
                };
            }
        });
    }
}
