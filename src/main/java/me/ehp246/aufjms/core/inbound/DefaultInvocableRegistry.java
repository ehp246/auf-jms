package me.ehp246.aufjms.core.inbound;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.ehp246.aufjms.api.inbound.InvocableType;
import me.ehp246.aufjms.api.inbound.InvocableTypeDefinition;
import me.ehp246.aufjms.api.inbound.InvocableTypeRegistry;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 *
 * Invokable by Type Registry.
 *
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultInvocableRegistry implements InvocableTypeRegistry {
    private final Map<String, InvocableTypeDefinition> cached = new ConcurrentHashMap<>();
    private final Map<String, InvocableTypeDefinition> registeredInvokables = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String, Method>> registeredMethods = new ConcurrentHashMap<>();

    public DefaultInvocableRegistry register(final Stream<InvocableTypeDefinition> invokingDefinitions) {
        invokingDefinitions.forEach(this::register);
        return this;
    }

    @Override
    public void register(final InvocableTypeDefinition invokingDefinition) {
        invokingDefinition.msgTypes().forEach(type -> {
            final var registered = registeredInvokables.putIfAbsent(type, invokingDefinition);
            if (registered != null) {
                throw new IllegalArgumentException("Duplicate type " + type + " from " + registered.type());
            }

            registeredMethods.put(invokingDefinition.type(), invokingDefinition.methods());
        });
    }

    @Override
    public Map<String, InvocableTypeDefinition> registered() {
        return Collections.unmodifiableMap(this.registeredInvokables);
    }

    @Override
    public InvocableType resolve(final JmsMsg msg) {
        final var msgType = OneUtil.toString(Objects.requireNonNull(msg).type(), "");

        final var definition = this.cached.computeIfAbsent(msgType, key -> registeredInvokables.entrySet().stream()
                .filter(e -> msgType.matches(e.getKey())).findAny().map(Map.Entry::getValue).orElse(null));


        if (definition == null) {
            return null;
        }

        var invoking = msg.invoking();
        invoking = invoking != null ? invoking.strip() : "";

        final var method = registeredMethods.get(definition.type()).get(invoking);

        if (method == null) {
            return null;
        }

        return new InvocableType(definition.type(), method, definition.scope(), definition.model());
    }
}
