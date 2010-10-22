package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;

public class StepsLeftPanel extends Composite 
implements NavigationListener
{
	protected static final String STYLE_LABEL = "stepsLeftLabel";
	protected static final String STYLE_PANEL = "stepsLeftPanel";
    protected static final String STYLE_COMPLETE = "complete";
    VerticalPanel panel = new VerticalPanel();

    protected ArrayList<HTML> stepList = new ArrayList<HTML>();
    protected String[] stepNames;
    private int currentStep = 0;
        
    public StepsLeftPanel(String[] stepNames)
    {            
    	// add the steps to the display, selecting the first one
    	int count = 0;
        for(String step: stepNames) 
        {
        	if (count == 0)
        		addStep(step, GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        	else
        		addStep(step, GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
        	count++;
        }
        
        // add style
        panel.setStyleName(STYLE_PANEL);

        initWidget(panel);
    }
    
    private void addStep(String stepLabel, String dependentStyle)
    {
        HTML stepHTML = new HTML(stepLabel);
        stepHTML.setStyleName(STYLE_LABEL);
        stepHTML.addStyleDependentName(dependentStyle);
        stepList.add(stepHTML);
        panel.add(stepHTML);
    }
    
    /**
     * Highlight the current step in the user navigation
     * 
     * Select the "step" at the new index, and unselect the step
     * at the old index by updating the dependent style sheet names
     * 
     * @param newIndex
     * @param prevIndex
     */
    protected void updateStep(Widget oldStep, String styleNameForOldStep, Widget newStep)
    {       

        // deselect the old widgets
        oldStep.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        oldStep.addStyleDependentName(styleNameForOldStep);
        
        // select the new widgets
        newStep.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
        newStep.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
    }
    
    /**
     * Call back when "next" navigation button is clicked
     * Does nothing if already at end of step list
     */
    public void onNext()
    {
        if (currentStep < stepList.size()-1)
        {
        	Widget oldStep = stepList.get(currentStep);
        	Widget newStep = stepList.get(++currentStep);
            // deselect the old widgets
            oldStep.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
            oldStep.addStyleDependentName(STYLE_COMPLETE);
            // select the new widgets
            newStep.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
            newStep.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        }
    }
    
    /**
     * Call back when "next" navigation button is clicked
     * Does nothing if already at end of step list
     */
    public void onPrevious()
    {
        if (currentStep > 0)
        {
        	Widget oldStep = stepList.get(currentStep);
        	Widget newStep = stepList.get(--currentStep);
            // deselect the old widgets
            oldStep.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
            oldStep.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
            // select the new widgets
            newStep.removeStyleDependentName(STYLE_COMPLETE);
            newStep.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        }
    }
    
    public void reset()
    {
        if (currentStep > 0)
        {
        	Widget oldStep = stepList.get(currentStep);
        	currentStep = 0;
        	Widget newStep = stepList.get(currentStep);
            // deselect the old widgets
            oldStep.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
            oldStep.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
            // select the new widgets
            newStep.removeStyleDependentName(STYLE_COMPLETE);
            newStep.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        }
    }
}
