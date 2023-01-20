package me.ehp246.aufjms.core.inbound;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.inbound.InvocableTypeDefinition;
import me.ehp246.aufjms.api.inbound.InvocationModel;
import me.ehp246.aufjms.core.inbound.DefaultInvocableRegistry;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.test.TimingExtension;
import me.ehp246.test.mock.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
@EnabledIfSystemProperty(named = "me.ehp246.perf", matches = "true")
class DefaultInvocableRegistryPerfTest {
    private final int count = 2_000_000;
    private final List<String> types = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
    private final DefaultInvocableRegistry registry = new DefaultInvocableRegistry()
            .register(Stream.of(new InvocableTypeDefinition(Set.copyOf(types), Object.class,
                    Map.of("", new ReflectedType<>(Object.class).findMethod("toString")), InstanceScope.MESSAGE,
                    InvocationModel.DEFAULT)));

    @Test
    void test_01() {
        final var msg = new MockJmsMsg(UUID.randomUUID().toString());

        IntStream.range(0, count).forEach(i -> registry.resolve(msg));
    }

    @Test
    void test_02() {
        final var msgs = types.stream().map(type -> new MockJmsMsg(type)).collect(Collectors.toList());

        final var mode = types.size();
        IntStream.range(0, count).forEach(i -> registry.resolve(msgs.get(i % mode)));
    }
}
