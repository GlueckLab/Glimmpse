package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class StepsLeftPanel extends Composite 
implements NavigationListener, StepStatusListener
{
	protected static final String STYLE = "stepsLeftLabel";
	protected static final String STYLE_PANEL = "stepsLeftPanel";
    protected static final String STYLE_COMPLETE = "complete";
    protected static final String STYLE_IN_PROGRESS = "inprogress";
    VerticalPanel panel = new VerticalPanel();

    protected ArrayList<HTML> stepList = new ArrayList<HTML>();

    private int currentStep = 0;
    
    protected ArrayList<NavigationListener> navigationListeners = new ArrayList<NavigationListener>();
    
    public StepsLeftPanel(WizardStepPanel[] stepPanels)
    {               
        for(WizardStepPanel step: stepPanels) addStep(step.getName());
        
        // select the first step
        Widget step = stepList.get(0);
        step.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
        step.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        
        // add style
        panel.setStyleName(STYLE_PANEL);

        initWidget(panel);
    }
    
    public StepsLeftPanel(String[] stepNames)
    {               
        for(String step: stepNames) addStep(step);
        
        // select the first step
        Widget step = stepList.get(0);
        step.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
        step.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        
        // add style
        panel.setStyleName(STYLE_PANEL);

        initWidget(panel);
    }
    
    private void addStep(String stepLabel)
    {
        HTML stepHTML = new HTML(stepLabel);
        stepHTML.setStyleName(STYLE);
        stepHTML.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
        stepHTML.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                HTML source = (HTML) e.getSource();
                HTML newStep = null;
                int stepIdx = 0;
                for(HTML step: stepList)
                {
                    if (step == source)
                    {
                        newStep = step;
                        break;
                    }
                    stepIdx++;
                }
                    
                if (newStep != null) 
                {
                    updateStep(stepList.get(currentStep), newStep);
                    currentStep = stepIdx;
                    for(NavigationListener listener: navigationListeners) listener.onStep(currentStep);
                }
            }
        });
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
    protected void updateStep(Widget oldStep,Widget newStep)
    {       

        // deselect the old widgets
        oldStep.removeStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SELECTED);
        oldStep.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_DESELECTED);
        
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
            updateStep(stepList.get(currentStep), stepList.get(++currentStep));
        //wizard.showWidget(currentStep);
    }
    
    /**
     * Call back when "previous" navigation button is clicked
     * Does nothing if already at beginning of step list
     */
    public void onPrevious()
    {
        if (currentStep > 0) 
            updateStep(stepList.get(currentStep), stepList.get(--currentStep));
        //wizard.showWidget(currentStep);
    }
    
    /**
     * Return to first step
     */
    public void onCancel()
    {
        if (currentStep > 0)
        {
            updateStep(stepList.get(currentStep), stepList.get(0));
            currentStep = 0;
            //wizard.showWidget(currentStep);
        }

    }
    
    public void onStep(int stepIndex)
    {
    	
    }
    
    public void onStepComplete()
    {
//    	if (stepName != null && !stepName.isEmpty())
//    	{
//    		for(HTML step: stepList)
//    		{
//    			if (stepName.equals(step.getHTML()))
//    			{
//    				// TODO
////    				step.removeStyleDependentName(STYLE_IN_PROGRESS);
////    				step.addStyleDependentName(STYLE_COMPLETE);
//    				break;
//    			}
//    		}
//    	}
    }
    
    public void onStepInProgress()
    {
//    	if (stepName != null && !stepName.isEmpty())
//    	{
//    		for(HTML step: stepList)
//    		{
//    			if (stepName.equals(step.getHTML()))
//    			{
//    				
//    			}
//    		}
//    	}
    }
    
    public void addNavigationListener(NavigationListener listener)
    {
    	navigationListeners.add(listener);
    }
    
}
