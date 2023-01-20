package me.ehp246.test.mock;

import java.lang.reflect.Method;

import me.ehp246.aufjms.api.inbound.Invocable;

/**
 * @author Lei Yang
 *
 */
public class MockExecutable implements Invocable {
    private final String method;
    private Exception ex;

    public MockExecutable(String method) {
        super();
        this.method = method;
    }
    
    public void throwIt() throws Exception {
        this.ex = new RuntimeException();
        
        throw this.ex;
    }

    @Override
    public Object instance() {
        return this;
    }

    @Override
    public Method method() {
        try {
            return MockExecutable.class.getDeclaredMethod(method, new Class[] { null });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
