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
	private static final PropertyChangeListener[] LISTENER_ARRAY = new PropertyChangeListener[]{};
	private Observable d_source;
	private Collection<PropertyChangeListener> d_listeners = createCollection();
	
	public ObserverManager(Observable source) {
		d_source = source;
	}
	
	protected Collection<PropertyChangeListener> createCollection() {
		return new LinkedList<PropertyChangeListener>();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		synchronized (d_listeners) {
			d_listeners.add(listener);			
		}
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		synchronized (d_listeners) {
			d_listeners.remove(listener);			
		}
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent event = new PropertyChangeEvent(d_source, propertyName, oldValue, newValue);
		
		// Copy the list of listeners for thread safety
		// We don't synchronize the for-loop because listeners might be doing time-consuming stuff
		PropertyChangeListener[] arr;
		synchronized (d_listeners) {
			arr = d_listeners.toArray(LISTENER_ARRAY);			
		}
		
		for (PropertyChangeListener l : arr) {
			l.propertyChange(event);
		}
	}
}
