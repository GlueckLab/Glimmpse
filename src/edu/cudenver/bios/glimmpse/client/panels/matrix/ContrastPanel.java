package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class ContrastPanel extends WizardStepPanel
{    
	public ContrastPanel()
	{
		super(Glimmpse.constants.stepsLeftContrast());
		VerticalPanel panel = new VerticalPanel();
		
		panel.add(new HTML("contrast panel"));
		
		initWidget(panel);
	}

	public void reset()
	{
		
	}
}
