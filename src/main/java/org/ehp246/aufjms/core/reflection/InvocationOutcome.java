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
	
	public static <T> InvocationOutcome<T> returned(T returned) {
		return new InvocationOutcome<T>(returned, null, true);
	}
	
	public static <T> InvocationOutcome<T> thrown(Throwable thrown) {
		return new InvocationOutcome<T>(null, thrown, false);
	}

	public T getReturned() {
		return returned;
	}
	
	public Throwable getThrown() {
		return thrown;
	}
	
	public boolean hasReturned() {
		return hasReturned;
	}
	
	public boolean hasThrown() {
		return !hasReturned;
	}
}