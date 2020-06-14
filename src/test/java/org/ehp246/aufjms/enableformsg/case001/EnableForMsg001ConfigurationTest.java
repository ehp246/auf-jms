package org.ehp246.aufjms.enableformsg.case001;

import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.Msg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootTest(classes = EnableForMsg001Configuration.class, properties = "spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false")
public class EnableForMsg001ConfigurationTest {
	@Autowired
	private AnnotationConfigApplicationContext appCtx;

	@BeforeEach
	void setup() {
	}

	@Test
	public void destination001() {
		final var resolver = appCtx.getBean(MsgEndpoint.class);

		Assertions.assertEquals(true, resolver != null);
		Assertions.assertEquals("", resolver.getDestinationName());

		Assertions.assertEquals(EnableForMsg001Configuration.DefaultQueue,
				appCtx.getBean(DestinationNameResolver.class).resolve(""));
	}

	@Test
	public void registry001() {
		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn("Calc");

		final var resolved = appCtx.getBean(MsgEndpoint.class).getResolver().resolve(msg);

		Assertions.assertEquals(true, resolved != null);
		Assertions.assertEquals(Calc.class, resolved.getInstance().getClass());
	}
}
