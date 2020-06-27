package org.ehp246.aufjms.core.formsg;

import java.util.Set;

import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.core.formsg.case001.Case001;
import org.ehp246.aufjms.core.formsg.case002.Case002;
import org.ehp246.aufjms.core.formsg.case003.Case003;
import org.ehp246.aufjms.core.formsg.case004.Case004;
import org.ehp246.aufjms.core.formsg.case005.Case005;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ForMsgScannerTest {

	@Test
	void case001() {
		final var found = new ForMsgScanner(Set.of(Case001.class.getPackageName())).perform();

		Assertions.assertEquals(1, found.size());

		final var one = found.stream().findAny().get();

		Assertions.assertEquals(Case001.class, one.getInstanceType());
		Assertions.assertEquals(Case001.class.getSimpleName(), one.getMsgType());
		Assertions.assertEquals(InstanceScope.MESSAGE, one.getInstanceScope());
		Assertions.assertEquals(InvocationModel.DEFAULT, one.getInvocationModel());

		final var methods = one.getMethods();

		Assertions.assertEquals(3, methods.size());
	}

	@Test
	void duplicateDefaults001() {
		Assertions.assertThrows(RuntimeException.class,
				() -> new ForMsgScanner(Set.of(Case002.class.getPackageName())).perform());
	}

	@Test
	void duplicateInvokingNames001() {
		Assertions.assertThrows(RuntimeException.class,
				() -> new ForMsgScanner(Set.of(Case003.class.getPackageName())).perform());
	}

	@Test
	void missingInvoking001() {
		Assertions.assertThrows(RuntimeException.class,
				() -> new ForMsgScanner(Set.of(Case004.class.getPackageName())).perform());
	}

	@Test
	void scope001() {
		Assertions.assertThrows(RuntimeException.class,
				() -> new ForMsgScanner(Set.of(Case005.class.getPackageName())).perform());
	}
}
