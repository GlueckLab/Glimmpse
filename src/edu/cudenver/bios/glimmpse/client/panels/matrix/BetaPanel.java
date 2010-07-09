package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class BetaPanel extends WizardStepPanel
{
	public BetaPanel()
	{
		super(Glimmpse.constants.stepsLeftBeta());
		VerticalPanel panel = new VerticalPanel();
		
		panel.add(new HTML("beta panel"));
		
		initWidget(panel);
	}
	
    public void reset()
    {
    }
}
