package me.ehp246.aufjms.global.case002;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufjms.api.exception.ForMsgExecutionException;
import me.ehp246.aufjms.core.byjms.ReplyEndpointConfiguration;
import me.ehp246.aufjms.global.case002.bymsg.Add;
import me.ehp246.aufjms.global.case002.bymsg.Counter;
import me.ehp246.aufjms.global.case002.bymsg.ExceptionThrower;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfiguration.class, properties = {
        "spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false" })
public class AppConfigurationTest {
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Test
    public void timeout001() {
        Assertions.assertEquals(30000, beanFactory.getBean(ReplyEndpointConfiguration.class).getTimeout(),
                "Should be the default timeout");
    }

    @Test
    public void ttl001() {
        Assertions.assertEquals(0, beanFactory.getBean(ReplyEndpointConfiguration.class).getTtl(),
                "Should be the default TTL");
    }

    @Test
    public void add001() {
        Assertions.assertEquals(11, beanFactory.getBean(Add.class).add(10, 1));
    }

    @Test
    public void mem001() {
        final var counter = beanFactory.getBean(Counter.class);

        counter.set(-1);

        Assertions.assertEquals(-1, counter.get());

        Assertions.assertEquals(0, counter.add(1));

        Assertions.assertEquals(0, counter.get());
    }

    @Test
    public void exception001() {
        final var thrower = beanFactory.getBean(ExceptionThrower.class);

        final var thrown = Assertions.assertThrows(ForMsgExecutionException.class, thrower::throw001);
        Assertions.assertEquals("Throw 001", thrown.getMessage());
    }

    @Test
    public void exception002() {
        final var thrower = beanFactory.getBean(ExceptionThrower.class);

        final var thrown = Assertions.assertThrows(Exception.class, thrower::throw002);
        Assertions.assertEquals("Throw 002", thrown.getMessage());
    }

    @Test
    public void exception003() {
        final var thrower = beanFactory.getBean(ExceptionThrower.class);

        final var thrown = (ForMsgExecutionException) Assertions.assertThrows(Exception.class, thrower::throw003);
        Assertions.assertEquals(3, thrown.getCode());
        Assertions.assertEquals("Throw 003", thrown.getMessage());
    }
}
