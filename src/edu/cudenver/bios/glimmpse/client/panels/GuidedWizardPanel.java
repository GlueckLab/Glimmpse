package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;

import edu.cudenver.bios.glimmpse.client.StudyDesignManager;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.panels.guided.EffectSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.OutcomesPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.PredictorsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.StudyGroupsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityPanel;

public class GuidedWizardPanel extends Composite
implements StudyDesignManager
{
	// content panels 
	AlphaPanel alphaPanel = new AlphaPanel();
	OutcomesPanel outcomesPanel = new OutcomesPanel();
	PredictorsPanel predictorsPanel = new PredictorsPanel();
	StudyGroupsPanel studyGroupsPanel = new StudyGroupsPanel();
	HypothesisPanel hypothesisPanel = new HypothesisPanel();
	EffectSizePanel effectSizePanel = new EffectSizePanel();
	VariabilityPanel variabilityPanel = new VariabilityPanel();
	OptionsPanel optionsPanel = new OptionsPanel();
	ResultsPanel resultsPanel = new ResultsPanel(this);
	
    // list of panels for the wizard
	WizardStepPanel[] panelList = {
			alphaPanel, 
			outcomesPanel, 
			predictorsPanel, 
			studyGroupsPanel, 
			hypothesisPanel,
			effectSizePanel,
			variabilityPanel,
			optionsPanel,
			resultsPanel};
	
	// wizard navigation panel
	WizardPanel wizardPanel;
	
	/**
	 * Create an empty matrix panel
	 */
	public GuidedWizardPanel()
	{	
		VerticalPanel panel = new VerticalPanel();
		
		wizardPanel = new WizardPanel(panelList);
		panel.add(wizardPanel);

		// set up listener relationships
		outcomesPanel.addOutcomesListener(studyGroupsPanel);
		predictorsPanel.addPredictorsListener(studyGroupsPanel);
		predictorsPanel.addCovariateListener(optionsPanel);
		// initialize
		initWidget(panel);
	}
	
	/**
	 * Fill in the wizard from an XML description of the study matrices
	 */
	public void loadFromXML(Document doc)
	{
		
	}
    
    public void reset()
    {
    	wizardPanel.reset();
    }
    
    public void addCancelListener(CancelListener listener)
    {
    	wizardPanel.addCancelListener(listener);
    }

	@Override
	public String getPowerRequestXML()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStudyDesignXML()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Solution getSolvingFor()
	{
		return Solution.POWER;
	}
}
