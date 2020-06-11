package org.ehp246.demo.case002;

import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.demo.case002.calc.Calc;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RunWith(JUnitPlatform.class)
public class DemoApplicationTest {
	private final AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(
			DemoApplication.class);

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

		Assertions.assertEquals(1, resolved.size());
		Assertions.assertEquals(Calc.class, resolved.get(0).getInstance().getClass());
	}
}
