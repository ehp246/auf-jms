package org.ehp246.aufjms.api.jms;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface FromBody<B> {
	List<?> from(B body, List<Receiver> receivers);

	default Object from(B body, Receiver receiver) {
		return this.from(body, List.of(receiver)).get(0);
	}

	interface Receiver {
		default List<? extends Annotation> getAnnotations() {
			return null;
		}

		Class<?> getType();

		default void receive(Object value) {

		}
	}
}
