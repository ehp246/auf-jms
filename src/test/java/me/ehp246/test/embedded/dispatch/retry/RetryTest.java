package me.ehp246.test.embedded.dispatch.retry;

import java.util.concurrent.ExecutionException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufjms.api.exception.JmsDispatchFailedException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
class RetryTest {
    @Autowired
    private int[] countRef;

    @Autowired
    private AppConfig.Case01 case01;

    @Autowired
    private OnMsg onMsg;

    @BeforeEach
    void cler() {
        countRef[0] = 0;
    }

    @Test
    @Timeout(3)
    void test_02() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();

        case01.fail(expected);

        Assertions.assertEquals("\"" + expected + "\"", onMsg.take().text());

        Assertions.assertEquals(3, countRef[0]);
    }

    @Test
    void test_03() throws InterruptedException, ExecutionException {
        countRef[0] = 4;
        Assertions.assertThrows(JmsDispatchFailedException.class, () -> case01.fail(UUID.randomUUID().toString()));
        Assertions.assertEquals(7, countRef[0]);
    }
}
