/**
 * 
 */
package org.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.endpoint.case001.Cases;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case001;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case002;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case003;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case004;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case005;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case006;
import org.ehp246.aufjms.core.endpoint.case007.DuplicateType007;
import org.ehp246.aufjms.core.endpoint.case008.MultiType008;
import org.ehp246.aufjms.core.endpoint.error001.Error001;
import org.ehp246.aufjms.core.endpoint.error002.Error002;
import org.ehp246.aufjms.core.endpoint.error003.Error003;
import org.ehp246.aufjms.core.reflection.ReflectingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Lei Yang
 *
 */
@RunWith(JUnitPlatform.class)
public class AtEndpointFactoryTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(AtEndpointFactoryTest.class);
	private final AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(
			AtEndpointFactory.class, Case004.class, Case005.class);
	private final AtEndpointFactory factory = appCtx.getBean(AtEndpointFactory.class);

	@Test
	public void destination001() {
		Assertions.assertEquals(null,
				factory.newMsgEndpoint(null, Set.of(Case001.class.getPackageName())).getDestinationName());

		Assertions.assertEquals("",
				factory.newMsgEndpoint("", Set.of(Case001.class.getPackageName())).getDestinationName());

		Assertions.assertEquals("1",
				factory.newMsgEndpoint("1", Set.of(Case001.class.getPackageName())).getDestinationName());
	}

	@Test
	public void resolverConflicts001() {
		final var assertThrows = Assertions.assertThrows(RuntimeException.class,
				() -> factory.newMsgEndpoint("", Set.of(Error001.class.getPackageName())),
				"Should throw for duplicate methods on scanning");

		LOGGER.info(assertThrows.getMessage());
	}

	@Test
	public void resolverConflicts002() {
		final var assertThrows = Assertions.assertThrows(RuntimeException.class,
				() -> factory.newMsgEndpoint("", Set.of(Error002.class.getPackageName())),
				"Should throw for duplicate methods on scanning");

		LOGGER.info(assertThrows.getMessage());
	}

	@Test
	public void resolverInstantation001() {
		final var assertThrows = Assertions.assertThrows(RuntimeException.class,
				() -> factory.newMsgEndpoint("", Set.of(Error003.class.getPackageName())),
				"Should throw for duplicate methods on scanning");

		LOGGER.info(assertThrows.getMessage());
	}

	@Test
	public void resolverCase001() {
		final var resolver = factory.newMsgEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case001.class.getSimpleName());

		final var resolvedInstance = resolver.resolve(msg);

		Assertions.assertEquals(Case001.class, resolvedInstance.getInstance().getClass());
		Assertions.assertEquals(new ReflectingType<>(Case001.class).findMethod("execute"),
				resolvedInstance.getMethod());
		Assertions.assertEquals(InvocationModel.DEFAULT, resolvedInstance.getExecutionModel());

		final var resolved2 = resolver.resolve(msg);

		Assertions.assertNotEquals(resolvedInstance.getInstance(), resolved2.getInstance(),
				"Should be a new object for each resolution");
	}

	@Test
	public void resolverCase002() {
		final var resolver = factory.newMsgEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case002.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(Case002.class, resolved.getInstance().getClass());
		Assertions.assertEquals(new ReflectingType<>(Case002.class).findMethod("m001"), resolved.getMethod());
		Assertions.assertEquals(InvocationModel.DEFAULT, resolved.getExecutionModel());
	}

	@Test
	public void resolverCase003() {
		final var resolver = factory.newMsgEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case003.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(Case003.class, resolved.getInstance().getClass());
		Assertions.assertEquals(new ReflectingType<>(Case003.class).findMethod("m001"), resolved.getMethod());
		Assertions.assertEquals(InvocationModel.SYNC, resolved.getExecutionModel());
	}

	@Test
	public void resolverCase004() {
		final var resolver = factory.newMsgEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case004.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(Case004.class, resolved.getInstance().getClass());

		Assertions.assertEquals(true, appCtx.getBean(Case004.class) == resolved.getInstance(), "Should be the bean");

		Assertions.assertEquals(true, appCtx.getBean(Case004.class) == resolver.resolve(msg).getInstance(),
				"Should be the bean again");
	}

	@Test
	public void resolverCase005() {
		final var resolver = factory.newMsgEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case005.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(Case005.class, resolved.getInstance().getClass());

		Assertions.assertEquals(false, appCtx.getBean(Case005.class) == resolved.getInstance(),
				"Should not be the bean");
	}

	@Test
	public void resolverCase006() {
		final var resolver = factory.newMsgEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case006.class.getSimpleName());

		final var e = Assertions.assertThrows(Exception.class, () -> resolver.resolve(msg),
				"Should allow registration but not resolution");

		LOGGER.info(e.getMessage());
	}

	@Test
	public void resolverDuplicateType007() {
		final var e = Assertions.assertThrows(RuntimeException.class,
				() -> factory.newMsgEndpoint("", Set.of(DuplicateType007.class.getPackageName())).getResolver());

		LOGGER.info(e.getMessage());
	}

	@Test
	public void resolverMultiType008() {
		final var resolver = factory.newMsgEndpoint("", Set.of(MultiType008.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn("");

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(null, resolved, "Should not match empty string");

		Mockito.when(msg.getType()).thenReturn(" ");

		Assertions.assertEquals(null, resolver.resolve(msg), "Should not match blank string");

		Mockito.when(msg.getType()).thenReturn("MultiType008");

		Assertions.assertEquals(null, resolver.resolve(msg), "Should not match default type");
	}

	@Test
	public void resolverMultiType009() {
		final var resolver = factory.newMsgEndpoint("", Set.of(MultiType008.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);

		Mockito.when(msg.getType()).thenReturn("MultiType008");
		Assertions.assertEquals(null, resolver.resolve(msg), "Should not match to the default");

		Mockito.when(msg.getType()).thenReturn("MultiType008-v1");

		Assertions.assertEquals(new ReflectingType<>(MultiType008.class).findMethod("m002"),
				resolver.resolve(msg).getMethod(), "Should match to m002");

		Mockito.when(msg.getExecuting()).thenReturn("m001");

		Assertions.assertEquals(new ReflectingType<>(MultiType008.class).findMethod("m001"),
				resolver.resolve(msg).getMethod(), "Should match to m001");
		Mockito.when(msg.getExecuting()).thenReturn("m001");

		Mockito.when(msg.getExecuting()).thenReturn("m002");

		Assertions.assertEquals(null, resolver.resolve(msg), "Should not match");
	}
}
