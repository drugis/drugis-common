package org.drugis.common.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.renderer.category.StandardBarPainter;

public final class LookAndFeel {
	public static void configureJFreeChartLookAndFeel() {
		StandardChartTheme chartTheme = new StandardChartTheme("ADDIS");
		chartTheme.setBarPainter(new StandardBarPainter());
		chartTheme.setShadowVisible(false);
		ChartFactory.setChartTheme(chartTheme);
	}
}
