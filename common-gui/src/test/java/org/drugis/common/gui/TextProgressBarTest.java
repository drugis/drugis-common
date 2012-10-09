package org.drugis.common.gui;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.gui.task.TextProgressBar;
import org.drugis.common.threading.status.TextProgressModel;
import org.junit.Test;

public class TextProgressBarTest {
	private class TestProgressModel extends AbstractObservable implements TextProgressModel {
		private String d_text;
		private Double d_progress;
		private boolean d_determinate;
		
		public TestProgressModel(String text, boolean determinate, Double progress) {
			d_text = text;
			d_determinate = determinate;
			d_progress = progress;
		}
		public TestProgressModel(String text, double progress) {
			this(text, true, progress);
		}
		public TestProgressModel(String text) {
			this(text, false, null);
		}

		public boolean getDeterminate() { return d_determinate; }
		public Double getProgress() { return d_progress; }
		public String getText() { return d_text; }
		
		public void setDeterminate(boolean determinate) {
			boolean oldValue = d_determinate;
			d_determinate = determinate;
			firePropertyChange(PROPERTY_DETERMINATE, oldValue, d_determinate);
		}
		
		public void setProgress(Double progress) {
			Double oldValue = d_progress;
			d_progress = progress;
			firePropertyChange(PROPERTY_PROGRESS, oldValue, d_progress);
		}
		
		public void setText(String text) {
			String oldValue = d_text;
			d_text = text;
			firePropertyChange(PROPERTY_TEXT, oldValue, d_text);
		}
	}
	
	@Test
	public void testInitialValuesNotIndeterminate() {
		final String text = "Duurt";
		
		TextProgressModel model = new TestProgressModel(text);
		
		TextProgressBar bar = new TextProgressBar(model);
		assertEquals(text, bar.getString());
		assertEquals(false, bar.isIndeterminate());
	}
	
	@Test
	public void testInitialValuesDeterminate() {
		final String text = "Duurt";
		
		TextProgressModel model = new TestProgressModel(text, 0.23);
		
		TextProgressBar bar = new TextProgressBar(model);
		assertEquals(text, bar.getString());
		assertEquals(false, bar.isIndeterminate());
		assertEquals(0, bar.getMinimum());
		assertEquals(100, bar.getMaximum());
		assertEquals(23, bar.getValue());
	}
	


	@Test
	public void testProgression() throws InterruptedException, InvocationTargetException {
		TestProgressModel model = new TestProgressModel("X", 0.23);
		
		TextProgressBar bar = new TextProgressBar(model);
		assertEquals(23, bar.getValue());
		
		model.setProgress(0.29);
		
		SwingUtilities.invokeAndWait(new Runnable() { public void run() {} });

		assertEquals(29, bar.getValue());
	}
	
	@Test
	public void testToDeterminate() throws InterruptedException, InvocationTargetException {
		TestProgressModel model = new TestProgressModel("X");
		TextProgressBar bar = new TextProgressBar(model);
		
		model.setProgress(0.1);
		model.setDeterminate(true);
		
		SwingUtilities.invokeAndWait(new Runnable() { public void run() {} });

		assertEquals(false, bar.isIndeterminate());
		assertEquals(10, bar.getValue());
	}
	
	@Test
	public void testChangeText() throws InterruptedException, InvocationTargetException {
		TestProgressModel model = new TestProgressModel("X");
		TextProgressBar bar = new TextProgressBar(model);
		
		model.setText("Y");

		SwingUtilities.invokeAndWait(new Runnable() { public void run() {} });

		assertEquals("Y", bar.getString());
	}
}
