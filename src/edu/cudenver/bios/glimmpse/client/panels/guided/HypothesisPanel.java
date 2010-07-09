package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class HypothesisPanel extends WizardStepPanel
{   
    public HypothesisPanel()
    {
    	super(Glimmpse.constants.stepsLeftHypotheses());
        VerticalPanel panel = new VerticalPanel();
        
        initWidget(panel);
    }
    
    public void reset() {}
}
