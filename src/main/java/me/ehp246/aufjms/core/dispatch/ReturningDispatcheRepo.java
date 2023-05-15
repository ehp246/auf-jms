package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
interface ReturningDispatcheRepo {
    ReturningDispatch add(String correlationId, RemoteReturnBinder binder);

    ReturningDispatch take(String correlationId);

    record ReturningDispatch(RemoteReturnBinder binder, CompletableFuture<Object> future) implements Invocable {
        private final static Method COMPLETE;
        static {
            try {
                COMPLETE = ReturningDispatch.class.getMethod("", JmsMsg.class);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object instance() {
            return this;
        }

        @Override
        public Method method() {
            return COMPLETE;
        }

        public void complete(final JmsMsg msg) {
            this.future.complete(this.binder.apply(msg));
        }
    }
}
