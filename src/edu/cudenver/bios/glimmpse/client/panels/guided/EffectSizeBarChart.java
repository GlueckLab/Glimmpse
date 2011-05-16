/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.formatters.ColorFormat;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.BarChart.Options;

/**
 * Prototype for patterns of mean differences - unused at present
 */
public class EffectSizeBarChart extends Composite
{
	DataTable data = DataTable.create();
	Options options;
	BarChart chart;
	int selectedRow = -1;
	int selectedColumn = -1;
	public EffectSizeBarChart()
	{
		VerticalPanel panel = new VerticalPanel();

		chart = new BarChart(createTable(), createOptions());
		chart.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				// May be multiple selections.
				JsArray<Selection> selections = chart.getSelections();
				for (int i = 0; i < selections.length(); i++)
				{
					Selection selection = selections.get(i);
					if (selection.isCell()) 
					{
						// get cell row/column
						selectedRow = selection.getRow();
						selectedColumn = selection.getColumn();	
					}
				}

			}
		});
		panel.add(chart);

		
		Grid grid = new Grid(3,4);
		
		final ListBox list1 = new ListBox();
		final ListBox list2 = new ListBox();
		final ListBox list3 = new ListBox();
		final ListBox list4 = new ListBox();
		for(int j = 0; j < 10; j++) 
		{
			list1.addItem(Integer.toString(j));
			list2.addItem(Integer.toString(j));
			list3.addItem(Integer.toString(j));
			list4.addItem(Integer.toString(j));
		}
		list1.setSelectedIndex(1);
		list2.setSelectedIndex(2);
		list3.setSelectedIndex(2);
		list4.setSelectedIndex(1);
		grid.setWidget(0, 0, new HTML("male"));
		grid.setWidget(0, 1, new HTML("male"));
		grid.setWidget(0, 2, new HTML("female"));
		grid.setWidget(0, 3, new HTML("female"));
		grid.setWidget(1, 0, new HTML("A"));
		grid.setWidget(1, 1, new HTML("B"));
		grid.setWidget(1, 2, new HTML("A"));
		grid.setWidget(1, 3, new HTML("B"));
		grid.setWidget(2, 0, list1);
		grid.setWidget(2, 1, list2);
		grid.setWidget(2, 2, list3);
		grid.setWidget(2, 3, list4);
		panel.add(grid);
		list1.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				ListBox lb = (ListBox) e.getSource();
				data.setValue(0, 1, Integer.parseInt(lb.getItemText(lb.getSelectedIndex())));
				chart.draw(data,options);
			}
		});
		list2.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				ListBox lb = (ListBox) e.getSource();
				data.setValue(1, 1, Integer.parseInt(lb.getItemText(lb.getSelectedIndex())));
				chart.draw(data,options);
			}
		});
		list3.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				ListBox lb = (ListBox) e.getSource();
				data.setValue(2, 1, Integer.parseInt(lb.getItemText(lb.getSelectedIndex())));
				chart.draw(data,options);
			}
		});
		list4.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				ListBox lb = (ListBox) e.getSource();
				data.setValue(3, 1, Integer.parseInt(lb.getItemText(lb.getSelectedIndex())));
				chart.draw(data,options);
			}
		});
		
		ListBox common = new ListBox();
		common.addItem("Custom Relative Differences");
		common.addItem("Gender Main Effect");
		common.addItem("Treatment Main Effect");
		common.addItem("Gender by Treatment Interaction");
		common.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				ListBox lb = (ListBox) e.getSource();
				String value = lb.getItemText(lb.getSelectedIndex());
				if (value.equals("Gender Main Effect"))
				{
					data.setValue(0, 1, 1);
					data.setValue(1, 1, 1);
					data.setValue(2, 1, 2);
					data.setValue(3, 1, 2);
					list1.setSelectedIndex(1);
					list2.setSelectedIndex(1);
					list3.setSelectedIndex(2);
					list4.setSelectedIndex(2);
					chart.draw(data, options);
				} 
				else if (value.equals("Treatment Main Effect"))
				{
					data.setValue(0, 1, 2);
					data.setValue(1, 1, 1);
					data.setValue(2, 1, 2);
					data.setValue(3, 1, 1);
					list1.setSelectedIndex(2);
					list2.setSelectedIndex(1);
					list3.setSelectedIndex(2);
					list4.setSelectedIndex(1);
					chart.draw(data, options);
				}
				else
				{
					// interaction
					data.setValue(0, 1, 1);
					data.setValue(1, 1, 2);
					data.setValue(2, 1, 2);
					data.setValue(3, 1, 1);
					list1.setSelectedIndex(1);
					list2.setSelectedIndex(2);
					list3.setSelectedIndex(2);
					list4.setSelectedIndex(1);
					chart.draw(data, options);
				}
			}
		});
		panel.add(new HTML("Common patterns of differences:"));
		panel.add(common);
		initWidget(panel);
	}

	private AbstractDataTable createTable() 
	{
		//DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Label");
		data.addColumn(ColumnType.NUMBER, "Difference");
		data.addRows(4);
		data.setValue(0, 0, "M,Aggggggggggggggggggggggggggggggggg");
		data.setValue(0, 1, 1);
		data.setValue(1, 0, "M,B");
		data.setValue(1, 1, 2);
		data.setValue(2, 0, "F,A");
		data.setValue(2, 1, 2);
		data.setValue(3, 0, "F,B");
		data.setValue(3, 1, 1);
		
		ColorFormat format = ColorFormat.create();
		 format.addRange(-1,2000, "white","yellow");
		 format.format(data,1); 

		return data;
	}

	private Options createOptions() {
	    options = Options.create();
	    options.setWidth(400);
	    options.setHeight(240);
	    //options.set3D(true);
	    options.setTitle("Estimated Relative Clinical Differences");
	    options.setShowCategories(true);
	    return options;
	  }
	
	
	
}
