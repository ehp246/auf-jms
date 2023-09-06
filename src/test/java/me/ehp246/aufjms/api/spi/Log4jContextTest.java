package me.ehp246.aufjms.api.spi;

import org.apache.logging.log4j.ThreadContext;
import org.jgroups.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.test.mock.MockJmsMsg;

/**
 * @author Lei Yang
 *
 */
class Log4jContextTest {
    private MockedStatic<ThreadContext> mockContext;

    @BeforeEach
    public void beforeEach() {
        mockContext = Mockito.mockStatic(ThreadContext.class);
    }

    @AfterEach
    public void afterEach() {
        mockContext.close();
    }

    @Test
    void log4jHeader_01() {
        final var value = UUID.randomUUID().toString();
        final var name = UUID.randomUUID().toString();
        final var mockJmsMsg = new MockJmsMsg().withProperty(Log4jContext.LOG4J_THREAD_CONTEXT_HEADER_PREFIX + name,
                value);

        Log4jContext.set(mockJmsMsg);

        mockContext.verify(() -> ThreadContext.put(name, value), Mockito.times(1));

        Log4jContext.clear(mockJmsMsg);

        mockContext.verify(() -> ThreadContext.remove(name), Mockito.times(1));
    }

    @Test
    void test() {
        Log4jContext.set((JmsDispatch) null);
        Log4jContext.clear((JmsDispatch) null);

        Log4jContext.set((JmsMsg) null);
        Log4jContext.clearMsg();
    }

}
