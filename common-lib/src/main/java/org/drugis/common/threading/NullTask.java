package org.drugis.common.threading;

public class NullTask extends SimpleSuspendableTask {
	private String d_str = "NullTask";

	public NullTask() {
		super(new Runnable() { public void run() {} });
	}
	
	public NullTask(String str) {
		this();
		d_str = str;
	}

	@Override
	public String toString() {
		return d_str;
	}
}
