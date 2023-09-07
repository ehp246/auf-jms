package me.ehp246.test.embedded.log4jcontext;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.jms.JmsDispatch;

/**
 * @author Lei Yang
 *
 */
class ThreadContextDispatchListener implements DispatchListener.OnDispatch {
    private final AtomicReference<String> ref = new AtomicReference<>();

    @Override
    public void onDispatch(final JmsDispatch dispatch) {
        ref.set(ThreadContext.get("OrderId"));
    }

    String take() {
        return ref.getAndSet(null);
    }
}
