package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import me.ehp246.aufjms.api.exception.JmsDispatchException;
import me.ehp246.aufjms.api.jms.BodyOf;
import me.ehp246.aufjms.api.jms.FromJson;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.reflection.ReflectedProxyMethod;

/**
 * @author Lei Yang
 *
 */
final class DefaultProxyReturnParser {
    private final FromJson fromJson;

    DefaultProxyReturnParser(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    ProxyReturnBinder parse(final Method method) {
        final var reflected = new ReflectedProxyMethod(method);

        if (reflected.returnsVoid()) {
            return (LocalReturnBinder) dispatch -> null;
        }

        final var bodyOf = new BodyOf(method.getReturnType());
        return (RemoteReturnBinder) (jmsDispatch, replyFuture) -> {
            final JmsMsg msg;
            try {
                msg = jmsDispatch.replyTimeout() == null ? replyFuture.get()
                        : replyFuture.get(jmsDispatch.replyTimeout().toSeconds(), TimeUnit.SECONDS);
            } catch (Exception e) {
                if (reflected.isOnThrows(e.getClass())) {
                    throw e;
                }
                throw new JmsDispatchException(e);
            }

            return fromJson.apply(msg.text(), bodyOf);
        };
    }
}
