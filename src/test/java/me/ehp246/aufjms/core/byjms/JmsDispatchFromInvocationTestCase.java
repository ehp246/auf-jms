package me.ehp246.aufjms.core.byjms;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import me.ehp246.aufjms.api.Invocation;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchFromInvocationTestCase {
    public void m01() {

    }

    public void m02(Map<String, String> map) {

    }

    Method getM01() {
        return OneUtil.orThrow(() -> this.getClass().getMethod("m01"));
    }

    Method getM02() {
        return OneUtil.orThrow(() -> this.getClass().getMethod("m02", Map.class));
    }

    Invocation getM01Invocation() {
        return new Invocation() {

            @Override
            public Object target() {
                return JmsDispatchFromInvocationTestCase.this;
            }

            @Override
            public Method method() {
                return getM01();
            }

            @Override
            public List<?> args() {
                return null;
            }
        };
    }

    Invocation getM02Invocation(final List<?> args) {
        return new Invocation() {

            @Override
            public Object target() {
                return JmsDispatchFromInvocationTestCase.this;
            }

            @Override
            public Method method() {
                return getM02();
            }

            @Override
            public List<?> args() {
                return args;
            }
        };
    }
}
