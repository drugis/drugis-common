package org.drugis.common.beans;

import java.beans.PropertyChangeListener;

import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class ListPropertyChangeProxyTest {

	private ObservableList<MyBean> d_list;
	private MyBean d_bean0;
	private MyBean d_bean1;
	private MyBean d_bean3;

	@Before
	public void setUp() {
		d_list = new ArrayListModel<MyBean>();
		d_bean0 = new MyBean("before");
		d_bean1 = new MyBean("before1");
		d_bean3 = new MyBean("before3");
		d_list.add(d_bean0);
		d_list.add(d_bean1);
		d_list.add(d_bean3);
	}
	
	/** 
	 * 	Simply test whether a listener has been attached to list elements at initialization.
	 */
	@Test
	public void testProxy() {
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(d_bean0, MyBean.PROPERTY_NAME, "before", "after");
		new ListPropertyChangeProxy<MyBean>(d_list, listener);
		d_list.get(0).setName("after");
		EasyMock.verify(listener);
	}
	
	/**
	 * Test that listeners are added to elements that are added after list initialization.
	 */
	@Test
	public void testAdd() {
		MyBean bean2 = new MyBean("before2");
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(bean2, MyBean.PROPERTY_NAME, "before2", "after2");
		new ListPropertyChangeProxy<MyBean>(d_list, listener);
		d_list.add(2, bean2);
		bean2.setName("after2");
		EasyMock.verify(listener);
	}
	
	/**
	 * Test that listeners are removed from elements that are removed from the list.
	 */
	@Test
	public void testRemove() {
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(d_bean3, MyBean.PROPERTY_NAME, "before3", "after3");
		new ListPropertyChangeProxy<MyBean>(d_list, listener);
		d_list.remove(1);
		d_bean1.setName("after1");
		d_bean3.setName("after3");
	}
	
	/**
	 * Test that replacing elements in the list adds/removes listeners correctly.
	 */
	@Test
	public void testChange() {
		MyBean bean2 = new MyBean("before2");
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(bean2, MyBean.PROPERTY_NAME, "before2", "after2");
		new ListPropertyChangeProxy<MyBean>(d_list, listener);
		d_list.set(1, bean2);
		bean2.setName("after2");
		d_bean1.setName("after1");
		EasyMock.verify(listener);
	}
	
}
