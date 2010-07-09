package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class EffectSizePanel extends WizardStepPanel
{
    EffectSizeBarChart chart = new EffectSizeBarChart();
    
    public EffectSizePanel()
    {
    	super(Glimmpse.constants.stepsLeftEffectSize());
        VerticalPanel panel = new VerticalPanel();
        
        panel.add(chart);
        
        initWidget(panel);
    }
    
    public void reset()
    {
    	
    }
}
