package me.ehp246.test.embedded.reqres;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class })
class ReqResTest {
    @Autowired
    private MathProxy matchProxy;

    @Test
    void test_01() {
        Assertions.assertEquals(-11, matchProxy.inc(-10));
    }
}
