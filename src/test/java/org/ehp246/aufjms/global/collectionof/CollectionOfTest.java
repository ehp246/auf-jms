package org.ehp246.aufjms.global.collectionof;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CollectionOfConfiguration.class, properties = {
		"spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false" })
public class CollectionOfTest {
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

		alarm.get().forEach(instant -> Assertions.assertEquals(Instant.class, instant.getClass()));

	}

	@Test
	void collection003() {
		final var alarm = beanFactory.getBean(SetAlarm.class);
		final var expected = Instant.now();

		final var flat = alarm.flatSet(Set.of(List.of(expected, expected.minusMillis(100)),
				List.of(expected.minusMillis(10)), List.of(expected.minusMillis(20))));

		Assertions.assertEquals(4, flat.size());
	}
}
