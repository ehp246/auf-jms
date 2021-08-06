package me.ehp246.aufjms.core.endpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.jms.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.InvocationContext;
import me.ehp246.aufjms.api.endpoint.ResolvedExecutable;
import me.ehp246.aufjms.api.jms.FromMsgBody;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.reflection.ReflectingInvocation;

/**
 *
 * @author Lei Yang
 *
 */
public class DefaultExecutableBinder implements ExecutableBinder {
    private final static Logger LOGGER = LogManager.getLogger(DefaultExecutableBinder.class);

    protected static final Map<Class<? extends Annotation>, Function<JmsMsg, String>> HEADER_VALUE_SUPPLIERS = Map
            .of(OfType.class, JmsMsg::type);

    protected static final Set<Class<? extends Annotation>> HEADER_ANNOTATIONS = Set
            .copyOf(HEADER_VALUE_SUPPLIERS.keySet());

    private final FromMsgBody<String> fromBody;

    public DefaultExecutableBinder(final FromMsgBody<String> fromBody) {
        super();
        this.fromBody = fromBody;
    }

    @Override
    public ReflectingInvocation bind(final ResolvedExecutable resolved, final InvocationContext ctx) {
        final var method = resolved.getMethod();
        if (method.getParameterCount() == 0) {
            return new ReflectingInvocation(resolved.getInstance(), method, null);
        }

        final var parameters = method.getParameters();
        final var arguments = new Object[parameters.length];

        final var boundMarkers = bindContextArgs(parameters, ctx, arguments);

        final var receivers = new ArrayList<FromMsgBody.Receiver<?>>();
        for (int i = 0; i < boundMarkers.length; i++) {
            if (boundMarkers[i]) {
                continue;
            }

            final var ref = Integer.valueOf(i);
            receivers.add(new FromMsgBody.Receiver<>() {

                @Override
                public void receive(final Object value) {
                    arguments[ref] = value;
                }

                @Override
                public Class<?> getType() {
                    return parameters[ref].getType();
                }

                @Override
                public List<? extends Annotation> getAnnotations() {
                    return List.of(parameters[ref].getAnnotations());
                }
            });
        }

        if (receivers.size() > 0) {
            fromBody.from(ctx.getMsg().text(), receivers);
        }

        return new ReflectingInvocation(resolved.getInstance(), method, arguments);
    }

    /**
     * Fills in the context arguments at the index position. Returns indices of
     * positions that have been filled.
     *
     * @param parameters
     * @param mq
     * @param arguments
     * @return
     */
    private boolean[] bindContextArgs(final Parameter[] parameters, final InvocationContext ctx,
            final Object[] arguments) {
        final boolean[] markers = new boolean[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final var msg = ctx.getMsg();

            // Bind by type
            final var type = parameter.getType();
            if (type.isAssignableFrom(JmsMsg.class)) {
                arguments[i] = msg;
                markers[i] = true;
            } else if (type.isAssignableFrom(Message.class)) {
                arguments[i] = msg.message();
                markers[i] = true;
            }

            // Bind Headers
            final var annotations = parameter.getAnnotations();
            var found = Stream.of(annotations)
                    .filter(annotation -> HEADER_ANNOTATIONS.contains(annotation.annotationType())).findAny();
            if (found.isPresent()) {
                arguments[i] = HEADER_VALUE_SUPPLIERS.get(found.get().annotationType()).apply(msg);
                markers[i] = true;
            }

            // Bind Properties
            found = Stream.of(annotations).filter(annotation -> annotation.annotationType() == OfProperty.class)
                    .findAny();
            if (found.isPresent()) {
                arguments[i] = msg.property(((OfProperty) found.get()).value(), parameter.getType());
                markers[i] = true;
            }
        }

        return markers;
    }
}
