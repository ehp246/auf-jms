package me.ehp246.aufjms.api.spi;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface FromJson {
    List<?> apply(String json, List<To> receivers);

    interface To {
        Class<?> type();

        default List<? extends Annotation> annotations() {
            return List.of();
        }

        default <T> void receive(final T value) {

        }
    }
}
