package me.ehp246.aufjms.core.inbound;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDeliveryCount;
import me.ehp246.aufjms.api.annotation.OfGroupId;
import me.ehp246.aufjms.api.annotation.OfGroupSeq;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfRedelivered;
import me.ehp246.aufjms.api.annotation.OfThreadContext;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.inbound.BoundInvocable;
import me.ehp246.aufjms.api.inbound.Invocable;
import me.ehp246.aufjms.api.inbound.InvocableBinder;
import me.ehp246.aufjms.api.jms.FromJson;
import me.ehp246.aufjms.api.jms.JMSSupplier;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.JmsNames;
import me.ehp246.aufjms.api.spi.BodyOfBuilder;
import me.ehp246.aufjms.core.reflection.ReflectedMethod;
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

    private final FromJson fromJson;
    private final Map<Method, ArgBinders> parsed = new ConcurrentHashMap<>();

    public DefaultInvocableBinder(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @Override
    public BoundInvocable bind(final Invocable target, final JmsMsg msg) {
        final var method = target.method();

        final var argBinders = this.parsed.computeIfAbsent(method, this::parse);

        final var paramBinders = argBinders.paramBinders();
        final var parameterCount = method.getParameterCount();

        /*
         * Bind the arguments.
         */
        final var arguments = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            arguments[i] = paramBinders.get(i).apply(msg);
        }

        /*
         * Bind the Thread Context
         */
        final var threadContextBinders = argBinders.threadContextBinders();
        final Map<String, String> threadContext = threadContextBinders == null ? Map.of()
                : threadContextBinders.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
                        entry -> threadContextBinders.get(entry.getKey()).apply(arguments)));

        return new BoundInvocable() {

            @Override
            public Invocable invocable() {
                return target;
            }

            @Override
            public JmsMsg msg() {
                return msg;
            }

            @Override
            public Object[] arguments() {
                return arguments;
            }

            @Override
            public Map<String, String> threadContext() {
                return threadContext;
            }

        };
    }

    private ArgBinders parse(final Method method) {
        method.setAccessible(true);

        final var parameters = method.getParameters();
        final Map<Integer, Function<JmsMsg, Object>> paramBinders = new HashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final var type = parameter.getType();

            /*
             * Binding in priorities. Type first.
             */
            if (type.isAssignableFrom(JmsMsg.class)) {
                paramBinders.put(i, msg -> msg);
                continue;
            } else if (type.isAssignableFrom(TextMessage.class)) {
                paramBinders.put(i, JmsMsg::message);
                continue;
            } else if (type.isAssignableFrom(FromJson.class)) {
                paramBinders.put(i, msg -> fromJson);
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
                paramBinders.put(i, msg -> fn.apply(msg));
                continue;
            }

            /*
             * Properties
             */
            final var prop = Stream.of(annotations).filter(ann -> ann.annotationType() == OfProperty.class).findAny()
                    .map(ann -> (OfProperty) ann);
            if (prop.isPresent()) {
                if (Map.class.isAssignableFrom(type)) {
                    paramBinders.put(i,
                            msg -> msg.propertyNames().stream().collect(Collectors.toMap(Function.identity(),
                                    name -> JMSSupplier.invoke(() -> msg.message().getObjectProperty(name)))));
                } else {
                    final var name = OneUtil.getIfBlank(prop.get().value(),
                            () -> OneUtil.firstUpper(parameter.getName()));
                    paramBinders.put(i, msg -> msg.property(name, type));
                }
                continue;
            }

            /*
             * Body
             */
            final var bodyOf = BodyOfBuilder.ofView(Optional.ofNullable(parameter.getAnnotation(JsonView.class))
                    .map(JsonView::value).map(OneUtil::firstOrNull).orElse(null), parameter.getType());

            paramBinders.put(i, msg -> msg.text() == null ? null : fromJson.apply(msg.text(), bodyOf));
        }

        final var threadCOntextBinders = new ReflectedMethod(method).allParametersWith(OfThreadContext.class)
                .stream().collect(Collectors.toMap(p -> {
                    final var name = p.parameter().getAnnotation(OfThreadContext.class).value();
                    return OneUtil.hasValue(name) ? name : p.parameter().getName();
                }, p -> {
                    final var index = p.index();
                    return (Function<Object[], String>) (args -> args[index] == null ? "null" : args[index].toString());
                }, (l, r) -> l));

        return new ArgBinders(paramBinders, threadCOntextBinders);
    }

    record ArgBinders(Map<Integer, Function<JmsMsg, Object>> paramBinders,
            Map<String, Function<Object[], String>> threadContextBinders) {
    };
}
