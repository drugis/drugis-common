/**
 * 
 */
package org.drugis.common.gui;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SingleColumnPanelBuilder {
	FormLayout d_layout = new FormLayout("fill:0:grow", "p");
	PanelBuilder d_builder = new PanelBuilder(d_layout);
	CellConstraints d_cc = new CellConstraints();
	int d_row = 0;

	public SingleColumnPanelBuilder() {
		d_builder.setDefaultDialogBorder();
	}
	
	public void add(JComponent comp) {
		nextRow();
		d_builder.add(comp, d_cc.xy(1, d_row));
	}

	private void nextRow() {
		d_row = d_row < 1 ? 1 : LayoutUtil.addRow(d_layout, d_row);
	}
	
	public void addSeparator(String label) {
		nextRow();
		d_builder.addSeparator(label, d_cc.xy(1, d_row));
	}
	
	public JPanel getPanel() {
		return d_builder.getPanel();
	}
}