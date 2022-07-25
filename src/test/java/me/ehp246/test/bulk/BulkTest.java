package me.ehp246.test.bulk;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.test.TimingExtension;

/**
 * @author Lei Yang
 *
 */
@Disabled
@ExtendWith(TimingExtension.class)
@SpringBootTest(classes = { AppConfig.class })
class BulkTest {
    private final static int count = 10_000;

    @Autowired
    private Proxy proxy;

    @Test
    void send_01() {
        IntStream.range(0, count).forEach(proxy::bulkMsg);
    }

    @Test
    void send_02() {
        proxy.bulkMsg(IntStream.range(0, count));
    }
}
