package me.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case01.Case01;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case02.Case02;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case03.Case03;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case04.Case04;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case05.Case05;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case06.Case06;
import me.ehp246.aufjms.util.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class DefaultInvokableResolverTest {
    private final static String PATH = "me.ehp246.aufjms.core.endpoint.invokableresolvercase.";

    @Test
    void type_01() {
        Assertions.assertEquals(null, DefaultInvokableResolver
                .registeryFrom(Set.of(Case01.class.getPackageName())).resolve(new MockJmsMsg()));
    }

    @Test
    void type_02() {
        final var registery = DefaultInvokableResolver
                .registeryFrom(Set.of(Case01.class.getPackageName()));

        Assertions.assertEquals(Case01.class, registery.resolve(new MockJmsMsg("Case01")).getInstanceType());
    }

    @Test
    void error_03() {
        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(Case02.class.getPackageName())));

        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(Case03.class.getPackageName())));

        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(Case04.class.getPackageName())));

        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(Case05.class.getPackageName())));
    }

    @Test
    void scope_01() {
        final var registery = DefaultInvokableResolver.registeryFrom(Set.of(Case06.class.getPackageName()));

        Assertions.assertEquals(InstanceScope.BEAN, registery.resolve(new MockJmsMsg("Case06")).getScope());
    }
}
