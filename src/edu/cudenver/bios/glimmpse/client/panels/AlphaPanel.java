package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class AlphaPanel extends WizardStepPanel
implements DynamicListValidator
{
    // dynamic table of alpha values
    protected DynamicListPanel alphaListPanel = 
    	new DynamicListPanel(Glimmpse.constants.alphaTableTitle(), this);
    
    public AlphaPanel()
    {
    	super(Glimmpse.constants.stepsLeftAlpha());
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
    	complete = (validRowCount > 0);
    	if (complete)
    		for(StepStatusListener listener: stepStatusListeners) listener.onStepComplete();
    	else
    		for(StepStatusListener listener: stepStatusListeners) listener.onStepInProgress();
    }
    
    public void reset()
    {
    	
    }
}
