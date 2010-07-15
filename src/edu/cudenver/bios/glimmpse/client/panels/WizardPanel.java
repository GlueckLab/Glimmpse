package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class WizardPanel extends Composite
implements NavigationListener, StepStatusListener
{
	// main panel
	HorizontalPanel panel = new HorizontalPanel();
	// left navigation / "steps left" panel
    protected StepsLeftPanel stepsLeftPanel;
	// index of currently visible step
    protected int currentStep = 0;    
    // deck panel containing all steps in the input wizard
	DeckPanel wizardDeck = new DeckPanel();
	// top toolbar (save, clear, start over)
	// navigation buttons
	NavigationPanel navPanel = new NavigationPanel();
	
	/**
	 * Create an empty matrix panel
	 */
	public WizardPanel(WizardStepPanel[] stepPanels)
	{		
		VerticalPanel contentPanel = new VerticalPanel();
		
		// initialize the steps left panel
		stepsLeftPanel = new StepsLeftPanel(stepPanels);
		
		// add the content panels to the deck
		for(WizardStepPanel stepPanel: stepPanels)
		{
			wizardDeck.add(stepPanel);
			stepPanel.addStepStatusListener(this);
		}
		
		// add navigation callbacks
		navPanel.addNavigationListener(this);
		navPanel.addNavigationListener(stepsLeftPanel);
		enterStep();
		
		// layout the wizard panel
		contentPanel.add(new ToolBar());
		contentPanel.add(wizardDeck);
		contentPanel.add(navPanel);
		panel.add(stepsLeftPanel);		
		panel.add(contentPanel);

		// set style
		// TODO: finish style
		//panel.setStyleName(LAYOUT_STYLE);		
		
		// set up listener relationships
		stepsLeftPanel.addNavigationListener(this);

		// initialize
		initWidget(panel);
	}
	
    /**
     * Call back when "next" navigation button is clicked
     * Does nothing if already at end of step list
     */
    public void onNext()
    {
    	if (currentStep < wizardDeck.getWidgetCount()-1)
    	{
    		exitStep();
    		currentStep++;
    		enterStep();
    	}
    }
    
    /**
     * Call back when "previous" navigation button is clicked
     * Does nothing if already at beginning of step list
     */
    public void onPrevious()
    {
    	if (currentStep > 0)
    	{
    		exitStep();
    		currentStep--;
    		enterStep();
    	}
    }
    
    /**
     * Return to first step
     */
    public void onCancel()
    {
    	// TODO: clear everything
    	exitStep();
    	stepsLeftPanel.onCancel();
    	currentStep = 0;
    	enterStep();
    }
	
    /**
     * Navigate to a specific step
     */
    public void onStep(int stepIndex)
    {
    	if (stepIndex >= 0 && stepIndex < wizardDeck.getWidgetCount())
    	{
    		exitStep();
    		currentStep = stepIndex;
    		enterStep();
    	}
    }
    
    /**
     * Allow forward navigation when user input to the current step is complete.
     */
    public void onStepComplete()
    {
    	navPanel.setNext(true);
    }
    
    /**
     * Disallow forward navigation when user input to the current step is not complete.
     */
    public void onStepInProgress()
    {
    	navPanel.setNext(false);
    }
    
    /**
     * Clear data from all panels in the wizard 
     */
    public void reset()
    {
    	for(int i = 0; i < wizardDeck.getWidgetCount(); i++)
    	{
    		WizardStepPanel step = (WizardStepPanel) wizardDeck.getWidget(i);
    		step.reset();
    	}
    }

    /**
     * Call any exit functions as we leave the current step
     */
    private void exitStep()
    {
    	WizardStepPanel w = (WizardStepPanel) wizardDeck.getWidget(currentStep);
    	w.onExit();
    }

    /**
     *  Enter the new step, calling any setup routines
     */
    private void enterStep()
    {
    	WizardStepPanel w = (WizardStepPanel) wizardDeck.getWidget(currentStep);
    	w.onEnter();
    	wizardDeck.showWidget(currentStep);
    	navPanel.setNext(w.isComplete());
    }
}
