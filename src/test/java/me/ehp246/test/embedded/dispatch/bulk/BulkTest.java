package me.ehp246.test.embedded.dispatch.bulk;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.dispatch.DefaultDispatchFn;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.ToJson;
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
    private ToJson toJson;
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
     */
    @Test
    void send_02() throws Exception {
        final var jmseContext = provider.get(null).createContext();
        final var fn = new DefaultDispatchFn(jmseContext, toJson, null);
        final var dispatch = new BulkDispatch();

        IntStream.range(0, count).forEach(i -> {
            fn.send(dispatch.setBody(i + ""));
        });

        jmseContext.close();
    }

    /**
     * 15:06:19.844 15,574 ms.
     */
    @Test
    void send_03() {
        final var fn = new DefaultDispatchFn(this.provider.get(null), toJson, null);
        final AtQueue at = "inbox"::toString;

        IntStream.range(0, count).forEach(i -> {
            fn.send(JmsDispatch.toDispatch(at, "bulkMsg", i + ""));
        });
    }
}
