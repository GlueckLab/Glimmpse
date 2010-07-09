package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;

public class OptionsPanel extends WizardStepPanel
{
    public OptionsPanel()
    {
    	super(Glimmpse.constants.stepsLeftOptions());
        VerticalPanel panel = new VerticalPanel();
        
        initWidget(panel);
    }
    
    public void reset()
    {
    	
    }
}
