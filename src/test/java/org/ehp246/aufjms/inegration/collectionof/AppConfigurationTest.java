package org.ehp246.aufjms.inegration.collectionof;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AppConfiguration.class, properties = {
		"spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false" })
class AppConfigurationTest {
	@Autowired
	private ListableBeanFactory beanFactory;

	@Test
	void collection001() {
		final var alarm = beanFactory.getBean(SetAlarm.class);

		final var expected = Instant.now();
		alarm.set(expected);

		final Instant set = alarm.get().stream().findAny().get();

		Assertions.assertEquals(expected, set);
	}

	@Test
	void collection002() {
		final var alarm = beanFactory.getBean(SetAlarm.class);

		final var expected = Instant.now();
		alarm.set(Set.of(expected, expected.minusMillis(100)));

		final Instant[] set = (Instant[]) alarm.get().toArray();

		Assertions.assertEquals(expected, set[0]);
	}

}
