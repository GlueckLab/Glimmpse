package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class StatisticalTestPanel extends Composite
{
    protected ArrayList<StepStatusListener> stepStatusListeners = new ArrayList<StepStatusListener>();
    protected String name;
    
	public StatisticalTestPanel(String name)
	{
		this.name = name;
		VerticalPanel panel = new VerticalPanel();
		
		
		panel.add(new HTML("test panel"));
		
		initWidget(panel);
	}
	
	
	public String toXML()
	{
		return "";
	}
	
    public void addStepStatusListener(StepStatusListener listener)
    {
    	stepStatusListeners.add(listener);
    }
}
