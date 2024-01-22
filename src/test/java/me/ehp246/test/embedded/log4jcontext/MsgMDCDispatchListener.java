package me.ehp246.test.embedded.log4jcontext;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.jms.JmsDispatch;

/**
 * @author Lei Yang
 *
 */
@Service
class MsgMDCDispatchListener implements DispatchListener.OnDispatch {
    private final AtomicReference<Map<String, String>> ref = new AtomicReference<>();

    @Override
    public void onDispatch(final JmsDispatch dispatch) {
        ref.set(ThreadContext.getContext());
    }

    Map<String, String> take() throws InterruptedException, ExecutionException {
        final var map = ref.get();
        ref.set(null);
        return map;
    }
}
