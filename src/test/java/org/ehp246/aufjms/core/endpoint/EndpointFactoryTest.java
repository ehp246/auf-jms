/**
 * 
 */
package org.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.ehp246.aufjms.api.endpoint.ExecutionModel;
import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.endpoint.case001.Cases;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case001;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case002;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case003;
import org.ehp246.aufjms.core.endpoint.error001.Error001;
import org.ehp246.aufjms.core.endpoint.error002.Error002;
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
public class EndpointFactoryTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(EndpointFactoryTest.class);
	private final AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(
			EndpointFactory.class);
	private final EndpointFactory factory = appCtx.getBean(EndpointFactory.class);

	@Test
	public void resolverConflicts001() {
		final var assertThrows = Assertions.assertThrows(RuntimeException.class,
				() -> factory.newEndpoint("", Set.of(Error001.class.getPackageName())),
				"Should throw for duplicate methods on scanning");

		LOGGER.info(assertThrows.getMessage());
	}

	@Test
	public void resolverConflicts002() {
		final var assertThrows = Assertions.assertThrows(RuntimeException.class,
				() -> factory.newEndpoint("", Set.of(Error002.class.getPackageName())),
				"Should throw for duplicate methods on scanning");

		LOGGER.info(assertThrows.getMessage());
	}

	@Test
	public void resolverDefaultMethod002() {
		final var resolver = factory.newEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case001.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(1, resolved.size());

		final var resolvedInstance = resolved.get(0);

		Assertions.assertEquals(Case001.class, resolvedInstance.getInstance().getClass());
		Assertions.assertEquals(new ReflectingType<>(Case001.class).findMethod("execute"),
				resolvedInstance.getMethod());
		Assertions.assertEquals(ExecutionModel.DEFAULT, resolvedInstance.getExecutionModel());
	}

	@Test
	public void resolverAnnotatedMethod003() {
		final var resolver = factory.newEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case002.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(1, resolved.size());

		final var resolvedInstance = resolved.get(0);

		Assertions.assertEquals(Case002.class, resolvedInstance.getInstance().getClass());
		Assertions.assertEquals(new ReflectingType<>(Case002.class).findMethod("m001"), resolvedInstance.getMethod());
		Assertions.assertEquals(ExecutionModel.DEFAULT, resolvedInstance.getExecutionModel());
	}

	@Test
	public void resolverAnnotatedMethod004() {
		final var resolver = factory.newEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case003.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(1, resolved.size());

		final var resolvedInstance = resolved.get(0);

		Assertions.assertEquals(Case003.class, resolvedInstance.getInstance().getClass());
		Assertions.assertEquals(new ReflectingType<>(Case003.class).findMethod("m001"), resolvedInstance.getMethod());
		Assertions.assertEquals(ExecutionModel.SYNC, resolvedInstance.getExecutionModel());
	}
}
