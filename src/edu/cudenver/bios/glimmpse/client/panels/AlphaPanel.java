package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class AlphaPanel extends Composite
implements DynamicListValidator
{
    // dynamic table of alpha values
    protected DynamicListPanel alphaListPanel = 
    	new DynamicListPanel(Glimmpse.constants.alphaTableTitle(), this);
    
    protected ArrayList<StepStatusListener> stepStatusListeners = new ArrayList<StepStatusListener>();
    protected String name;
    
    public AlphaPanel(String name)
    {
    	this.name = name;
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.alphaTitle());
        HTML description = new HTML(Glimmpse.constants.alphaDescription());

        // layout the panels
        panel.add(header);
        panel.add(description);
        panel.add(alphaListPanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        // initialize the panel
        initWidget(panel);
    }
    
    /**
     * Return an XML representation of the alpha list
     * @return XML representation of the alpha list
     */
    public String toXML()
    {
    	return alphaListPanel.toXML("alphaList");
    }
    
    public void addStepStatusListener(StepStatusListener listener)
    {
    	stepStatusListeners.add(listener);
    }
    
    public void validate(String value) throws IllegalArgumentException
    {
    	try
    	{
    		TextValidation.parseDouble(value, 0, 1);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
    	}
    }
    
    public void onValidRowCount(int validRowCount)
    {
    	if (validRowCount > 0)
    		for(StepStatusListener listener: stepStatusListeners) listener.onStepComplete(name);
    	else
    		for(StepStatusListener listener: stepStatusListeners) listener.onStepInProgress(name);
    }
}
