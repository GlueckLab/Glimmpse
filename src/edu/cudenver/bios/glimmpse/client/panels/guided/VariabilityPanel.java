package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.LearCovariancePanel;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class VariabilityPanel extends WizardStepPanel
{
    public VariabilityPanel()
    {
    	super(Glimmpse.constants.stepsLeftVariability());
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        LearCovariancePanel chart = new LearCovariancePanel();
        
        panel.add(chart);
        
        initWidget(panel);
        
    }
    
    public void reset()
    {
    	
    }

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
}
