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
package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;

/**
 * Prototype for Lear covariance model - not currently used
 *
 */
public class LearCovariancePanel extends Composite implements ChangeHandler
{
	DataTable data = DataTable.create();
	Options options= Options.create();
	LineChart chart;
	
	ListBox deltaListBox = new ListBox();
	TextBox rhoTextBox = new TextBox();
	
	public LearCovariancePanel()
	{
		VerticalPanel panel = new VerticalPanel();

		chart = new LineChart(createTable(), createOptions());
		
		final ListBox list = new ListBox();

		for(int j = 0; j <= 10; j++) 
		{
			list.addItem(Integer.toString(j));
		}
		list.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				ListBox lb = (ListBox) e.getSource();
				data.setValue(0, 1, Integer.parseInt(lb.getItemText(lb.getSelectedIndex())));
				chart.draw(data,options);
			}
		});

		// add callbacks to adjust the decay rate
		rhoTextBox.addChangeHandler(this);
		deltaListBox.addChangeHandler(this);
		for(int i = 0; i <= 10; i++) deltaListBox.addItem(Integer.toString(i));
		// layout the parameter inputs
		Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML("Correlation: "));
		grid.setWidget(0, 1, rhoTextBox);
		grid.setWidget(1, 0, new HTML("Delta: "));
		grid.setWidget(1, 1, deltaListBox);
		
		// layout panel
		panel.add(chart);
		panel.add(grid);

		initWidget(panel);
	}

	public void onChange(ChangeEvent e)
	{
		try
		{
			double rho = Double.parseDouble(rhoTextBox.getText());
			if (rho < 0 || rho > 1) throw new NumberFormatException();
			double delta = Double.parseDouble(deltaListBox.getItemText(deltaListBox.getSelectedIndex()));

			data.setValue(0, 1, learCorrelation(rho, delta, 1));
			data.setValue(1, 1, learCorrelation(rho, delta, 2));
			data.setValue(2, 1, learCorrelation(rho, delta, 3));
			data.setValue(3, 1, learCorrelation(rho, delta, 4));
			data.setValue(4, 1, learCorrelation(rho, delta, 5));
			chart.draw(data, options);
		}
		catch (NumberFormatException nfe)
		{
			rhoTextBox.setText("1");
		}
	}
	
	private double learCorrelation(double rho, double delta, double distance)
	{
		double dmin = 1;
		double dmax = 2;
		
		double exp = dmin + delta * ((distance - dmin)/(dmax - dmin));
		return Math.pow(rho, exp);
		
	}
	
	private AbstractDataTable createTable() 
	{
		//DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Label");
		data.addColumn(ColumnType.NUMBER, "Correlation");
		data.addRows(5);
		data.setValue(0, 0, "1");
		data.setValue(0, 1, 1);
		data.setValue(1, 0, "2");
		data.setValue(1, 1, 1);
		data.setValue(2, 0, "3");
		data.setValue(2, 1, 1);
		data.setValue(3, 0, "4");
		data.setValue(3, 1, 1);
		data.setValue(4, 0, "5");
		data.setValue(4, 1, 1);
		
		return data;
	}

	private Options createOptions() {
	    options = Options.create();
	    options.setWidth(400);
	    options.setHeight(240);
	    //options.set3D(true);
	    options.setTitle("Estimated Covariance");
	    options.setShowCategories(true);
	    return options;
	  }
}
