package me.ehp246.aufjms.global.destination.case002;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufjms.api.endpoint.MsgEndpoint;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = DestinationConfiguration002.class, properties = {
		"spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false" })
public class DestinationTest002 {
	@Autowired
	private AnnotationConfigApplicationContext appCtx;

	@Test
	void destination001() {
		final var endpoints = appCtx.getBeansOfType(MsgEndpoint.class);

		Assertions.assertEquals(2, endpoints.size());

		final var names = endpoints.values().stream().map(MsgEndpoint::getDestinationName).collect(Collectors.toSet());

		Assertions.assertEquals(true,
				names.contains("queue://" + DestinationConfiguration002.class.getName() + ".request"),
				"Should have the default request queue name");

		Assertions.assertEquals(true,
				names.contains("topic://" + DestinationConfiguration002.class.getName() + ".reply"),
				"Should have the default reply topic name");
	}
}
