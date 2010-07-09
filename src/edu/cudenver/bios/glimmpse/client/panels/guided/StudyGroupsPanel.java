package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class StudyGroupsPanel extends WizardStepPanel
{
    public StudyGroupsPanel()
    {
    	super(Glimmpse.constants.stepsLeftGroups());
        VerticalPanel panel = new VerticalPanel();
        
        initWidget(panel);
    }
    
    public void reset()
    {
    	
    }
}
