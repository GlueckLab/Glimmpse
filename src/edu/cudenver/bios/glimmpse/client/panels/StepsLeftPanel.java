package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

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
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;

public class StepsLeftPanel extends Composite implements NavigationListener
{
	protected static final String STYLE = "stepsLeftLabel";
	protected static final String PANEL_STYLE = "stepsLeftPanel";
	protected static final String SELECTED_STYLE = "selected";
    protected static final String DESELECTED_STYLE = "deselected";
    
    VerticalPanel panel = new VerticalPanel();

    protected ArrayList<HTML> steps = new ArrayList<HTML>();

    private int currentStep = 0;
    
    public StepsLeftPanel()
    {        
        addStep("Outcomes");
        addStep("Predictors");
        addStep("Groups");
        addStep("Hypotheses");
        addStep("Effect Size");
        addStep("Variability");
        addStep("Type I Error");
        addStep("Options");
        addStep("Results");
        
        // select the first step
        Widget step = steps.get(0);
        step.removeStyleDependentName(DESELECTED_STYLE);
        step.addStyleDependentName(SELECTED_STYLE);
        
        // add style
        panel.setStyleName(PANEL_STYLE);

        initWidget(panel);
    }
    
    private void addStep(String stepLabel)
    {
        HTML stepHTML = new HTML(stepLabel);
        stepHTML.setStyleName(STYLE);
        stepHTML.addStyleDependentName(DESELECTED_STYLE);
        stepHTML.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                HTML source = (HTML) e.getSource();
                HTML newStep = null;
                int stepIdx = 0;
                for(HTML step: steps)
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
                    updateStep(steps.get(currentStep), newStep);
                    currentStep = stepIdx;
                }
            }
        });
        steps.add(stepHTML);
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
        oldStep.removeStyleDependentName(SELECTED_STYLE);
        oldStep.addStyleDependentName(DESELECTED_STYLE);
        
        // select the new widgets
        newStep.removeStyleDependentName(DESELECTED_STYLE);
        newStep.addStyleDependentName(SELECTED_STYLE);
    }
    
    /**
     * Call back when "next" navigation button is clicked
     * Does nothing if already at end of step list
     */
    public void onNext()
    {
        if (currentStep < steps.size()-1)
            updateStep(steps.get(currentStep), steps.get(++currentStep));
    }
    
    /**
     * Call back when "previous" navigation button is clicked
     * Does nothing if already at beginning of step list
     */
    public void onPrevious()
    {
        if (currentStep > 0) 
            updateStep(steps.get(currentStep), steps.get(--currentStep));
    }
    
    /**
     * Return to first step
     */
    public void onCancel()
    {
        if (currentStep > 0)
        {
            updateStep(steps.get(currentStep), steps.get(0));
            currentStep = 0;
        }

    }
    
}
