package org.drugis.common.threading;

/**
 * Exception thrown when Suspendable is terminated.
 */
public class AbortedException extends RuntimeException {
	private static final long serialVersionUID = 6661473521989247040L;

	public AbortedException() {
		super("Runnable terminated.");
	}

	public AbortedException(String message) {
		super(message);
	}
}
