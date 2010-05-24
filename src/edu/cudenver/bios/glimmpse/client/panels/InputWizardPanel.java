package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.StepsLeftPanel;


public class InputWizardPanel extends Composite
implements NavigationListener, OutcomesListener
{
    private static final String LAYOUT_STYLE = "wizardLayoutPanel";
    // top steps left panel
    protected StepsLeftPanel stepsLeftPanel = new StepsLeftPanel(this);
    
    // deck panel containing all steps in the input wizard
	DeckPanel wizardDeck = new DeckPanel();
	
	// content panels 
	OutcomesPanel outcomesPanel = new OutcomesPanel();
	PredictorsPanel predictorsPanel = new PredictorsPanel();
	StudyGroupsPanel studyGroupsPanel = new StudyGroupsPanel();
	HypothesisPanel hypothesisPanel = new HypothesisPanel();
	EffectSizePanel effectSizePanel = new EffectSizePanel();
	VariabilityPanel variabilityPanel = new VariabilityPanel();
	AlphaPanel alphaPanel = new AlphaPanel();
	OptionsPanel optionsPanel = new OptionsPanel();
	ResultsPanel resultsPanel = new ResultsPanel();
	
	// navigation buttons
	NavigationPanel navPanel = new NavigationPanel();
	
	public InputWizardPanel()
	{
		HorizontalPanel panel = new HorizontalPanel();
		VerticalPanel contentPanel = new VerticalPanel();
		
		// add the content panels to the deck
		wizardDeck.add(outcomesPanel);
		wizardDeck.add(predictorsPanel);
		wizardDeck.add(studyGroupsPanel);
        wizardDeck.add(hypothesisPanel);
        wizardDeck.add(effectSizePanel);
        wizardDeck.add(variabilityPanel);
        wizardDeck.add(alphaPanel);
        wizardDeck.add(optionsPanel);
        wizardDeck.add(resultsPanel);
		wizardDeck.showWidget(0);
		
		// add content callbacks
		outcomesPanel.addOutcomesListener(this);
		
		// add navigation callbacks
		navPanel.addNavigationListener(this);
		navPanel.addNavigationListener(stepsLeftPanel);
		
		// layout the wizard panel
		contentPanel.add(new ToolBar());
		contentPanel.add(wizardDeck);
		contentPanel.add(navPanel);
		panel.add(stepsLeftPanel);		
		panel.add(contentPanel);
		
		// set style
		panel.setStyleName(LAYOUT_STYLE);
		
		// initialize
		initWidget(panel);
	}
	
	public void showWidget(int index)
	{
	    wizardDeck.showWidget(index);
	}
	
	// navigation listener callbacks
	public void onNext()
	{

	}
	
	public void onPrevious()
	{
		
	}
	
	public void onCancel()
	{
		
	}
	
	// outcomes listener
	public void onOutcomes(ArrayList<String> outcomes)
	{

	}
	
	public void onRepetitions(int reps)
	{
		
	}
	
}
