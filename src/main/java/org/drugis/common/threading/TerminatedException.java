package org.drugis.common.threading;

/**
 * Exception thrown when Suspendable is terminated.
 */
public class TerminatedException extends Exception {
	private static final long serialVersionUID = 6661473521989247040L;

	public TerminatedException() {
		super("Runnable terminated.");
	}

	public TerminatedException(String message) {
		super(message);
	}
}
