package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.jms.BodyOf;
import me.ehp246.aufjms.api.jms.FromJson;

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
        final var returnType = method.getReturnType();

        if (returnType == void.class || returnType == Void.class) {
            return (LocalReturnBinder) dispatch -> null;
        }

        return (RemoteReturnBinder) (dispatch, msg) -> fromJson.apply(msg.text(), new BodyOf(returnType));
    }
}
