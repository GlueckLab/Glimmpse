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
