package me.ehp246.aufjms.core.byjms;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Lei Yang
 *
 */
class JmsDispatchFromInvocationTestCase {
    public void m01() {

    }

    public void m02(Map<String, String> map) {

    }

    Method getM01() throws NoSuchMethodException, SecurityException {
        return this.getClass().getMethod("m01");
    }

    Method getM02() throws NoSuchMethodException, SecurityException {
        return this.getClass().getMethod("m02", Map.class);
    }
}
