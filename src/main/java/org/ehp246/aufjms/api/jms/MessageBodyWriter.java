package org.ehp246.aufjms.api.jms;

import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MessageBodyWriter<T> {
	T write(List<?> values);
}
