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

    record To(Class<?> type, List<? extends Annotation> annotations) {
        public To(Class<?> type) {
            this(type, List.of());
        }
    }
}
