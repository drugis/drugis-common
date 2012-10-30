package org.drugis.common.event;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class IndifferentListDataListener implements ListDataListener {

	public void intervalAdded(ListDataEvent e) {
		update();
	}

	public void intervalRemoved(ListDataEvent e) {
		update();		
	}

	public void contentsChanged(ListDataEvent e) {
		update();		
	}

	protected abstract void update();
}
