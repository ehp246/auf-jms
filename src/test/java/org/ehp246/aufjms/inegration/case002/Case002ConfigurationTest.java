package org.ehp246.aufjms.inegration.case002;

import org.ehp246.aufjms.core.bymsg.ReplyEndpointConfiguration;
import org.ehp246.aufjms.inegration.case002.bymsg.Add;
import org.ehp246.aufjms.inegration.case002.bymsg.Counter;
import org.ehp246.aufjms.inegration.case002.bymsg.ExceptionThrower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = Case002Configuration.class, properties = {
		"spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false" })
public class Case002ConfigurationTest {
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

		Assertions.assertThrows(RuntimeException.class, thrower::throw001);
	}

	@Test
	public void exception002() {
		final var thrower = beanFactory.getBean(ExceptionThrower.class);

		Assertions.assertThrows(Exception.class, thrower::throw002);
	}
}
