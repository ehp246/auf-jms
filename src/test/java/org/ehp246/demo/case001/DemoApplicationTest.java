package org.ehp246.demo.case001;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RunWith(JUnitPlatform.class)
public class DemoApplicationTest {
	private final AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(
			DemoApplication.class);
	@SuppressWarnings("unchecked")
	private final AtomicReference<CompletableFuture<Object>> ref1 = (AtomicReference<CompletableFuture<Object>>) this.appCtx
			.getBean("ref1");

	@BeforeEach
	void setup() {
		this.ref1.set(new CompletableFuture<Object>());
	}

	@Test
	public void voidReturn001() throws InterruptedException, ExecutionException {
		final var bean = this.appCtx.getBean(Calc.class);

		bean.mem(10);

		Assertions.assertEquals(10, ((Integer) (ref1.get().get())).intValue());
	}

	@Test
	public void instantPayload001() throws InterruptedException, ExecutionException {
		final var bean = this.appCtx.getBean(Alarm.class);

		bean.set(Instant.now());

		Assertions.assertEquals(Instant[].class, ref1.get().get().getClass());
	}

	@Test
	public void test002() throws InterruptedException, ExecutionException {
		final var bean = this.appCtx.getBean(Calc.class);

		final var inc = bean.inc(0);

		Assertions.assertEquals(1, inc);
	}
}
