package org.drugis.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jgoodies.binding.beans.Observable;

/**
 * Class to manage PropertyChangeListener to facilitate implementation of Observable.
 */
public class ObserverManager {
	private Observable d_source;
	private ConcurrentLinkedQueue<PropertyChangeListener> d_listeners = new ConcurrentLinkedQueue<PropertyChangeListener>();
	
	public ObserverManager(Observable source) {
		d_source = source;
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
