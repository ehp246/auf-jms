package org.ehp246.aufjms.inegration.case001;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.ehp246.aufjms.api.jms.ReplyToNameSupplier;
import org.ehp246.aufjms.core.jackson.JacksonConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = Case001Configuration.class, properties = "spring.activemq.broker-url=vm://activemq?broker.persistent=false&broker.useShutdownHook=false")
class Case001ConfigurationTest {
	@Autowired
	private ApplicationContext appCtx;

	@Autowired
	private Calc calc;

	@Autowired
	@Qualifier("ref1")
	private AtomicReference<CompletableFuture<Object>> ref1;

	@Autowired
	private Alarm alarm;

	@Autowired
	@Qualifier(JacksonConfiguration.BEAN_NAME_OBJECT_MAPPER)
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		this.ref1.set(new CompletableFuture<Object>());
	}

	@Test
	public void voidReturn001() throws InterruptedException, ExecutionException {
		calc.mem(10);

		Assertions.assertEquals(10, ((Integer) (ref1.get().get())).intValue());
	}

	@Test
	public void instantPayload001() throws JsonProcessingException, InterruptedException, ExecutionException {
		final var expected = Instant.now();

		alarm.set(expected);

		Assertions.assertEquals(objectMapper.writeValueAsString(new Object[] { expected }), ref1.get().get());
	}

	@Test
	public void return001() throws InterruptedException, ExecutionException {
		final var inc = calc.inc(0);

		Assertions.assertEquals(1, inc);
	}

	@Test
	public void replyDestination001() {
		Assertions.assertEquals(Case001Configuration.class.getCanonicalName() + ".reply",
				appCtx.getBean(ReplyToNameSupplier.class).get());
	}
}
