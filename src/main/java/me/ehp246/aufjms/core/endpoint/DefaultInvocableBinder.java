package me.ehp246.aufjms.core.endpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.Session;
import javax.jms.TextMessage;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDeliveryCount;
import me.ehp246.aufjms.api.annotation.OfGroupId;
import me.ehp246.aufjms.api.annotation.OfGroupSeq;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfRedelivered;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.endpoint.BoundInvocable;
import me.ehp246.aufjms.api.endpoint.Invocable;
import me.ehp246.aufjms.api.endpoint.InvocableBinder;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.JMSSupplier;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.JmsNames;
import me.ehp246.aufjms.api.spi.FromJson;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocableBinder implements InvocableBinder {
    private static final Map<Class<? extends Annotation>, Function<JmsMsg, Object>> HEADER_VALUE_SUPPLIERS = Map.of(
            OfType.class, JmsMsg::type, OfCorrelationId.class, JmsMsg::correlationId, OfDeliveryCount.class,
            msg -> msg.property(JmsNames.DELIVERY_COUNT, Integer.class), OfGroupId.class, JmsMsg::groupId,
            OfGroupSeq.class, JmsMsg::groupSeq, OfRedelivered.class, JmsMsg::redelivered);

    private static final Set<Class<? extends Annotation>> HEADER_ANNOTATIONS = Set
            .copyOf(HEADER_VALUE_SUPPLIERS.keySet());

    private final Map<Method, Parsed> parsed = new ConcurrentHashMap<>();
    private final FromJson fromJson;

    public DefaultInvocableBinder(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @Override
    public BoundInvocable bind(final Invocable target, final MsgContext ctx) {
        final var method = target.method();

        final var parsed = this.parsed.computeIfAbsent(method, this::parse);

        final var payloadArgs = fromJson.apply(ctx.msg().text(), parsed.getPayloadReceivers()).iterator();

        final var parameterCount = method.getParameterCount();
        final var arguments = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            final var ctxArgFn = parsed.getCtxReceiver(i);
            arguments[i] = ctxArgFn != null ? ctxArgFn.apply(ctx) : payloadArgs.next();
        }

        return new BoundInvocableRecord(target, arguments, ctx.msg());
    }

    private Parsed parse(final Method method) {
        method.setAccessible(true);

        final var parameters = method.getParameters();
        final var parsed = new Parsed();

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final var type = parameter.getType();

            /*
             * Binding in priorities. Type first.
             */
            if (type.isAssignableFrom(JmsMsg.class)) {
                parsed.addCtxParameter(MsgContext::msg);
                continue;
            } else if (type.isAssignableFrom(TextMessage.class)) {
                parsed.addCtxParameter(ctx -> ctx.msg().message());
                continue;
            } else if (type.isAssignableFrom(MsgContext.class)) {
                parsed.addCtxParameter(ctx -> ctx);
                continue;
            } else if (type.isAssignableFrom(FromJson.class)) {
                parsed.addCtxParameter(ctx -> fromJson);
                continue;
            } else if (type.isAssignableFrom(Session.class)) {
                parsed.addCtxParameter(MsgContext::session);
                continue;
            }

            /*
             * Headers.
             */
            final var annotations = parameter.getAnnotations();
            final var header = Stream.of(annotations)
                    .filter(annotation -> HEADER_ANNOTATIONS.contains(annotation.annotationType())).findAny();
            if (header.isPresent()) {
                final var fn = HEADER_VALUE_SUPPLIERS.get(header.get().annotationType());
                parsed.addCtxParameter(ctx -> fn.apply(ctx.msg()));
                continue;
            }

            /*
             * Properties
             */
            final var prop = Stream.of(annotations).filter(ann -> ann.annotationType() == OfProperty.class).findAny()
                    .map(ann -> (OfProperty) ann);
            if (prop.isPresent()) {
                if (Map.class.isAssignableFrom(type)) {
                    parsed.addCtxParameter(ctx -> ctx.msg().propertyNames().stream().collect(Collectors.toMap(Function.identity(),
                            name -> JMSSupplier.invoke(() -> ctx.msg().message().getObjectProperty(name)))));
                } else {
                    final var name = OneUtil.getIfBlank(prop.get().value(), parameter::getName);
                    parsed.addCtxParameter(ctx -> ctx.msg().property(name, type));
                }
                continue;
            }

            /*
             * Payload
             */
            parsed.addPayloadParameter(new FromJson.To(parameter.getType(), List.of(parameter.getAnnotations())));
        }

        return parsed;
    }

    static class Parsed {
        /**
         * This is a simple list.
         */
        private final List<FromJson.To> payloadReceivers = new ArrayList<>();
        /**
         * <code>null</code> indicates a payload argument.
         */
        private final List<Function<MsgContext, Object>> ctxReceivers = new ArrayList<>();

        void addPayloadParameter(final FromJson.To to) {
            payloadReceivers.add(to);
            ctxReceivers.add(null);
        }

        void addCtxParameter(final Function<MsgContext, Object> fn) {
            ctxReceivers.add(fn);
        }

        List<FromJson.To> getPayloadReceivers() {
            return this.payloadReceivers;
        }

        Function<MsgContext, Object> getCtxReceiver(final int i) {
            return this.ctxReceivers.get(i);
        }
    }
}
