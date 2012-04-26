package org.drugis.common.gui.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.drugis.common.threading.IterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.DirectTransition;
import org.drugis.common.threading.activity.Transition;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.Port;

@SuppressWarnings("serial")
public class ActivityTaskProgressGraph extends JGraph {
	private Map<Object, GraphCell> d_cells = new HashMap<Object, GraphCell>();
	
	public ActivityTaskProgressGraph(ActivityTask task) {
		super(new DefaultGraphModel());
		createCells(task);
		createEdges(task);
	}
	
	Port getDefaultPort(Object vertex, GraphModel model) {
		for (int i = 0; i < model.getChildCount(vertex); i++) { // Iterate over all Children
			Object child = model.getChild(vertex, i); // Fetch the Child of Vertex at Index i
			if (child instanceof Port) // Check if Child is a Port
				return (Port) child; // Return the Child as a Port
		} // No Ports Found
		return null;
	}


	private void createEdges(ActivityTask task) {
		for (Transition t : task.getModel().getTransitions()) {
			Task in = t.getSources().get(0);
			Task out = t.getTargets().get(0);
			DefaultEdge edge = new DefaultEdge(t);

			Port inPort = getDefaultPort(d_cells.get(in), getModel());
			Port outPort = getDefaultPort(d_cells.get(out), getModel());
			ConnectionSet cs = new ConnectionSet(edge, inPort, outPort);
			((DefaultGraphModel)getModel()).insert(new Object[] { edge }, null, cs, null, null);
		}
	}

	private void createCells(ActivityTask task) {
		ArrayList<GraphCell> cells = new ArrayList<GraphCell>();
		for (Task t : task.getModel().getStates()) {
			DefaultGraphCell cell = new DefaultGraphCell(t);
			DefaultPort port = new DefaultPort(t.toString());
			cell.add(port);
			cells.add(cell);
			d_cells.put(t, cell);
			AttributeMap map = new AttributeMap();
			((DefaultGraphModel)getModel()).insert(new Object[] { cell }, map, null, null, null);
		}
	}
	
	public static void main(String [] args) {
		JFrame frame = new JFrame("Testing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(new ActivityTaskProgressGraph(createTask()));
		frame.pack();
		frame.setVisible(true);
	}
	
	static class ShortComputation implements IterativeComputation {
		private final int d_max;
		private int d_step;
		public ShortComputation(int max) {
			d_max = max;
			d_step = 0;
		}
		public void initialize() {}
		public void step() { ++d_step; }
		public void finish() {}
		public int getIteration() { return d_step; }
		public int getTotalIterations() { return d_max; }
	}

	private static ActivityTask createTask() {
		IterativeTask start = new IterativeTask(new ShortComputation(10000));
		IterativeTask end = new IterativeTask(new ShortComputation(10000));
		start.setReportingInterval(10);
		ActivityModel model = new ActivityModel(start, end, Collections.singletonList(new DirectTransition(start, end)));
		return new ActivityTask(model);
	}
}
