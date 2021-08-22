package me.ehp246.aufjms.core.endpoint.invokableresolvercase.case07;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.core.endpoint.DefaultInvokableResolver;
import me.ehp246.aufjms.util.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class TypeTest {
    @Test
    void type_03() {
        final var registery = DefaultInvokableResolver.registeryFrom(Set.of(Case07.class.getPackageName()));

        Assertions.assertEquals(Case07.class, registery.resolve(new MockJmsMsg("Case")).getInstanceType(),
                "should allow/merge duplicate types on the same class");
    }
}
