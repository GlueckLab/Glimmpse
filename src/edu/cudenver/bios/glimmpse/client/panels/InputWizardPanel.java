package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.panels.StepsLeftPanel;


public class InputWizardPanel extends Composite
{
    private static final String LAYOUT_STYLE = "wizardLayoutPanel";
    // top steps left panel
    protected StepsLeftPanel stepsLeftPanel = new StepsLeftPanel(this);
    
    // deck panel containing all steps in the input wizard
	DeckPanel wizardDeck = new DeckPanel();

	public InputWizardPanel()
	{
		HorizontalPanel panel = new HorizontalPanel();
		VerticalPanel contentPanel = new VerticalPanel();
		
		wizardDeck.add(new OutcomesPanel());
		wizardDeck.add(new PredictorsPanel());
		wizardDeck.add(new StudyGroupsPanel());
        wizardDeck.add(new HypothesisPanel());
        wizardDeck.add(new EffectSizePanel());
        wizardDeck.add(new VariabilityPanel());
        wizardDeck.add(new AlphaPanel());
        wizardDeck.add(new OptionsPanel());
        wizardDeck.add(new ResultsPanel());
		wizardDeck.showWidget(0);
		
		contentPanel.add(wizardDeck);
		
		panel.add(stepsLeftPanel);		
		panel.add(contentPanel);
		
		panel.setStyleName(LAYOUT_STYLE);
		initWidget(panel);
	}
	

	public void showWidget(int index)
	{
	    wizardDeck.showWidget(index);
	}
}
