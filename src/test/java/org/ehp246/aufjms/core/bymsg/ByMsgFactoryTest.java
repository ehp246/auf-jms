package org.ehp246.aufjms.core.bymsg;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.api.jms.MessagePortProvider;
import org.ehp246.aufjms.api.jms.MessageSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class ByMsgFactoryTest {
	private final AtomicReference<MessageSupplier> ref = new AtomicReference<MessageSupplier>(null);
	private final AtomicReference<String> refDestination = new AtomicReference<>(null);
	private final MessagePortProvider portProvider = supplier -> msgSupplier -> {
		supplier.getTo();
		ref.set(msgSupplier);
		return null;
	};
	private final DestinationNameResolver nameResolver = name -> {
		refDestination.set(name);
		return null;
	};

	private final ReplyEndpointConfiguration replyConfig = new ReplyEndpointConfiguration(() -> null, null, 1, 1);

	private final ByMsgFactory factory = new ByMsgFactory(portProvider, nameResolver, replyConfig);

	@BeforeEach
	public void beforeEach() {
		ref.set(null);
		refDestination.set(null);
	}

	/**
	 * General interface
	 */
	@Test
	public void general001() {
		Assertions.assertTrue(factory.newInstance(GeneralTestCase.Case001.class) instanceof GeneralTestCase.Case001);
	}

	@Test
	public void general002() {
		Assertions.assertEquals(2, factory.newInstance(GeneralTestCase.Case001.class).inc(1),
				"Should call default implementation");

		Assertions.assertNull(ref.get(), "Should not send message");
	}

	/**
	 * To destination
	 */
	@Test
	public void destination001() {
		final var newInstance = factory.newInstance(DestinationTestCase.Case001.class);

		newInstance.m001();

		Assertions.assertEquals("test.inbox", refDestination.get(), "Should be simple value of annotation");
	}

	@Test
	public void destination002() {
		final var newInstance = factory.newInstance(DestinationTestCase.Case002.class);

		newInstance.m001();

		Assertions.assertEquals("", refDestination.get(),
				"Should not happen since the interface won't be scanned and proxied");
	}

	/**
	 * ReplyTo
	 */

	/**
	 * Correlation Id
	 */
	@Test
	public void correlationId001() {
		final var newInstance = factory.newInstance(TypeTestCase.TypeCase001.class);

		newInstance.m001();

		Assertions.assertNotNull(ref.get().getCorrelationId(), "Should have id");
		Assertions.assertEquals(ref.get().getCorrelationId(), ref.get().getCorrelationId(), "Should not change");

	}

	/**
	 * Type
	 */
	@Test
	public void type001() {
		final var newInstance = factory.newInstance(TypeTestCase.TypeCase001.class);

		newInstance.m001();

		Assertions.assertEquals("TypeCase001", ref.get().getType(), "Type should derive from class name by default");

		ref.set(null);
		newInstance.m005();

		Assertions.assertEquals("M005", ref.get().getType(), "Should derive from method name");

		ref.set(null);
		final var expected = UUID.randomUUID().toString();
		newInstance.m002(expected);
		Assertions.assertEquals(expected, ref.get().getType(), "Should take parameter value");

		ref.set(null);
		newInstance.m003(UUID.randomUUID().toString());
		Assertions.assertEquals("Type001", ref.get().getType(), "Should take annotation value");

		ref.set(null);
		newInstance.m004(UUID.randomUUID().toString());
		Assertions.assertEquals("Type002", ref.get().getType(), "Should take annotation value");

		ref.set(null);
		newInstance.m006(UUID.randomUUID().toString());
		Assertions.assertEquals("Type004", ref.get().getType(), "Should take annotation value from parameter");
	}

	@Test
	public void type002() {
		final var newInstance = factory.newInstance(TypeTestCase.TypeCase001.class);

		final var expected = UUID.randomUUID().toString();
		newInstance.m002(expected);
		Assertions.assertEquals(expected, ref.get().getType(), "Should take parameter value");

		newInstance.m002("");
		Assertions.assertEquals("", ref.get().getType(), "Should take parameter value");

		newInstance.m002(null);
		Assertions.assertEquals(null, ref.get().getType(), "Should take parameter value");
	}

	@Test
	public void type003() {
		final var newInstance = factory.newInstance(TypeTestCase.TypeCase001.class);

		final var expected = UUID.randomUUID().toString();
		newInstance.m007(expected, UUID.randomUUID().toString());
		Assertions.assertEquals(expected, ref.get().getType(), "Should take the first annotated");
	}

	@Test
	public void timeout001() {
		final var newInstance = factory.newInstance(TimeoutTestCases.Case001.class);

		Assertions.assertTimeout(Duration.ofMillis(100),
				() -> Assertions.assertThrows(RuntimeException.class, newInstance::m001),
				"Should use timeout from the global setting");

		Assertions.assertTimeout(Duration.ofMillis(100),
				() -> Assertions.assertThrows(TimeoutException.class, newInstance::m002),
				"Should throw the exception witout wrapping");
	}

	@Test
	public void timeout002() {
		final var newInstance = factory.newInstance(TimeoutTestCases.Case002.class);

		Assertions.assertTimeout(Duration.ofMillis(1000),
				() -> Assertions.assertThrows(RuntimeException.class, newInstance::m001),
				"Should use timeout from the annotation");
	}

	@Test
	public void ttl001() {
		factory.newInstance(TtlTestCases.Case001.class).m001();

		Assertions.assertEquals(1, ref.get().getTtl(), "Should be the value from the annotation");
	}

	@Test
	public void ttl002() {
		factory.newInstance(TtlTestCases.Case002.class).m001();

		Assertions.assertEquals(500, ref.get().getTtl(), "Should be the value from the annotation");
	}

	/**
	 * Body
	 */
	@Test
	public void body001() {
		final var newInstance = factory.newInstance(BodyTestCase.class);
		newInstance.m001();

		Assertions.assertEquals(0, ref.get().getBodyValues().size(), "Should always return a list");

		ref.set(null);
		newInstance.m001(0);
		var bodyValue = ref.get().getBodyValues();

		Assertions.assertEquals(1, bodyValue.size(), "Should always return a list");
		Assertions.assertEquals(0, bodyValue.get(0), "Should be argument");

		ref.set(null);
		newInstance.m001(10, 1);
		bodyValue = ref.get().getBodyValues();

		Assertions.assertEquals(2, bodyValue.size(), "Should always return a list");
		Assertions.assertEquals(10, bodyValue.get(0), "Should be argument");
		Assertions.assertEquals((long) 1, bodyValue.get(1), "Should be argument");

		ref.set(null);
		newInstance.m001(null);
		bodyValue = ref.get().getBodyValues();

		Assertions.assertEquals(1, bodyValue.size(), "Should always return a list");
		Assertions.assertEquals(null, bodyValue.get(0), "Should be argument");

		ref.set(null);
		final var expected = new Object();
		newInstance.m001(expected, 12);
		bodyValue = ref.get().getBodyValues();

		Assertions.assertEquals(2, bodyValue.size(), "Should always return a list");
		Assertions.assertEquals(expected, bodyValue.get(0), "Should be argument");
		Assertions.assertEquals(12, bodyValue.get(1), "Should be argument");
	}

	@Test
	public void body002() {
		final var newInstance = factory.newInstance(BodyTestCase.class);
		newInstance.m002("");

		final var body = ref.get().getBodyValues();
		Assertions.assertEquals(0, body.size(), "Should skip annotated arguments");
	}

	@Test
	public void body003() {
		final var newInstance = factory.newInstance(BodyTestCase.class);
		final var expected = UUID.randomUUID().toString();
		newInstance.m002("", null, expected);

		final var body = ref.get().getBodyValues();
		Assertions.assertEquals(1, body.size(), "Should skip annotated arguments");
		Assertions.assertEquals(expected, body.get(0), "Should have argument value");
	}

	@Test
	public void body004() {
		final var newInstance = factory.newInstance(BodyTestCase.class);
		final var expected = UUID.randomUUID().toString();
		newInstance.m003("", null, expected);

		final var body = ref.get().getBodyValues();
		Assertions.assertEquals(2, body.size(), "Should skip annotated arguments");
		Assertions.assertEquals("", body.get(0), "Should have argument value");
		Assertions.assertEquals(expected, body.get(1), "Should have argument value");
	}

	@Test
	public void invoking001() {
		final var newInstance = factory.newInstance(InvokingTestCase.Case001.class);

		newInstance.m001();

		Assertions.assertEquals("m001", ref.get().getInvoking());

		ref.set(null);
		newInstance.m002();

		Assertions.assertEquals("m002", ref.get().getInvoking());

		ref.set(null);
		newInstance.m003();

		Assertions.assertEquals("m003", ref.get().getInvoking());

		ref.set(null);
		newInstance.m003(0);

		Assertions.assertEquals("m003-1", ref.get().getInvoking());
	}
}
