package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EffectSizePanel extends Composite
{
    EffectSizeBarChart chart = new EffectSizeBarChart();
    
    public EffectSizePanel()
    {
        VerticalPanel panel = new VerticalPanel();
        
        panel.add(chart);
        
        initWidget(panel);
    }
}
