package org.drugis.common.threading;

/**
 * Exception thrown when Suspendable is terminated.
 */
public class TerminatedException extends Exception {
	public TerminatedException() {
		super("Runnable terminated.");
	}

	public TerminatedException(String message) {
		super(message);
	}
}
