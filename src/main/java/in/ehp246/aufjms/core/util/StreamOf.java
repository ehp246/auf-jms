package in.ehp246.aufjms.core.util;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Lei Yang
 *
 */
public class StreamOf {

	private StreamOf() {
		super();
	}

	/**
	 * A stream with null filtered.
	 * 
	 * @param array
	 * @return
	 */
	public static <T> Stream<T> nonNull(T[] array) {
		return Optional.ofNullable(array).map(Stream::of).orElseGet(Stream::empty).filter(Objects::nonNull);
	}

	public static <T> Stream<T> nonNull(Collection<T> set) {
		return Optional.ofNullable(set).map(Collection::stream).orElseGet(Stream::empty).filter(Objects::nonNull);
	}
}
