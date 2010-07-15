package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class StudyGroupsPanel extends WizardStepPanel
implements OutcomesListener
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
}
