package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ExecutableTest {

    @Test
    void test() {
        final var actual = new Executable() {

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
