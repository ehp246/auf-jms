package me.ehp246.aufjms.api.jms;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface FromBody<B> {
    List<?> from(B body, List<Receiver<?>> receivers);

    @SuppressWarnings("unchecked")
    default <T> T from(final B body, final Receiver<T> receiver) {
        return (T) this.from(body, List.of(receiver)).get(0);
    }

    interface Receiver<T> {
        default List<? extends Annotation> getAnnotations() {
            return List.of();
        }

        Class<? extends T> getType();

        default void receive(final T value) {

        }
    }
}
