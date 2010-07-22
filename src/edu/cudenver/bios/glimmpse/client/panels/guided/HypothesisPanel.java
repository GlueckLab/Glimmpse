package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DeckPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class HypothesisPanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener
{   
	private static final int INDEPENDENT_GROUPS_INDEX = 0;
	private static final int REPEATED_MEASURES_INDEX = 1;
	
	protected boolean hasRepeatedMeasures = false;
	protected List<String> outcomes = null;
	
	protected DeckPanel deckPanel = new DeckPanel();
	
    public HypothesisPanel()
    {
    	super(Glimmpse.constants.stepsLeftHypotheses());
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.hypothesisTitle());
        HTML description = new HTML(Glimmpse.constants.hypothesisDescription());
        
        deckPanel.add(createNonRepeatedMeasuresPanel());
        deckPanel.add(createRepeatedMeasuresPanel());
        deckPanel.showWidget(INDEPENDENT_GROUPS_INDEX);
        
        panel.add(header);
        panel.add(description);
        panel.add(deckPanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        initWidget(panel);
    }

    private VerticalPanel createNonRepeatedMeasuresPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	return panel;
    }
    
    private VerticalPanel createRepeatedMeasuresPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	return panel;
    }
    
    public void reset() {}

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		this.outcomes = outcomes;
	}

	@Override
	public void onRepeatedMeasures(List<String> repeatedMeasures)
	{
		hasRepeatedMeasures = (repeatedMeasures == null);
		if (repeatedMeasures == null)
		{
			
		}
		else
		{
			
		}
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap)
	{
		int i = 0;
		for(String predictor: predictorMap.keySet())
		{
			addMainEffect(predictor);
			addInteractions(predictor, i++, predictorMap);
		}
	}
	
	private void addMainEffect(String predictor)
	{
		
	}
	
	private void addInteractions(String predictor, int startingIdx, HashMap<String, ArrayList<String>> predictorMap)
	{
		//for(int i = startingIdx; i < )
	}
}
