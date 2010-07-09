package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class ThetaPanel extends WizardStepPanel
{   
	public ThetaPanel()
	{
		super(Glimmpse.constants.stepsLeftTheta());

		VerticalPanel panel = new VerticalPanel();
		
		panel.add(new HTML("theta panel"));
		
		initWidget(panel);
	}
	
    public void reset()
    {
    	
    }
}
