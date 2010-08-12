package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.LearCovariancePanel;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class VariabilityPanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, CovariateListener
{
	protected static final int INDEPENDENT_GROUPS_INDEX = 0;
	protected static final int REPEATED_MEASURES_INDEX = 0;

	protected FlexTable indepGroupsOutcomesTable = new FlexTable();
	protected FlexTable indepGroupsOutcomeCorrelationsTable = new FlexTable();

	protected boolean hasRepeatedMeasures = false;
	protected List<String> outcomes = null;
	
	protected DeckPanel deckPanel = new DeckPanel();
    public VariabilityPanel()
    {
    	super(Glimmpse.constants.stepsLeftVariability());
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.variabilityTitle());
        HTML description = new HTML(Glimmpse.constants.variabilityDescription());
        
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

        panel.add(createIndependentOutcomesPanel());
        panel.add(createIndependentCorrelationsPanel());
        
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);

        return panel;
    }
    
    private VerticalPanel createIndependentOutcomesPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML header = new HTML("Estimated Standard Deviations");
    	HTML description = new HTML("Enter hour stddev estimates below");

    	panel.add(header);
    	panel.add(description);
    	panel.add(indepGroupsOutcomesTable);
    	
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);

    	return panel;
    }
    
    private VerticalPanel createIndependentCorrelationsPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML header = new HTML("Associations Between Outcome Measures");
    	HTML description = new HTML("Enter covariance and stuff");

    	panel.add(header);
    	panel.add(description);
    	panel.add(indepGroupsOutcomeCorrelationsTable);
    	
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);

    	return panel;
    }
    
    public VerticalPanel createRepeatedMeasuresPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	return panel;
    }
    
    public void reset()
    {
    	
    }

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		if (!hasRepeatedMeasures)
		{
			// TODO: baseline covariate
			int size = indepGroupsOutcomesTable.getRowCount();
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_SIGMA_ERROR, size, size);
			for(int row = 0; row < size; row++)
			{
				XMLUtilities.openTag(buffer, "r");
				int correlationIndex = 0;
				for(int col = 0; col < size; col++)
				{
					XMLUtilities.openTag(buffer, "c");
					String value;
					if (row == col)
					{
						TextBox tb = (TextBox) indepGroupsOutcomesTable.getWidget(row, 1);
						value = tb.getText();
					}
					else
					{
						// TODO: enter the correlation value
						TextBox tb = (TextBox) indepGroupsOutcomeCorrelationsTable.getWidget(correlationIndex, 1);
						value = tb.getText();
						correlationIndex++;
					}
					buffer.append(value);
					XMLUtilities.closeTag(buffer, "c");
				}
				XMLUtilities.closeTag(buffer, "r");

			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		else
		{
			
		}
		
		
		return buffer.toString();
	}
	
	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		return buffer.toString();
	}

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		int i = 0;
		for(String outcome: outcomes)
		{
			addOutcome(outcome);
			addOutcomeCorrelations(outcome, ++i, outcomes);
		}
	}

	private void addOutcome(String outcome)
	{
		int startRow = indepGroupsOutcomesTable.getRowCount();
		indepGroupsOutcomesTable.setWidget(startRow, 0, new HTML(outcome));
		indepGroupsOutcomesTable.setWidget(startRow, 1, new TextBox());
	}
	
	private void addOutcomeCorrelations(String outcome, int startIndex, List<String> outcomes)
	{
		int startRow = indepGroupsOutcomeCorrelationsTable.getRowCount();
		for(int i = startIndex; i < outcomes.size(); i++)
		{
				indepGroupsOutcomeCorrelationsTable.setWidget(startRow, 0, 
						new HTML("Correlation of " + outcome + " and " + outcomes.get(i)));
				indepGroupsOutcomeCorrelationsTable.setWidget(startRow, 1, new TextBox()); 
				startRow++;
		}
	}
	
	@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap,
			DataTable groups)
	{
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMean(double mean)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVariance(double variance)
	{
		// TODO Auto-generated method stub
		
	}
}
