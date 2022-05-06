package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class InvocableTest {

    @Test
    void test() {
        final var actual = new Invocable() {

            @Override
            public Method method() {
                return null;
            }

            @Override
            public Object instance() {
                return null;
            }
        };

        Assertions.assertEquals(InvocationModel.DEFAULT, actual.invocationModel());
    }

}
