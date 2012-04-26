package org.drugis.common.gui.task;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.drugis.common.threading.IterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.SimpleSuspendableTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.ForkTransition;
import org.drugis.common.threading.activity.JoinTransition;
import org.drugis.common.threading.activity.Transition;

public class TaskProgressBarExample {
	
	static class LongComputation implements IterativeComputation {
		private final int d_max;
		private int d_step;
		public LongComputation(int max) {
			d_max = max;
			d_step = 0;
		}
		public void initialize() {}
		public void step() { 
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			} 
			++d_step;
		}
		public void finish() {}
		public int getIteration() { return d_step; }
		public int getTotalIterations() { return d_max; }
	}
	
	public static void main(String[] args) {
		Task start = new SimpleSuspendableTask(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
				}
			}
		}, "start");
		IterativeTask middle1 = new IterativeTask(new LongComputation(1500), "fork 1");
		middle1.setReportingInterval(25);
		IterativeTask middle2 = new IterativeTask(new LongComputation(750), "fork 2");
		middle2.setReportingInterval(25);
		IterativeTask middle3 = new IterativeTask(new LongComputation(2000), "fork 3");
		middle3.setReportingInterval(25);
		List<Task> middle = Arrays.asList(new Task[] { middle1, middle2, middle3 } );
		IterativeTask end = new IterativeTask(new LongComputation(500), "end");
		end.setReportingInterval(50);

		List<Transition> trans = new ArrayList<Transition>();
		trans.add(new ForkTransition(start, middle));
		trans.add(new JoinTransition(middle, end));

		ActivityModel actModel = new ActivityModel(start, end, trans);
		final ActivityTask test = new ActivityTask(actModel, "Testing");
		TaskProgressBar taskProgressBar = new TaskProgressBar(test);
		taskProgressBar.setPreferredSize(new Dimension(350, 20));
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jPanel = new JPanel();
		jPanel.add(taskProgressBar);
		JButton jButton = new JButton("Start Task");
		jButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ThreadHandler.getInstance().scheduleTask(test);
			}
		
		});
		jPanel.add(jButton);
		frame.add(jPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
