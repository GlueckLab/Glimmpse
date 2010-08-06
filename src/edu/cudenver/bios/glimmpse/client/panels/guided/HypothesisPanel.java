package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.visualization.client.DataTable;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class HypothesisPanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, ClickHandler
{   
	private static final int INDEPENDENT_GROUPS_INDEX = 0;
	private static final int REPEATED_MEASURES_INDEX = 1;
	
	protected boolean hasRepeatedMeasures = false;
	protected List<String> outcomes = null;
	protected HashMap<String, ArrayList<String>> predictorMap = null;
	protected DeckPanel deckPanel = new DeckPanel();
	
	// independent groups widgets
	protected FlexTable independentMainEffectsTable = new FlexTable();
	protected FlexTable independentInteractionsTable = new FlexTable();

	
    public HypothesisPanel()
    {
    	super(Glimmpse.constants.stepsLeftHypotheses());
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.hypothesisTitle());
        HTML description = new HTML(Glimmpse.constants.hypothesisDescription());
        
        deckPanel.add(createIndependentGroupsPanel());
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

    private VerticalPanel createIndependentGroupsPanel()
    {
    	VerticalPanel panel = new VerticalPanel();

        panel.add(createIndependentMainEffectsPanel());
        panel.add(createIndependentInteractionsPanel());

        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);

        
        return panel;
    	
    }
    
    private VerticalPanel createIndependentMainEffectsPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML header = new HTML("Main Effects");

    	panel.add(header);
    	panel.add(this.independentMainEffectsTable);
    	
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
    }
    
    private VerticalPanel createIndependentInteractionsPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML header = new HTML("Interactions");

    	panel.add(header);
    	panel.add(this.independentInteractionsTable);
    	
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
    }
    
    private VerticalPanel createRepeatedMeasuresPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	return panel;
    }
    
    public void reset() 
    {
    	independentInteractionsTable.removeAllRows();
    	independentMainEffectsTable.removeAllRows();
    }

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		this.outcomes = outcomes;
	}

	@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		if (repeatedMeasures == null || repeatedMeasures.size() <= 0)
		{
			deckPanel.showWidget(INDEPENDENT_GROUPS_INDEX);
		}
		else
		{
			deckPanel.showWidget(REPEATED_MEASURES_INDEX);
		}
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap, DataTable groups)
	{
		this.predictorMap = predictorMap;
	}
	
	@Override
	public void onEnter()
	{
		Object[] predictorArray = (Object[]) predictorMap.keySet().toArray();
		reset();
		int i = 0;
		for(Object predictor: predictorArray)
		{
			addMainEffect((String) predictor);
			addInteractions((String) predictor, ++i, predictorArray);
		}
	}
	
	private void addMainEffect(String predictor)
	{
		int startRow = independentMainEffectsTable.getRowCount();
		for(String outcome: outcomes)
		{
			CheckBox cb = new CheckBox();
			cb.addClickHandler(this);
			independentMainEffectsTable.setWidget(startRow, 0, cb);
			independentMainEffectsTable.setWidget(startRow, 1, new HTML(predictor + " main effect on " + outcome));
			startRow++;
		}
	}
	
	private void addInteractions(String predictor, int startIndex, Object[] predictorArray)
	{
		int startRow = independentInteractionsTable.getRowCount();
		for(int i = startIndex; i < predictorArray.length; i++)
		{
			for(String outcome: outcomes)
			{
				CheckBox cb = new CheckBox();
				cb.addClickHandler(this);
				independentInteractionsTable.setWidget(startRow, 0, cb);
				independentInteractionsTable.setWidget(startRow, 1, 
						new HTML(predictor + " x  " + (String) predictorArray[i] + " interaction effect on " + outcome));
				startRow++;
			}
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
		boolean hypothesisSelected = false;

		// check if any hypotheses are selected
		if (deckPanel.getVisibleWidget() == INDEPENDENT_GROUPS_INDEX)
		{
			for(int r = 0; r < independentMainEffectsTable.getRowCount(); r++)
			{
				CheckBox cb = (CheckBox) independentMainEffectsTable.getWidget(r, 0);
				if (cb.getValue())
				{
					hypothesisSelected = true;
					break;
				}
			}
			if (!hypothesisSelected)
			{
				for(int r = 0; r < independentInteractionsTable.getRowCount(); r++)
				{
					CheckBox cb = (CheckBox) independentInteractionsTable.getWidget(r, 0);
					if (cb.getValue())
					{
						hypothesisSelected = true;
						break;
					}
				}
			}
		}
		else
		{
			
		}
		
		if (hypothesisSelected)
			notifyComplete();
		else
			notifyInProgress();
	}
}
