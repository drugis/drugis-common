package org.drugis.common.threading;

public class NullTask extends SimpleSuspendableTask {
	public NullTask() {
		super(new Runnable() { public void run() {} });
	}
	
	@Override
	public String toString() {
		return "NullTask";
	}
}
