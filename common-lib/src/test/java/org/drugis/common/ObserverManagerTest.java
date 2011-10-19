package org.drugis.common;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.common.beans.ObserverManager;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.beans.Observable;

public class ObserverManagerTest {
	
	private ObserverManager d_om;
	private Observable d_source;
	private boolean d_running;
	protected Throwable d_thrown;

	@Before
	public void setUp() {
		d_source = createMock(Observable.class);
		d_om = new ObserverManager(d_source);
		d_running = true;
	}
	
	@Test
	public void testSingleListener() {
		
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(d_source, "test", "X", "Y");
		d_om.addPropertyChangeListener(listener);
		d_om.firePropertyChange("test", "X", "Y");
		verify(listener);
	}
	
	@Test
	public void testMultipleListener() {
		PropertyChangeListener listener0 = JUnitUtil.mockStrictListener(d_source, "test", "X", "Y");
		d_om.addPropertyChangeListener(listener0);
		PropertyChangeListener listener1 = JUnitUtil.mockStrictListener(d_source, "test", "X", "Y");
		d_om.addPropertyChangeListener(listener1);
		d_om.firePropertyChange("test", "X", "Y");
		verify(listener0);
		verify(listener1);
	}
	
	@Test
	public void testRemoveListener() {
		PropertyChangeListener listener = createMock(PropertyChangeListener.class);
		replay(listener);
		
		d_om.addPropertyChangeListener(listener);
		d_om.removePropertyChangeListener(listener);
		d_om.firePropertyChange("test", "X", "Y");
		verify(listener);
	}
	
	/**
	 * @see http://mantis.drugis.org/view.php?id=376
	 */
	@Test
	public void testThreadSafety() throws Throwable {
		ThreadGroup group = new ThreadGroup("happy") {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				d_thrown = e;
			}
		};
		
		// Thread that adds listeners (which run for a while to trigger race condition)
		Thread listen = new Thread(group, new Runnable() {
			public void run() {
				while(d_running) {
					d_om.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent evt) {
							int untilImDead = 30;
							try {
								Thread.sleep(untilImDead);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
		
		// Thread that fires events
		Thread fire = new Thread(group, new Runnable() {
			public void run() {
				while(d_running) {
					d_om.firePropertyChange("je moeder", "je vader", "je stief");
				}
			}
		});
		
		listen.start();
		fire.start();
		
		// Wait for things to go wrong
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		d_running = false;
		
		// Rethrow exception (if any)
		if (d_thrown != null) {
			throw d_thrown;
		}
	}
}
