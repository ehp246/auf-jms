package me.ehp246.test.bulk;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.jms.JmsDispatchContext;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.test.TimingExtension;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
@SpringBootTest(classes = { AppConfig.class })
@EnabledIfSystemProperty(named = "me.ehp246.aufjms", matches = "true")
class BulkTest {
    private final static int count = 3_000;

    @Autowired
    private ConnectionFactoryProvider provider;
    @Autowired
    private Proxy proxy;

    /**
     * 14:44:39.168 15,177 ms.
     */
    @Test
    void send_01() {
        IntStream.range(0, count).forEach(proxy::bulkMsg);
    }

    /**
     * 15:32:17.380 2,204 ms
     *
     * @throws Exception
     */
    @Test
    void send_02() throws Exception {
        try (final var closeable = JmsDispatchContext.set(provider.get(null).createContext())) {
            IntStream.range(0, count).forEach(proxy::bulkMsg);
        }
    }

    @Test
    void send_03() {
        proxy.bulkMsg(IntStream.range(0, count));
    }
}
