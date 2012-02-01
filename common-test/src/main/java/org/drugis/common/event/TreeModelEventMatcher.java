package org.drugis.common.event;

import static org.easymock.EasyMock.reportMatcher;

import java.util.Arrays;

import javax.swing.event.TreeModelEvent;

import org.easymock.IArgumentMatcher;

public class TreeModelEventMatcher implements IArgumentMatcher {
	private TreeModelEvent d_expected;
	
	public TreeModelEventMatcher(TreeModelEvent expected) {
		d_expected = expected;
	}

	public void appendTo(StringBuffer buffer) {
		buffer.append("TreeModelEventMatcher(");
        buffer.append("source = " + d_expected.getSource() + ", ");
        buffer.append("path = " + d_expected.getTreePath() + ", ");
        buffer.append("childIndices = " + Arrays.toString(d_expected.getChildIndices()) + ", ");
        buffer.append("children = " + Arrays.toString(d_expected.getChildren()) + ")");
	}

	public boolean matches(Object argument) {
		if (argument instanceof TreeModelEvent) {
			TreeModelEvent event = (TreeModelEvent) argument;
			return d_expected.getSource() == event.getSource() &&
				d_expected.getTreePath().equals(event.getTreePath()) &&
				Arrays.equals(d_expected.getChildIndices(), event.getChildIndices()) &&
				Arrays.equals(d_expected.getChildren(), event.getChildren());
		}
		return false;
	}

    public static TreeModelEvent eqTreeModelEvent(TreeModelEvent in) {
        reportMatcher(new TreeModelEventMatcher(in));
        return null;
    }
}
