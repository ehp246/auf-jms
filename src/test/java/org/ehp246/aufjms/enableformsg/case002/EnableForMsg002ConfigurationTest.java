package org.ehp246.aufjms.enableformsg.case002;

import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.enableformsg.case002.endpoint002.Calc002;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = EnableForMsg002Configuration.class, properties = "spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false")
public class EnableForMsg002ConfigurationTest {
	@Autowired
	private AnnotationConfigApplicationContext appCtx;

	@BeforeEach
	void setup() {
	}

	@Test
	public void endpoint001(@Mock Msg msg) {
		final var endpoint = appCtx.getBean("@" + EnableForMsg002Configuration.class.getCanonicalName(),
				MsgEndpoint.class);

		Assertions.assertEquals(true, endpoint != null);
		Assertions.assertEquals("", endpoint.getDestinationName());

		Mockito.when(msg.getType()).thenReturn("Calc001");

		Assertions.assertEquals(Calc001.class, endpoint.getResolver().resolve(msg).getInstance().getClass());

		Mockito.when(msg.getType()).thenReturn("Calc002");

		Assertions.assertEquals(Calc002.class, endpoint.getResolver().resolve(msg).getInstance().getClass());
	}

	@Test
	public void endpoint002(@Mock Msg msg) {
		final var endpoint = appCtx.getBean("endpoint002@" + EnableForMsg002Configuration.class.getCanonicalName(),
				MsgEndpoint.class);

		Mockito.when(msg.getType()).thenReturn("Calc001");

		Assertions.assertEquals(null, endpoint.getResolver().resolve(msg), "Should not resolve the un-scanned");

		Mockito.when(msg.getType()).thenReturn("Calc002");

		Assertions.assertEquals(Calc002.class, endpoint.getResolver().resolve(msg).getInstance().getClass());
	}
}
