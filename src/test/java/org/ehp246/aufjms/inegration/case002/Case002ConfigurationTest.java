package org.ehp246.aufjms.inegration.case002;

import org.ehp246.aufjms.inegration.case002.bymsg.Add;
import org.ehp246.aufjms.inegration.case002.bymsg.Countor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = Case002Configuration.class, properties = "spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false")
public class Case002ConfigurationTest {
	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Test
	public void add001() {
		Assertions.assertEquals(11, beanFactory.getBean(Add.class).add(10, 1));
	}

	@Test
	public void mem001() {
		final var countor = beanFactory.getBean(Countor.class);

		countor.set(-1);

		Assertions.assertEquals(-1, countor.get());

		Assertions.assertEquals(0, countor.add(1));

		Assertions.assertEquals(0, countor.get());
	}
}
