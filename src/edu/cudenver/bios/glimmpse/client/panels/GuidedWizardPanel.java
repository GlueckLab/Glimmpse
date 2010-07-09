package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class GuidedWizardPanel extends Composite
implements NavigationListener, StepStatusListener
{
    // top steps left panel
    protected static final String[] stepNames = {
        Glimmpse.constants.stepsLeftAlpha(),
        Glimmpse.constants.stepsLeftOutcomes(),
        Glimmpse.constants.stepsLeftPredictors(),
        Glimmpse.constants.stepsLeftGroups(),
        Glimmpse.constants.stepsLeftHypotheses(),
        Glimmpse.constants.stepsLeftEffectSize(),
        Glimmpse.constants.stepsLeftVariability(),
        Glimmpse.constants.stepsLeftOptions(),
        Glimmpse.constants.stepsLeftResults() 	
    };
    protected StepsLeftPanel stepsLeftPanel = new StepsLeftPanel(stepNames);

    // deck panel containing all steps in the input wizard
	DeckPanel wizardDeck = new DeckPanel();
	
	// content panels 
	AlphaPanel alphaPanel = new AlphaPanel(Glimmpse.constants.stepsLeftAlpha());
	OutcomesPanel outcomesPanel = new OutcomesPanel();
	PredictorsPanel predictorsPanel = new PredictorsPanel();
	StudyGroupsPanel studyGroupsPanel = new StudyGroupsPanel();
	HypothesisPanel hypothesisPanel = new HypothesisPanel();
	EffectSizePanel effectSizePanel = new EffectSizePanel();
	VariabilityPanel variabilityPanel = new VariabilityPanel();
	OptionsPanel optionsPanel = new OptionsPanel();
	ResultsPanel resultsPanel = new ResultsPanel();
	
	// navigation buttons
	NavigationPanel navPanel = new NavigationPanel();
	
	/**
	 * Create an empty matrix panel
	 */
	public GuidedWizardPanel()
	{		
		HorizontalPanel panel = new HorizontalPanel();
		VerticalPanel contentPanel = new VerticalPanel();
		
		// add the content panels to the deck
        wizardDeck.add(alphaPanel);
		wizardDeck.add(outcomesPanel);
		wizardDeck.add(predictorsPanel);
		wizardDeck.add(studyGroupsPanel);
        wizardDeck.add(hypothesisPanel);
        wizardDeck.add(effectSizePanel);
        wizardDeck.add(variabilityPanel);
        wizardDeck.add(optionsPanel);
        wizardDeck.add(resultsPanel);
		wizardDeck.showWidget(0);
		
		// add content callbacks
		//outcomesPanel.addOutcomesListener(this);
		
		// add navigation callbacks
		navPanel.addNavigationListener(this);
		navPanel.addNavigationListener(stepsLeftPanel);
		
		// add listeners for step status updates
		alphaPanel.addStepStatusListener(this);
		alphaPanel.addStepStatusListener(stepsLeftPanel);
		
		// layout the wizard panel
		contentPanel.add(new ToolBar());
		contentPanel.add(wizardDeck);
		//contentPanel.add(navPanel);
		panel.add(stepsLeftPanel);		
		panel.add(contentPanel);
		
		// set style
		//panel.setStyleName(LAYOUT_STYLE);		
		
		// set up listener relationships
		stepsLeftPanel.addNavigationListener(this);

		// initialize
		initWidget(panel);
	}
	
	/**
	 * Fill in the wizard from an XML description of the study design
	 */
	public void loadFromXML(Document doc)
	{
		
	}
	
    /**
     * Call back when "next" navigation button is clicked
     * Does nothing if already at end of step list
     */
    public void onNext()
    {

    }
    
    /**
     * Call back when "previous" navigation button is clicked
     * Does nothing if already at beginning of step list
     */
    public void onPrevious()
    {

    }
    
    /**
     * Return to first step
     */
    public void onCancel()
    {

    }
    
    public void onStep(int stepIndex)
    {
    	
    }
    
    public void onStepComplete(String stepName)
    {
    	Window.alert("Complete notification: " + stepName);
    }
    
    public void onStepInProgress(String stepName)
    {
    	Window.alert("In Progress notification: " + stepName);
    }
    
    public void reset()
    {
    	
    }
}
