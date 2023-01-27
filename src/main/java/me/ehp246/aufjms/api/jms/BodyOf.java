package me.ehp246.aufjms.api.jms;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Provides typing information to serialize and de-serialize JSON payload.
 * <p>
 * Supports {@linkplain Collection} types, e.g., {@linkplain Set} and
 * {@linkplain List}. Not {@linkplain Map}.
 *
 * @author Lei Yang
 * @since 2.1
 */
public final class BodyOf<T> {
    public static final BodyOf<Map<String, Object>> MAP = new BodyOf<Map<String, Object>>(null, Map.class);

    private final List<Class<?>> reifying;

    /**
     * For {@linkplain ObjectWriter#withView(Class)} and
     * {@linkplain ObjectReader#withView(Class)}.
     */
    private final Class<?> view;

    public BodyOf(final Class<T> type) {
        this(null, type);
    }

    /**
     * At least one type should be specified. Does not accept <code>null</code>'s.
     */
    public BodyOf(final Class<?> view, final Class<?>... reifying) {
        this.view = view;
        this.reifying = List.of(reifying);
    }

    public Class<?> first() {
        return this.reifying.get(0);
    }

    /**
     * Should have at least one type. Non-modifiable.
     */
    public List<Class<?>> reifying() {
        return this.reifying;
    }

    public Class<?> view() {
        return view;
    }
}
