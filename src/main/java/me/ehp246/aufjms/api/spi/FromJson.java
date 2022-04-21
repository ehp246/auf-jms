package me.ehp246.aufjms.api.spi;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface FromJson {
    List<?> apply(String json, List<Receiver<?>> receivers);

    interface Receiver<T> {
        default List<? extends Annotation> annotations() {
            return List.of();
        }

        Class<? extends T> type();

        default void receive(final T value) {

        }
    }
}
