package me.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case01.Case01;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case06.Case06;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case09.Cases09;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case01.ErrorCase01;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case02.ErrorCase02;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case03.ErrorCase03;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case04.ErrorCase04;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case05.ErrorCase05;
import me.ehp246.aufjms.core.reflection.ReflectingType;
import me.ehp246.aufjms.util.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class DefaultInvokableResolverTest {
    @Test
    void type_01() {
        Assertions.assertEquals(null, DefaultInvokableResolver.registeryFrom(Set.of(Case01.class.getPackageName()))
                .resolve(new MockJmsMsg()));
    }

    @Test
    void type_02() {
        final var registery = DefaultInvokableResolver.registeryFrom(Set.of(Case01.class.getPackageName()));

        Assertions.assertEquals(Case01.class, registery.resolve(new MockJmsMsg("Case01")).getInstanceType());
    }

    @Test
    void error_01() {
        Assertions.assertThrows(RuntimeException.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(ErrorCase01.class.getPackageName())));

        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(ErrorCase02.class.getPackageName())));

        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(ErrorCase03.class.getPackageName())));

        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(ErrorCase04.class.getPackageName())));

        Assertions.assertThrows(Exception.class,
                () -> DefaultInvokableResolver.registeryFrom(Set.of(ErrorCase05.class.getPackageName())));
    }

    @Test
    void scope_01() {
        final var registery = DefaultInvokableResolver.registeryFrom(Set.of(Case06.class.getPackageName()));

        Assertions.assertEquals(InstanceScope.BEAN, registery.resolve(new MockJmsMsg("Case06")).getScope());
    }

    @Test
    void invoking_01() {
        final var registery = DefaultInvokableResolver.registeryFrom(Set.of(Cases09.class.getPackageName()));

        Assertions.assertEquals(ReflectingType.reflect(Cases09.Case01.class).findMethod("invoke"),
                registery.resolve(new MockJmsMsg("Case01")).getMethod());

        Assertions.assertEquals(ReflectingType.reflect(Cases09.Case02.class).findMethod("perform"),
                registery.resolve(new MockJmsMsg("Case02")).getMethod());
    }
}
