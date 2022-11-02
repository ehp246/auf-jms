package me.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class TypeTest {
    @Test
    void type_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultInvocableScanner(Object::toString)
                        .registeryFrom(Set.of("me.ehp246.aufjms.core.endpoint.invokableresolvercase.case07")),
                "should not allow duplicate types on the same class");
    }

    @Test
    void type_03() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultInvocableScanner(Object::toString)
                        .registeryFrom(Set.of("me.ehp246.aufjms.core.endpoint.invokableresolvercase.case08")),
                "should not allow duplicate types across classes");
    }
}
