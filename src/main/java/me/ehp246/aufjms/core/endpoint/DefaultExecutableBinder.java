package me.ehp246.aufjms.core.endpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.jms.JMSContext;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.spi.FromJson;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultExecutableBinder implements ExecutableBinder {
    private static final Map<Class<? extends Annotation>, Function<JmsMsg, String>> PROPERTY_VALUE_SUPPLIERS = Map
            .of(OfType.class, JmsMsg::type, OfCorrelationId.class, JmsMsg::correlationId);

    private static final Set<Class<? extends Annotation>> PROPERTY_ANNOTATIONS = Set
            .copyOf(PROPERTY_VALUE_SUPPLIERS.keySet());

    private final FromJson fromJson;

    public DefaultExecutableBinder(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @Override
    public Supplier<InvocationOutcome<?>> bind(final Executable target, final MsgContext ctx) {
        final var method = target.getMethod();
        if (method.getParameterCount() == 0) {
            return () -> {
                try {
                    method.setAccessible(true);
                    return InvocationOutcome.returned(method.invoke(target.getInstance(), (Object[]) null));
                } catch (InvocationTargetException e1) {
                    return InvocationOutcome.thrown(e1.getCause());
                } catch (Exception e2) {
                    return InvocationOutcome.thrown(e2);
                }
            };
        }

        final var parameters = method.getParameters();
        final var arguments = new Object[parameters.length];

        final var boundMarkers = bindContextArgs(parameters, ctx, arguments);

        final var receivers = new ArrayList<FromJson.Receiver<?>>();
        for (int i = 0; i < boundMarkers.length; i++) {
            if (boundMarkers[i]) {
                continue;
            }

            final var ref = Integer.valueOf(i);
            receivers.add(new FromJson.Receiver<>() {

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
            fromJson.from(ctx.msg().text(), receivers);
        }

        return () -> {
            try {
                method.setAccessible(true);
                return InvocationOutcome.returned(method.invoke(target.getInstance(), arguments));
            } catch (InvocationTargetException e1) {
                return InvocationOutcome.thrown(e1.getCause());
            } catch (Exception e2) {
                return InvocationOutcome.thrown(e2);
            }
        };
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
    private boolean[] bindContextArgs(final Parameter[] parameters, final MsgContext ctx,
            final Object[] arguments) {
        final boolean[] markers = new boolean[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final var msg = ctx.msg();

            // Bind by type
            final var type = parameter.getType();
            if (type.isAssignableFrom(JmsMsg.class)) {
                arguments[i] = msg;
                markers[i] = true;
            } else if (type.isAssignableFrom(TextMessage.class)) {
                arguments[i] = msg.message();
                markers[i] = true;
            } else if (type.isAssignableFrom(MsgContext.class)) {
                arguments[i] = ctx;
                markers[i] = true;
            } else if (type.isAssignableFrom(FromJson.class)) {
                arguments[i] = fromJson;
                markers[i] = true;
            } else if (type.isAssignableFrom(JMSContext.class)) {
                arguments[i] = ctx.jmsContext();
                markers[i] = true;
            }

            // Bind Headers
            final var annotations = parameter.getAnnotations();
            var found = Stream.of(annotations)
                    .filter(annotation -> PROPERTY_ANNOTATIONS.contains(annotation.annotationType())).findAny();
            if (found.isPresent()) {
                arguments[i] = PROPERTY_VALUE_SUPPLIERS.get(found.get().annotationType()).apply(msg);
                markers[i] = true;
            }

            // Bind Properties
            found = Stream.of(annotations).filter(annotation -> annotation.annotationType() == OfProperty.class)
                    .findAny();
            if (found.isPresent()) {
                if (Map.class.isAssignableFrom(type)) {
                    arguments[i] = msg.propertyMap();
                } else {
                    arguments[i] = msg.property(((OfProperty) found.get()).value(), type);
                }
                markers[i] = true;
            }
        }

        return markers;
    }
}
