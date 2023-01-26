package me.ehp246.aufjms.core.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.jms.JMSException;
import jakarta.jms.JMSRuntimeException;

/**
 * @author Lei Yang
 *
 */
public final class OneUtil {
    private OneUtil() {
        super();
    }

    public static String firstUpper(final String value) {
        return value == null || value.length() == 0 ? value
                : value.substring(0, 1).toUpperCase(Locale.US) + value.substring(1);
    }
    public static String toString(final Object value) {
        return toString(value, null);
    }

    public static String toString(final Object value, final String def) {
        return value == null ? def : value.toString();
    }

    public static String nullIfBlank(final Object value) {
        return nullIfBlank(toString(value));
    }

    public static String nullIfBlank(final String value) {
        return value != null && !value.isBlank() ? value : null;
    }

    public static boolean hasValue(final String value) {
        return value != null && !value.isBlank();
    }

    public static boolean hasValue(final Object[] value) {
        return value != null && value.length > 0;
    }

    public static <T> T firstOrNull(final T[] value) {
        return value != null && value.length > 0 ? value[0] : null;
    }

    public static String getIfBlank(final String value, final Supplier<String> supplier) {
        if (value == null || value.isBlank()) {
            return supplier.get();
        } else {
            return value;
        }
    }

    public static Stream<String> streamValues(final Collection<String> values) {
        return Optional.ofNullable(values).orElseGet(ArrayList::new).stream().filter(OneUtil::hasValue);
    }

    public static List<String> listValues(final Collection<String> values) {
        return streamValues(values).collect(Collectors.toList());
    }

    public static <V> V orElse(final Callable<V> callable, final V v) {
        try {
            return callable.call();
        } catch (final Exception e) {
            return v;
        }
    }

    public static <V> V orThrow(final Callable<V> callable) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> V orThrow(final Callable<V> callable, final String msg) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(msg, e);
        }
    }

    public static <V, X extends RuntimeException> V orThrow(final Callable<V> callable,
            final Function<Exception, X> fn) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw fn.apply(e);
        }
    }

    public static boolean isPresent(final List<? extends Annotation> annos, final Class<? extends Annotation> type) {
        return OneUtil.filter(annos, type).findAny().isPresent();
    }

    public static Stream<? extends Annotation> filter(final List<? extends Annotation> annos,
            final Class<? extends Annotation> type) {
        return Optional.ofNullable(annos).filter(Objects::nonNull).orElseGet(ArrayList::new).stream()
                .filter(anno -> anno.annotationType() == type);
    }

    public static RuntimeException ensureRuntime(final Throwable thrown) {
        if (thrown == null) {
            return null;
        }

        if (thrown instanceof final RuntimeException runtime) {
            return runtime;
        }

        if (thrown instanceof final JMSException jmsE) {
            return new JMSRuntimeException(jmsE.getMessage(), jmsE.getErrorCode(), jmsE);
        }

        return new RuntimeException(thrown);
    }
}

