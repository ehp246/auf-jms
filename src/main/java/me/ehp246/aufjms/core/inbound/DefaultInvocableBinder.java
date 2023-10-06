package me.ehp246.aufjms.core.inbound;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
import me.ehp246.aufjms.api.annotation.OfLog4jContext;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfRedelivered;
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
import me.ehp246.aufjms.core.reflection.ReflectedType;
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
        final var log4jContextBinders = argBinders.log4jContextBinders();
        final Map<String, String> log4jContext = new HashMap<>();
        if (log4jContextBinders != null && log4jContextBinders.size() > 0) {
            log4jContextBinders.entrySet().stream().forEach(entry -> {
                log4jContext.put(entry.getKey(), log4jContextBinders.get(entry.getKey()).apply(arguments));
            });
        }

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
            public Map<String, String> log4jContext() {
                return log4jContext;
            }

        };
    }

    private ArgBinders parse(final Method method) {
        method.setAccessible(true);

        final var parameters = method.getParameters();
        final Map<Integer, Function<JmsMsg, Object>> paramBinders = new HashMap<>();
        final var bodyArgIndexRef = new int[] { -1 };
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
            bodyArgIndexRef[0] = i;
        }

        final var log4jContextBinders = new HashMap<String, Function<Object[], String>>();

        /*
         * Assume only one body parameter on the parameter list
         */
        final int bodyParamIndex = bodyArgIndexRef[0];
        if (bodyParamIndex >= 0) {
            final var bodyParam = parameters[bodyParamIndex];
            final var ofLog4jContext = bodyParam.getAnnotation(OfLog4jContext.class);
            if (ofLog4jContext != null) {
                if (!ofLog4jContext.introspect()) {
                    log4jContextBinders.put(
                            Optional.ofNullable(bodyParam.getAnnotation(OfLog4jContext.class)).map(OfLog4jContext::value)
                                    .filter(OneUtil::hasValue).orElseGet(bodyParam::getName),
                            args -> args[bodyParamIndex] == null ? null : args[bodyParamIndex] + "");
                } else {

                    /*
                     * Duplicated names will overwrite each other un-deterministically.
                     */
                    final var bodyParamContextName = Optional.ofNullable(bodyParam.getAnnotation(OfLog4jContext.class))
                            .map(OfLog4jContext::value).filter(OneUtil::hasValue).orElseGet(() -> bodyParam.getName());
                    final var bodyFieldBinders = new ReflectedType<>(bodyParam.getType())
                            .streamSuppliersWith(OfLog4jContext.class)
                            .collect(Collectors.toMap(
                                    m -> bodyParamContextName + "."
                                            + Optional.of(m.getAnnotation(OfLog4jContext.class).value())
                                                    .filter(OneUtil::hasValue).orElseGet(() -> m.getName()),
                                    Function.identity(), (l, r) -> r))
                            .entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
                                final var m = entry.getValue();
                                return (Function<Object[], String>) args -> {
                                    final var body = args[bodyParamIndex];
                                    if (body == null) {
                                        return "null";
                                    }
                                    try {
                                        return m.invoke(body) + "";
                                    } catch (IllegalAccessException | IllegalArgumentException
                                            | InvocationTargetException e) {
                                        throw new RuntimeException(e);
                                    }
                                };
                            }));
                    log4jContextBinders.putAll(bodyFieldBinders);
                }
            }
        }

        /*
         * Parameters overwrite the body.
         */
        log4jContextBinders.putAll(new ReflectedMethod(method).allParametersWith(OfLog4jContext.class).stream()
                .collect(Collectors.toMap(p -> {
                    final var name = p.parameter().getAnnotation(OfLog4jContext.class).value();
                    return OneUtil.hasValue(name) ? name : p.parameter().getName();
                }, p -> {
                    final var index = p.index();
                    return (Function<Object[], String>) (args -> args[index] == null ? null : args[index] + "");
                }, (l, r) -> r)));

        return new ArgBinders(paramBinders, log4jContextBinders);
    }

    record ArgBinders(Map<Integer, Function<JmsMsg, Object>> paramBinders,
            Map<String, Function<Object[], String>> log4jContextBinders) {
    };
}
