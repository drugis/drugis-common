package org.drugis.common.threading;

/**
 * Class representing failures in computation.
 * 
 * @author Tommi Tervonen <tommi at smaa dot fi>
 *
 */
@SuppressWarnings("serial")
public class FailureException extends RuntimeException {

	public FailureException(String reason) {
		super(reason);
	}
}
