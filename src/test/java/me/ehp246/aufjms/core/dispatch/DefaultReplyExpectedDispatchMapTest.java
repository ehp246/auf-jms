package me.ehp246.aufjms.core.dispatch;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class DefaultReplyExpectedDispatchMapTest {

    @Test
    void test_01() {
        final var map = new DefaultReplyExpectedDispatchMap();
        final var id = UUID.randomUUID().toString();

        final var future = map.add(id);

        Assertions.assertEquals(future, map.get(id));

        map.remove(id);

        Assertions.assertEquals(null, map.get(id));
    }

    @Test
    void test_02() {
        final var map = new DefaultReplyExpectedDispatchMap();
        final var id = UUID.randomUUID().toString();

        map.add(id);

        Assertions.assertThrows(IllegalArgumentException.class, () -> map.add(id));
    }

    @Test
    void test_03() {
        final var map = new DefaultReplyExpectedDispatchMap();
        final var id = UUID.randomUUID().toString();

        Assertions.assertEquals(null, map.get(id));
        Assertions.assertEquals(null, map.remove(id));
    }
}
