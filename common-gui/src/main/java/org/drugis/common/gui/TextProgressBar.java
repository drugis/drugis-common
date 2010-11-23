package org.drugis.common.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class TextProgressBar extends JProgressBar {
	public TextProgressBar(TextProgressModel model) {
		setIndeterminate(!model.getDeterminate());
		setString(model.getText());
		setStringPainted(true);
		if(model.getDeterminate()) {
			setValue(proportionToPercentage(model.getProgress()));
		}
		model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if(e.getPropertyName().equals(TextProgressModel.PROPERTY_PROGRESS)) {
					setValue(proportionToPercentage((Double) e.getNewValue()));
				} else if (e.getPropertyName().equals(TextProgressModel.PROPERTY_DETERMINATE)) {
					setIndeterminate(! (Boolean) e.getNewValue());
				} else if (e.getPropertyName().equals(TextProgressModel.PROPERTY_TEXT)) {
					setString((String)e.getNewValue());
				} 
			}
		});
	}

	private int proportionToPercentage(double val) {
		return (int) Math.round(100 * val);
	}
}
