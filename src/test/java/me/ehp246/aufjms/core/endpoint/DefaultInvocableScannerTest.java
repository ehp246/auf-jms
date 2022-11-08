package me.ehp246.aufjms.core.endpoint;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case01.Case01;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case02.Case02;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case03.Case03;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case04.Case04;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case06.Case06;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case09.Cases09;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.case10.Cases10;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case01.ErrorCase01;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case02.ErrorCase02;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case03.ErrorCase03;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case04.ErrorCase04;
import me.ehp246.aufjms.core.endpoint.invokableresolvercase.error.case05.ErrorCase05;
import me.ehp246.aufjms.core.reflection.ReflectedType;
import me.ehp246.aufjms.util.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocableScannerTest {
    @Test
    void type_01() {
        Assertions.assertEquals(null, new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Case01.class.getPackageName())).resolve(new MockJmsMsg()));
    }

    @Test
    void type_02() {
        final var registery = new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Case01.class.getPackageName()));

        Assertions.assertEquals(Case01.class, registery.resolve(new MockJmsMsg("Case01")).instanceType());
    }

    @Test
    void type_03() {
        final var registery = new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Case02.class.getPackageName()));

        Assertions.assertEquals(Case02.class, registery.resolve(new MockJmsMsg("Case01")).instanceType());

        Assertions.assertEquals(Case02.class, registery.resolve(new MockJmsMsg("Case01-1")).instanceType());

        Assertions.assertEquals(Case02.class, registery.resolve(new MockJmsMsg("Case01-2")).instanceType());
    }

    @Test
    void type_04() {
        final var registery = new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Case03.class.getPackageName()));

        Assertions.assertEquals(Case03.class, registery.resolve(new MockJmsMsg("")).instanceType());

        Assertions.assertEquals(Case03.class, registery.resolve(new MockJmsMsg("Case01")).instanceType());

        Assertions.assertEquals(Case03.class, registery.resolve(new MockJmsMsg("Case01-1")).instanceType());

        Assertions.assertEquals(Case03.class, registery.resolve(new MockJmsMsg("Case01-2")).instanceType());
    }

    @Test
    void type_05() {
        final var registery = new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Case04.class.getPackageName()));

        Assertions.assertEquals(null, registery.resolve(new MockJmsMsg("")));

        Assertions.assertEquals(null, registery.resolve(new MockJmsMsg("Case01")));

        Assertions.assertEquals(Case04.Case01.class, registery.resolve(new MockJmsMsg("127.0.0.1")).instanceType());

        Assertions.assertEquals(Case04.Case01.class, registery.resolve(new MockJmsMsg("12:00")).instanceType());

        Assertions.assertEquals(Case04.Case02.class, registery.resolve(new MockJmsMsg("12")).instanceType());

        Assertions.assertEquals(null, registery.resolve(new MockJmsMsg("-12")));
    }

    @Test
    void type_06() {
        final var expected = UUID.randomUUID().toString();
        final var registery = new DefaultInvocableScanner(
                new MockEnvironment().withProperty("number", expected)::resolvePlaceholders)
                        .registeryFrom(Set.of(Cases10.class.getPackageName()));

        Assertions.assertEquals(Cases10.Case01.class,
                registery.resolve(new MockJmsMsg("Case" + expected)).instanceType());
    }

    @Test
    void type_07() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultInvocableScanner(new MockEnvironment()::resolveRequiredPlaceholders)
                        .registeryFrom(Set.of(Cases10.class.getPackageName())));
    }

    @Test
    void type_08() {
        final var registery = new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Case01.class.getPackageName()));

        Assertions.assertEquals(me.ehp246.aufjms.core.endpoint.invokableresolvercase.case01.Case02.class,
                registery.resolve(new MockJmsMsg("Case02")).instanceType());
    }

    @Test
    void error_01() {
        Assertions.assertThrows(RuntimeException.class, () -> new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(ErrorCase01.class.getPackageName())));

        Assertions.assertThrows(Exception.class, () -> new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(ErrorCase02.class.getPackageName())));

        Assertions.assertThrows(Exception.class, () -> new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(ErrorCase03.class.getPackageName())));

        Assertions.assertThrows(Exception.class, () -> new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(ErrorCase04.class.getPackageName())));

        Assertions.assertThrows(Exception.class, () -> new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(ErrorCase05.class.getPackageName())));
    }

    @Test
    void scope_01() {
        final var registery = new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Case06.class.getPackageName()));

        Assertions.assertEquals(InstanceScope.BEAN, registery.resolve(new MockJmsMsg("Case06")).scope());
    }

    @Test
    void invoking_01() {
        final var registery = new DefaultInvocableScanner(Object::toString)
                .registeryFrom(Set.of(Cases09.class.getPackageName()));

        Assertions.assertEquals(ReflectedType.reflect(Cases09.Case01.class).findMethod("invoke"),
                registery.resolve(new MockJmsMsg("Case01")).method());

        Assertions.assertEquals(ReflectedType.reflect(Cases09.Case02.class).findMethod("perform"),
                registery.resolve(new MockJmsMsg("Case02")).method());
    }

}
