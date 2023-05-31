package me.ehp246.aufjms.core.dispatch;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
final class ProxyInvocationHandler implements InvocationHandler {
    private final Class<?> proxyInterface;
    private final int hashCode = new Object().hashCode();
    private final JmsDispatchFn dispatchFn;
    private final Function<Method, DispatchMethodBinder> methodBinderSupplier;
    private final ReplyExpectedDispatchMap futureMap;

    ProxyInvocationHandler(final Class<?> proxyInterface, final JmsDispatchFn dispatchFn,
            final Function<Method, DispatchMethodBinder> methodBinderSupplier,
            final ReplyExpectedDispatchMap futureMap) {
        super();
        this.proxyInterface = proxyInterface;
        this.dispatchFn = dispatchFn;
        this.methodBinderSupplier = methodBinderSupplier;
        this.futureMap = futureMap;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return this.toString();
        }
        if (method.getName().equals("hashCode")) {
            return hashCode;
        }
        if (method.getName().equals("equals")) {
            return proxy == args[0];
        }
        if (method.isDefault()) {
            return MethodHandles.privateLookupIn(proxyInterface, MethodHandles.lookup())
                    .findSpecial(proxyInterface, method.getName(),
                            MethodType.methodType(method.getReturnType(), method.getParameterTypes()), proxyInterface)
                    .bindTo(proxy).invokeWithArguments(args);
        }

        final var methodBinder = methodBinderSupplier.apply(method);

        final var jmsDispatch = methodBinder.invocationBinder().apply(proxy, args);

        final var returnBinder = methodBinder.returnBinder();

        // Reply msg expected?
        final CompletableFuture<JmsMsg> futureMsg = (returnBinder instanceof RemoteReturnBinder)
                ? futureMap.add(jmsDispatch.correlationId())
                : null;

        dispatchFn.send(jmsDispatch);

        if (returnBinder instanceof LocalReturnBinder localBinder) {
            return localBinder.apply(jmsDispatch);
        }

        return ((RemoteReturnBinder) returnBinder).apply(jmsDispatch, futureMsg);
    }

}
