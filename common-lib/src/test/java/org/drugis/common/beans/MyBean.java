/**
 * 
 */
package org.drugis.common.beans;

public class MyBean extends AbstractObservable {
	public static final String PROPERTY_NAME = "name";
	
	private String d_name;
	public MyBean(String name) {
		setName(name);
	}
	public void setName(String newValue) {
		String oldValue = d_name;
		d_name = newValue;
		firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}
	public String getName() {
		return d_name;
	}
}