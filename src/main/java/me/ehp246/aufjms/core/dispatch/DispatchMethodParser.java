package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.dispatch.ByJmsProxyConfig;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface DispatchMethodParser {
    DispatchMethodBinder parse(final Method method, final ByJmsProxyConfig config);
}
