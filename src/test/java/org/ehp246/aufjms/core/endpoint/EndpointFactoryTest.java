/**
 * 
 */
package org.ehp246.aufjms.core.endpoint;

import java.util.Set;

import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.endpoint.case001.Cases;
import org.ehp246.aufjms.core.endpoint.case001.Cases.Case001;
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
	public void resolver001() {
		final var assertThrows = Assertions.assertThrows(RuntimeException.class,
				() -> factory.newEndpoint("", Set.of(EndpointFactoryTest.class.getPackageName())),
				"Should throw for duplicate methods on scanning");

		LOGGER.info(assertThrows.getMessage());
	}

	@Test
	public void resolver002() {
		final var resolver = factory.newEndpoint("", Set.of(Cases.class.getPackageName())).getResolver();

		final var msg = Mockito.mock(Msg.class);
		Mockito.when(msg.getType()).thenReturn(Case001.class.getSimpleName());

		final var resolved = resolver.resolve(msg);

		Assertions.assertEquals(1, resolved.size());

		final var resolvedInstance = resolved.get(0);
		Assertions.assertEquals(Case001.class, resolvedInstance.getInstance().getClass());

		Assertions.assertEquals(new ReflectingType<>(Case001.class).findMethod("execute"),
				resolvedInstance.getMethod());
	}
}
