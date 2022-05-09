package me.ehp246.aufjms.core.endpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.Session;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDeliveryCount;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.JMSSupplier;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.JmsNames;
import me.ehp246.aufjms.api.spi.FromJson;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocableBinder implements InvocableBinder {
    private static final Map<Class<? extends Annotation>, Function<JmsMsg, Object>> PROPERTY_VALUE_SUPPLIERS = Map.of(
            OfType.class, JmsMsg::type, OfCorrelationId.class, JmsMsg::correlationId, OfDeliveryCount.class,
            msg -> msg.property(JmsNames.DELIVERY_COUNT, Integer.class));

    private static final Set<Class<? extends Annotation>> PROPERTY_ANNOTATIONS = Set
            .copyOf(PROPERTY_VALUE_SUPPLIERS.keySet());

    private final FromJson fromJson;

    public DefaultInvocableBinder(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @Override
    public BoundInvocable bind(final Invocable target, final MsgContext ctx) {
        final var method = target.method();

        method.setAccessible(true);

        if (method.getParameterCount() == 0) {
            return new BoundInvocableRecord(target, ctx.msg());
        }

        final var parameters = method.getParameters();
        final var arguments = new Object[parameters.length];

        final var boundMarkers = bindContextArgs(parameters, ctx, arguments);

        final var receivers = new ArrayList<FromJson.To>();
        for (int i = 0; i < boundMarkers.length; i++) {
            if (boundMarkers[i]) {
                continue;
            }

            final var ref = Integer.valueOf(i);
            receivers.add(new FromJson.To() {

                @Override
                public void receive(final Object value) {
                    arguments[ref] = value;
                }

                @Override
                public Class<?> type() {
                    return parameters[ref].getType();
                }

                @Override
                public List<? extends Annotation> annotations() {
                    return List.of(parameters[ref].getAnnotations());
                }
            });
        }

        if (receivers.size() > 0) {
            fromJson.apply(ctx.msg().text(), receivers);
        }

        return new BoundInvocableRecord(target, Arrays.asList(arguments), ctx.msg());
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
    private boolean[] bindContextArgs(final Parameter[] parameters, final MsgContext ctx, final Object[] arguments) {
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
            } else if (type.isAssignableFrom(Session.class)) {
                arguments[i] = ctx.session();
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
                    arguments[i] = propertyMap(msg);
                } else {
                    arguments[i] = msg.property(((OfProperty) found.get()).value(), type);
                }
                markers[i] = true;
            }
        }

        return markers;
    }

    private static Map<String, Object> propertyMap(final JmsMsg msg) {
        final var message = msg.message();
        return msg.propertyNames().stream().collect(Collectors.toMap(Function.identity(),
                name -> JMSSupplier.invoke(() -> message.getObjectProperty(name))));
    }
}
