package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.List;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.RepeatedMeasuresListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class VariabilityErrorPanel extends WizardStepPanel
implements OutcomesListener, RepeatedMeasuresListener,
CovariateListener
{
	protected static final int INDEPENDENT_GROUPS_INDEX = 0;
	protected static final int REPEATED_MEASURES_INDEX = 0;

	protected FlexTable indepGroupsOutcomesTable = new FlexTable();
	protected FlexTable indepGroupsOutcomeCorrelationsTable = new FlexTable();

	protected boolean hasRepeatedMeasures = false;
	protected List<String> outcomes = null;
	
	protected DeckPanel deckPanel = new DeckPanel();
    public VariabilityErrorPanel()
    {
    	super();
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.variabilityErrorTitle());
        HTML description = new HTML(Glimmpse.constants.variabilityErrorDescription());
        
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

    	Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML("Estimated standard deviation for each outcome: "));
		grid.setWidget(0, 1, new TextBox());
		grid.setWidget(1, 0, new HTML("Correlation between any pair of outcomes: "));
		grid.setWidget(1, 1, new TextBox());
		
		HorizontalPanel adjustPanel = new HorizontalPanel();
		adjustPanel.add(new CheckBox());
		adjustPanel.add(new HTML("I would like to check power for half and twice these estimates"));
		panel.add(grid);
		panel.add(adjustPanel);		

        return panel;
    }

    private VerticalPanel createRepeatedMeasuresPanel()
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
			int upperTriangleCorrelatinIndex = 0;
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_SIGMA_ERROR, size, size);
			for(int row = 0; row < size; row++)
			{
				int lowerTriangleCorrelatinIndex = row - 1;
				XMLUtilities.openTag(buffer, "r");
				for(int col = 0; col < size; col++)
				{
					XMLUtilities.openTag(buffer, "c");
					String value;
					if (row == col)
					{
						TextBox tb = (TextBox) indepGroupsOutcomesTable.getWidget(row, 1);
						value = tb.getText();
					}
					else if (col > row)
					{
						// filling in upper triangle of covariance matrix
						TextBox tb = (TextBox) indepGroupsOutcomeCorrelationsTable.getWidget(upperTriangleCorrelatinIndex++, 1);
						value = tb.getText();
					}
					else
					{
						// filling lower triangle of covariance matrix
						TextBox tb = (TextBox) indepGroupsOutcomeCorrelationsTable.getWidget(lowerTriangleCorrelatinIndex, 1);
						value = tb.getText();
						lowerTriangleCorrelatinIndex += (size-col-2); // trust me, this works
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
		indepGroupsOutcomesTable.removeAllRows();
		indepGroupsOutcomeCorrelationsTable.removeAllRows();
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
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = hasCovariate;
	}

	@Override
	public void onMean(double mean)
	{
		// no action
	}

	@Override
	public void onVariance(double variance)
	{
		// no action
	}

}
