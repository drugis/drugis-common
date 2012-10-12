package org.drugis.common.gui.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.drugis.common.threading.status.TextProgressModel;

@SuppressWarnings("serial")
public class TextProgressBar extends JProgressBar {
	public TextProgressBar(TextProgressModel model) {
//		setIndeterminate(!model.getDeterminate());
		setString(model.getText());
		setStringPainted(true);
		if(model.getDeterminate()) {
			setValue(proportionToPercentage(model.getProgress()));
		} else {
			setValue(0);
		}
		model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if(e.getPropertyName().equals(TextProgressModel.PROPERTY_PROGRESS)) {
					invokeSetValue(proportionToPercentage((Double) e.getNewValue()));
				} else if (e.getPropertyName().equals(TextProgressModel.PROPERTY_DETERMINATE)) {
					invokeSetIndeterminate(!(Boolean)e.getNewValue());
				} else if (e.getPropertyName().equals(TextProgressModel.PROPERTY_TEXT)) {
					invokeSetString((String)e.getNewValue());
				} 
			}


		});
	}
	
	private void invokeSetString(final String v) {
		Runnable r = new Runnable() {
			public void run() {
				setString(v);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	private void invokeSetIndeterminate(final boolean v) {
		Runnable r = new Runnable() {
			public void run() {
				setIndeterminate(v);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	private void invokeSetValue(final int v) {
		Runnable r = new Runnable() {
			public void run() {
				setValue(v);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	private int proportionToPercentage(double val) {
		return (int) Math.round(100 * val);
	}
}
