package org.ehp246.aufjms.provider.activemq;

import javax.jms.Queue;
import javax.jms.Topic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.mock.env.MockEnvironment;

/**
 * @author Lei Yang
 *
 */
@RunWith(JUnitPlatform.class)
class PrefixedNameResolverTest {
	private final MockEnvironment env = new MockEnvironment().withProperty("topic1", "topic://topic.1")
			.withProperty("queue1", "queue://queue.1").withProperty("name1", "name.1");
	private final PrefixedNameResolver resolver = new PrefixedNameResolver(env);

	@Test
	void test001() {
		final var resolved = resolver.resolve("direct.name");

		Assertions.assertEquals(true, resolved instanceof Queue);

		Assertions.assertEquals(true, resolved == resolver.resolve("direct.name"), "Should be the same object");
	}

	@Test
	void test002() {
		final var resolved = resolver.resolve("topic://direct.name");

		Assertions.assertEquals(true, resolved instanceof Topic);

		Assertions.assertEquals(true, resolved == resolver.resolve("topic://direct.name"), "Should be the same object");
	}

	@Test
	void test003() {
		final var resolved = resolver.resolve("${topic1}");

		Assertions.assertEquals(true, resolved instanceof Topic);

		Assertions.assertEquals(true, resolved.toString().contains("topic.1"));
	}

	@Test
	void test004() {
		final var resolved = resolver.resolve("topic://${name1}");

		Assertions.assertEquals(true, resolved instanceof Topic);

		Assertions.assertEquals(true, resolved.toString().contains("name.1"));
	}

	@Test
	void test005() {
		final var resolved = resolver.resolve("queue://${name1}");

		Assertions.assertEquals(true, resolved instanceof Queue);

		Assertions.assertEquals(true, resolved.toString().contains("name.1"));

		Assertions.assertEquals(true, resolver.resolve("queue://${name1}") == resolver.resolve("queue://${name1}"));
	}
}
