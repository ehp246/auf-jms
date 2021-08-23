package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case07;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.core.endpoint.DefaultInvokableResolver;

/**
 * @author Lei Yang
 *
 */
class TypeTest {
    @Test
    void type_01() {
        Assertions.assertThrows(Exception.class, () -> DefaultInvokableResolver.registeryFrom(Set.of(Case07.class.getPackageName())),
                "should not allow duplicate types on the same class");
    }
}
