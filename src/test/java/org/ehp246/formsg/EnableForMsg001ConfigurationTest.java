package org.ehp246.formsg;

import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.demo.case002.calc.Calc;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnableForMsg001Configuration.class, properties = "spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false")
public class EnableForMsg001ConfigurationTest {
	@Autowired
	private AnnotationConfigApplicationContext appCtx;
	// new AnnotationConfigApplicationContext(EnableForMsg001Configuration.class);

	@BeforeEach
	void setup() {
	}

	@Test
	public void resolver001() {
		final var resolver = appCtx.getBean(MsgEndpoint.class);

		Assertions.assertEquals(true, resolver != null);
		Assertions.assertEquals("", resolver.getDestinationName());
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
