package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class DesignPanel extends WizardStepPanel
{    
	public DesignPanel()
	{
		super(Glimmpse.constants.stepsLeftDesign());
		VerticalPanel panel = new VerticalPanel();
		
		panel.add(new HTML("design panel"));
		
		initWidget(panel);
	}
	
	public void reset()
	{
		
	}
}
