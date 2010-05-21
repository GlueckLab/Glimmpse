package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VariabilityPanel extends Composite
{
    public VariabilityPanel()
    {
        VerticalPanel panel = new VerticalPanel();
        
        LearCovariancePanel chart = new LearCovariancePanel();
        
        panel.add(chart);
        
        initWidget(panel);
        
    }
}
