/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.common.gui.table;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


@SuppressWarnings("serial")
public class EnhancedTable extends JTable {
	
	public static EnhancedTable createWithSorter(TableModel model) {
		EnhancedTable enhancedTable = createBare(model);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		enhancedTable.setRowSorter(sorter);
		return enhancedTable;
	}
	
	/**
	 * Create a "bare" enhancedTable. You need to call autoSizeColumns() yourself.
	 * @param model The table model.
	 * @return A fully initialized EnhancedTable.
	 */
	public static EnhancedTable createBare(TableModel model) {
		return new EnhancedTable(model);
	}

	private EnhancedTableHeader d_tableHeader;

	private EnhancedTable(TableModel model) {
		super(model);
		d_tableHeader = new TooltipTableHeader(model, getColumnModel(), this);
		setTableHeader(d_tableHeader);
		setBackground(Color.WHITE);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableCopyHandler.registerCopyAction(this);
	}

	public void autoSizeColumns() {
		if (d_tableHeader != null) {
			d_tableHeader.autoSizeColumns();
		}
		setPreferredScrollableViewportSize(getPreferredSize());
	}
	
	public EnhancedTable(TableModel model, int maxColWidth) {
		this(model);
		d_tableHeader.setMaxColWidth(maxColWidth);
		autoSizeColumns();
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		autoSizeColumns();
	}
	
	@Override
	public EnhancedTableHeader getTableHeader() {
		return d_tableHeader;
	}
	
}
