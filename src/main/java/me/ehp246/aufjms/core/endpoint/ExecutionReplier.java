package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.dispatch.DispatchFn;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.endpoint.ExecutedInstance;
import me.ehp246.aufjms.api.jms.AtDestination;

/**
 * @author Lei Yang
 *
 */
public final class ExecutionReplier {
    private final DispatchFn dispatchFn;

    public ExecutionReplier(final DispatchFn dispatchFn) {
        super();
        this.dispatchFn = dispatchFn;
    }

    public void apply(final ExecutedInstance executed) {
        final var msg = executed.getMsg();
        final var replyTo = msg.replyTo();

        if (replyTo == null) {
            return;
        }

        this.dispatchFn.dispatch(new JmsDispatch() {
            @Override
            public String type() {
                return msg.type();
            }

            @Override
            public String correlationId() {
                return msg.correlationId();
            }

            @Override
            public AtDestination at() {
                return new AtDestination() {

                    @Override
                    public String name() {
                        return null;
                    }
                };
            }
        });
    }
}
