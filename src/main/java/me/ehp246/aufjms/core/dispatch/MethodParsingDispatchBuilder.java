package me.ehp246.aufjms.core.dispatch;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufjms.api.dispatch.ByJmsConfig;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchBuilder;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.spi.PropertyResolver;

/**
 * @author Lei Yang
 *
 */
public class MethodParsingDispatchBuilder implements InvocationDispatchBuilder {
    private static final Map<Method, ParsedMethodSupplier> CACHE = new ConcurrentHashMap<>();

    private final PropertyResolver propertyResolver;

    public MethodParsingDispatchBuilder(PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    @Override
    public JmsDispatch get(Object proxy, Method method, Object[] args, ByJmsConfig config) {
        return CACHE.computeIfAbsent(method, m -> ParsedMethodSupplier.parse(m, propertyResolver)).apply(config,
                args);
    }

}
