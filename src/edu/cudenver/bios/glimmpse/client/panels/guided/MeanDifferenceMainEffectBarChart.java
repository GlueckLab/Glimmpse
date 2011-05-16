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

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ColumnChart;
import com.google.gwt.visualization.client.visualizations.ColumnChart.Options;

/**
 * Prototype for graphical representaiton of mean differences - unused
 */
public class MeanDifferenceMainEffectBarChart extends Composite
{
	protected DataTable data = DataTable.create();
	protected Options options;
	protected ColumnChart chart;
	protected int width = 0;
	protected int height = 0;

	public MeanDifferenceMainEffectBarChart(List<Integer> columns, int width, int height)
	{
		this.width = width;
		this.height = height;
		VerticalPanel panel = new VerticalPanel();
		chart = new ColumnChart(createTable(columns), createOptions());
		panel.add(chart);
		initWidget(panel);
	}

	private AbstractDataTable createTable(List<Integer> columns) 
	{
		data.addColumn(ColumnType.STRING, "Label");
		data.addColumn(ColumnType.NUMBER);

		data.addRows(columns.size());
		int i = 0;
		for(Integer value: columns)
		{
			data.setValue(i,0,"label");
			data.setValue(i,1, value);
			i++;
		}
		
//		ColorFormat format = ColorFormat.create();
//		 format.addRange(-1,2000, "white","yellow");
//		 format.format(data,1); 

		return data;
	}

	private Options createOptions() {
	    options = Options.create();
	    options.setWidth(width);
	    options.setHeight(height);
	    options.setShowCategories(false);
	    options.setLegend(LegendPosition.NONE);
	    return options;
	  }
	
}
