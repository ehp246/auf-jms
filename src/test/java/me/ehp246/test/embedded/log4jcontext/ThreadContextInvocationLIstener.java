package me.ehp246.test.embedded.log4jcontext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.InvocationListener;
import me.ehp246.aufjms.api.inbound.Invoked.Completed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;

/**
 * @author Lei Yang
 *
 */
class ThreadContextInvocationLIstener
        implements InvocationListener.OnCompleted, InvocationListener.OnFailed, InvocationListener.OnInvoking {
    private CompletableFuture<Map<String, String>> ref = new CompletableFuture<>();
    private final Map<String, String> map = new HashMap<String, String>();

    @Override
    public void onInvoking(final BoundInvocable bound) {
        map.clear();
        map.putAll(ThreadContext.getContext());
    }

    @Override
    public void onFailed(final Failed failed) {
        map.putAll(ThreadContext.getContext());
        ref.complete(map);
    }

    @Override
    public void onCompleted(final Completed completed) {
        map.putAll(ThreadContext.getContext());
        ref.complete(map);
    }

    Map<String, String> take() throws InterruptedException, ExecutionException {
        ref.get();

        ref = new CompletableFuture<Map<String, String>>();

        return map;
    }
}
