package org.drugis.common.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;

import com.jgoodies.binding.beans.Observable;

/**
 * Class to manage PropertyChangeListener to facilitate implementation of Observable.
 */
public class ObserverManager {
	private Observable d_source;
	private Collection<PropertyChangeListener> d_listeners = createCollection();
	
	public ObserverManager(Observable source) {
		d_source = source;
	}
	
	protected Collection<PropertyChangeListener> createCollection() {
		return new LinkedList<PropertyChangeListener>();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		d_listeners.add(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		d_listeners.remove(listener);
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent event = new PropertyChangeEvent(d_source, propertyName, oldValue, newValue);
		for (PropertyChangeListener l : d_listeners) {
			l.propertyChange(event);
		}
	}
}
