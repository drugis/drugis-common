package org.drugis.common.stat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatisticsTest {
	private static final double EPSILON = 0.000001;
	@Test
	public void testMeanDifference() {
		EstimateWithPrecision e = Statistics.meanDifference(
			-2.5, 1.6, 177, -2.6, 1.5, 176);
		assertEquals(-0.1, e.getPointEstimate(), EPSILON);
		assertEquals(0.1650678, e.getStandardError(), EPSILON);
	}
}
