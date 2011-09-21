/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.common.beans;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;


public class ContentAwareListModelTest {
	public class MyBean extends AbstractObservable {
		private String d_name;
		public MyBean(String name) {
			setName(name);
		}
		public void setName(String newValue) {
			String oldValue = d_name;
			d_name = newValue;
			firePropertyChange("name", oldValue, newValue);
		}
		public String getName() {
			return d_name;
		}
	}

	private ObservableList<MyBean> d_list;
	private ContentAwareListModel<MyBean> d_contentAware;

	@Before
	public void setUp() {
		d_list = new ArrayListModel<MyBean>();
		d_list.add(new MyBean("My MyBean!"));
		d_contentAware = new ContentAwareListModel<MyBean>(d_list, new String[] { "name" });
	}
	
	@Test
	public void testAddElementsFiresChange() {
		ListDataListener mockListenerAdd = createMock(ListDataListener.class);
		mockListenerAdd.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		replay(mockListenerAdd);
		
		d_contentAware.addListDataListener(mockListenerAdd);
		
		d_list.add(new MyBean("His MyBean!"));
		verify(mockListenerAdd);
		d_contentAware.removeListDataListener(mockListenerAdd);
		
		ListDataListener mockListenerRemove = createMock(ListDataListener.class);
		mockListenerRemove.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		replay(mockListenerRemove);
		
		d_contentAware.addListDataListener(mockListenerRemove);
		
		d_list.remove(0);
		verify(mockListenerRemove);
		d_contentAware.removeListDataListener(mockListenerRemove);
	}
	
	@Test
	public void testNameChangeFiresChange() {
		MyBean myBean = new MyBean("My MyBean!");
		d_list.add(myBean);

		ListDataListener mockListener = createMock(ListDataListener.class);
		mockListener.contentsChanged(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.CONTENTS_CHANGED, 1, 1)));
		replay(mockListener);
		
		d_contentAware.addListDataListener(mockListener);
		myBean.setName("His MyBean!");
		verify(mockListener);
		d_contentAware.removeListDataListener(mockListener);
		
		// also test listening to elements initially in the list.
		d_contentAware = new ContentAwareListModel<MyBean>(d_list, new String[] { "name" });
		
		ListDataListener mockListener1 = createMock(ListDataListener.class);
		mockListener1.contentsChanged(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.CONTENTS_CHANGED, 0, 0)));
		replay(mockListener1);
		
		d_contentAware.addListDataListener(mockListener1);
		
		d_list.get(0).setName("His MyBean!");
		verify(mockListener1);
		d_contentAware.removeListDataListener(mockListener1);
	}
	
}
