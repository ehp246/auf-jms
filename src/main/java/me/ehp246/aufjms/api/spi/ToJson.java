package me.ehp246.aufjms.api.spi;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(final List<From> values);

    record From(Object value, Class<?> type, List<? extends Annotation> annotations) {
        public From {
            if (!type.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException(
                        "Un-assignable from " + value.getClass().getName() + " to " + type.getName());
            }
        }

        public From(Object value) {
            this(value, value.getClass(), List.of());
        }

        public From(Object value, Class<?> type) {
            this(value, type, List.of());
        }
    }
}
