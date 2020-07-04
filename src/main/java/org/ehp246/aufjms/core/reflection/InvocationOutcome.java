package org.ehp246.aufjms.core.reflection;

public class InvocationOutcome<T> {
	private final T returned;
	private final Throwable thrown;
	private final boolean hasReturned;

	private InvocationOutcome(final T returned, final Throwable thrown, final boolean hasReturned) {
		super();
		this.returned = returned;
		this.hasReturned = hasReturned;
		this.thrown = thrown;
	}

	public static <T> InvocationOutcome<T> returned(final T returned) {
		return new InvocationOutcome<T>(returned, null, true);
	}

	public static <T> InvocationOutcome<T> thrown(final Throwable thrown) {
		return new InvocationOutcome<T>(null, thrown, false);
	}

	public T returned() {
		return returned;
	}

	public Throwable thrown() {
		return thrown;
	}

	public boolean hasReturned() {
		return hasReturned;
	}

	public boolean hasThrown() {
		return !hasReturned;
	}

	public Object outcomeValue() {
		return hasReturned() ? returned() : thrown();
	}
}