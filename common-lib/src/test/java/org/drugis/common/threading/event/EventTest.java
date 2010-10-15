package org.drugis.common.threading.event;

import static org.drugis.common.JUnitUtil.assertNotEquals;
import static org.junit.Assert.*;

import org.drugis.common.threading.Task;
import org.junit.Before;
import org.junit.Test;

public class EventTest {
	private Task d_task1;
	private Task d_task2;
	private Task d_task3;
	@Before
	public void setUp() {
		d_task1 = Util.buildNullTask();
		d_task2 = Util.buildNullTask();
		d_task3 = Util.buildNullTask();
	}

	@Test
	public void startEventEqOnSource() {
		assertEquals(new TaskStartedEvent(d_task1), new TaskStartedEvent(d_task1));
		assertEquals(new TaskStartedEvent(d_task1).hashCode(), new TaskStartedEvent(d_task1).hashCode());
		assertNotEquals(new TaskStartedEvent(d_task1), new TaskStartedEvent(d_task2));
	}
	
	@Test
	public void finishedEventEqOnSource() {
		assertEquals(new TaskFinishedEvent(d_task1), new TaskFinishedEvent(d_task1));
		assertEquals(new TaskFinishedEvent(d_task1).hashCode(), new TaskFinishedEvent(d_task1).hashCode());
		assertNotEquals(new TaskFinishedEvent(d_task1), new TaskFinishedEvent(d_task2));
	}
	
	@Test
	public void abortedEventEqOnSource() {
		assertEquals(new TaskAbortedEvent(d_task1), new TaskAbortedEvent(d_task1));
		assertEquals(new TaskAbortedEvent(d_task1).hashCode(), new TaskAbortedEvent(d_task1).hashCode());
		assertNotEquals(new TaskAbortedEvent(d_task1), new TaskAbortedEvent(d_task2));
	}
	
	@Test
	public void progressEventEqOnMembers() {
		assertEquals(new TaskProgressEvent(d_task1, 100, 10000), new TaskProgressEvent(d_task1, 100, 10000));
		assertEquals(new TaskProgressEvent(d_task1, 100, 10000).hashCode(), new TaskProgressEvent(d_task1, 100, 10000).hashCode());
		assertNotEquals(new TaskProgressEvent(d_task1, 100, 10000), new TaskProgressEvent(d_task2, 100, 10000));
		assertNotEquals(new TaskProgressEvent(d_task1, 100, 10000), new TaskProgressEvent(d_task1, 200, 10000));
		assertNotEquals(new TaskProgressEvent(d_task1, 100, 10000), new TaskProgressEvent(d_task1, 100, 20000));
	}
	
	@Test
	public void failedEventEqOnSourceAndCause() {
		Exception cause = new Exception("FAIL");
		assertEquals(new TaskFailedEvent(d_task1, cause), new TaskFailedEvent(d_task1, cause));
		assertEquals(new TaskFailedEvent(d_task1, cause).hashCode(), new TaskFailedEvent(d_task1, cause).hashCode());
		assertNotEquals(new TaskFailedEvent(d_task1, cause), new TaskFailedEvent(d_task2, cause));
		assertNotEquals(new TaskFailedEvent(d_task1, cause), new TaskFailedEvent(d_task1, new Exception("FAIL")));
	}
	
	@Test
	public void phaseStartedEventEqOnSourceAndPhase() {
		assertEquals(new PhaseStartedEvent(d_task1, d_task2), new PhaseStartedEvent(d_task1, d_task2));
		assertEquals(new PhaseStartedEvent(d_task1, d_task2).hashCode(), new PhaseStartedEvent(d_task1, d_task2).hashCode());
		assertNotEquals(new PhaseStartedEvent(d_task1, d_task2), new PhaseStartedEvent(d_task1, d_task3));
		assertNotEquals(new PhaseStartedEvent(d_task1, d_task2), new PhaseStartedEvent(d_task3, d_task2));
	}
	
	@Test
	public void phaseFinishedEventEqOnSourceAndPhase() {
		assertEquals(new PhaseFinishedEvent(d_task1, d_task2), new PhaseFinishedEvent(d_task1, d_task2));
		assertEquals(new PhaseFinishedEvent(d_task1, d_task2).hashCode(), new PhaseFinishedEvent(d_task1, d_task2).hashCode());
		assertNotEquals(new PhaseFinishedEvent(d_task1, d_task2), new PhaseFinishedEvent(d_task1, d_task3));
		assertNotEquals(new PhaseFinishedEvent(d_task1, d_task2), new PhaseFinishedEvent(d_task3, d_task2));
	}
	
	@Test
	public void startEventNeqOnType() {
		assertNotEquals(new TaskStartedEvent(d_task1), new TaskFinishedEvent(d_task1));
		assertNotEquals(new TaskFinishedEvent(d_task1), new TaskStartedEvent(d_task1));
		assertNotEquals(new TaskStartedEvent(d_task1), new TaskAbortedEvent(d_task1));
		assertNotEquals(new TaskAbortedEvent(d_task1), new TaskStartedEvent(d_task1));
		assertNotEquals(new TaskStartedEvent(d_task1), new TaskProgressEvent(d_task1, 100, 10000));
		assertNotEquals(new TaskProgressEvent(d_task1, 100, 10000), new TaskStartedEvent(d_task1));
		Exception cause = new Exception("FAIL");
		assertNotEquals(new TaskStartedEvent(d_task1), new TaskFailedEvent(d_task1, cause));
		assertNotEquals(new TaskFailedEvent(d_task1, cause), new TaskStartedEvent(d_task1));
		assertNotEquals(new PhaseStartedEvent(d_task1, d_task2), new TaskStartedEvent(d_task1));
		assertNotEquals(new TaskStartedEvent(d_task1), new PhaseStartedEvent(d_task1, d_task2));
		assertNotEquals(new PhaseFinishedEvent(d_task1, d_task2), new TaskStartedEvent(d_task1));
		assertNotEquals(new TaskStartedEvent(d_task1), new PhaseFinishedEvent(d_task1, d_task2));	
	}
}
