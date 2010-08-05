package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.BarChart.Options;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class EffectSizePanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener
{
	// data table holding the relative sizes of the groups
	DataTable data = DataTable.create();
	Options options;
	BarChart chart;
	
    public EffectSizePanel()
    {
    	super(Glimmpse.constants.stepsLeftEffectSize());
        VerticalPanel panel = new VerticalPanel();
        
        new BarChart(data, options);
        panel.add(chart);
        
        initWidget(panel);
    }
    
    public void reset()
    {
    	data.removeRows(0, data.getNumberOfRows());
    }

    @Override
    public void onEnter()
    {
    	
    }
    
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRepeatedMeasures(List<String> repeatedMeasures)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap)
	{
		// TODO Auto-generated method stub
		
	}
}
