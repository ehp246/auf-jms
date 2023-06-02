package me.ehp246.aufjms.core.dispatch;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
sealed interface InvocationReturnBinder {
}

@FunctionalInterface
non-sealed interface LocalReturnBinder extends InvocationReturnBinder {
    Object apply(JmsDispatch dispatch);
}

@FunctionalInterface
non-sealed interface RemoteReturnBinder extends InvocationReturnBinder {
    Object apply(JmsDispatch dispatch, CompletableFuture<JmsMsg> replyFuture) throws Exception;
}
