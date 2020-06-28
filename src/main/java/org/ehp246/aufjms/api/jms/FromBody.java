package org.ehp246.aufjms.api.jms;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface FromBody<B> {
	void from(B body, List<Receiver> receivers);

	interface Receiver {
		List<? extends Annotation> getAnnotations();

		Class<?> getType();

		void receive(Object value);
	}
}
