package org.drugis.common;

import org.apache.commons.math3.distribution.TDistribution;

public class StudentTTable {
	/**
	 * Get the critical value for the Student's t distribution, for one-sided 0.025 probability of error.
	 * PRE-COND: v > 0
	 * @param v Degrees of freedom.
	 * @return Critical value.
	 */
	public static double getT(int v) {
		if (v < 1) {
			throw new IllegalArgumentException("student T distribution defined only for positive degrees of freedom");
		}
		TDistribution dist = new TDistribution(v);
		return dist.inverseCumulativeProbability(0.975);
	}
}
