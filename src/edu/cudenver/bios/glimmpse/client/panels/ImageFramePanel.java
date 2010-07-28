package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageFramePanel extends Composite
{
    protected NamedFrame frame;
    
    public ImageFramePanel(String name)
    {
        VerticalPanel panel = new VerticalPanel();
        
        frame = new NamedFrame(name);
        panel.add(frame);
        
        initWidget(panel);
    }

    public NamedFrame getFrame()
    {
        return frame;
    }
    
    
}
