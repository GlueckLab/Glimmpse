package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class EffectSizePanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, CovariateListener, ListValidator
{
	protected static final int MAX_RELATIVE_EFFECT_SIZE = 10;
	
	// effect size table
	protected FlexTable effectSizeTable = new FlexTable();
    // error display
    protected HTML errorHTML = new HTML();

	protected List<String> outcomesList = null;
	protected List<RepeatedMeasure> repeatedMeasuresList = null;
	
	protected HashMap<String, ArrayList<String>> predictorMap;
	protected DataTable groupData;
	
    // covariate information - mostly to make it easier to build the essence matrix xml
    protected boolean hasCovariate = false;
    protected double mean = Double.NaN;
    protected double variance = Double.NaN;
    
   	// list of per group sample sizes
    protected ListEntryPanel betaScaleListPanel =
    	new ListEntryPanel(Glimmpse.constants.meanDifferenceTableColumn(), this);
    
    public EffectSizePanel()
    {
    	super();
        VerticalPanel panel = new VerticalPanel();

        // create header/instruction text
        HTML header = new HTML("old");
        HTML description = new HTML("obsolete");
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(createRelativeEffectSizePanel());
        panel.add(createBetaScalePanel());

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        initWidget(panel);
    }
    
    private VerticalPanel createRelativeEffectSizePanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
        HTML header = new HTML("Relative Effect Sizes");
        HTML description = new HTML("Select the bar for the group mean you wish to adjust and type the updated value in the text box");
        
        panel.add(header);
        panel.add(description);
        VerticalPanel tablePanel = new VerticalPanel();
        tablePanel.add(effectSizeTable);
        panel.add(tablePanel);
        
    	// add style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        tablePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);
        effectSizeTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
    	return panel;
    }
        
    private VerticalPanel createBetaScalePanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
        HTML header = new HTML("Actual Effect Sizes");
        HTML description = new HTML("In the list below, enter the true effect sizes you expect.  Note that this is a multiplier...");
        
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);

    	// add style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    	    	
    	return panel;
    }
    
	
    public void reset()
    {
    }

    @Override
    public void onEnter()
    {
    	// clear the flex table 
		effectSizeTable.removeAllRows();
		// add the study groups to the table
    	if (predictorMap.size() > 0 && outcomesList.size() > 0)
    	{
    		effectSizeTable.setWidget(0, 0, new HTML("Group"));
    		effectSizeTable.getFlexCellFormatter().setColSpan(0, 0, groupData.getNumberOfColumns());
    		effectSizeTable.getRowFormatter().setStyleName(0, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);

    		for(int col = 0; col < groupData.getNumberOfColumns(); col++)
    		{
    			effectSizeTable.setWidget(1, col, new HTML(groupData.getColumnLabel(col)));
    		}
    		effectSizeTable.getRowFormatter().setStyleName(1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);

    		for(int row = 0; row < groupData.getNumberOfRows(); row++)
    		{
    			effectSizeTable.getRowFormatter().setStyleName(row+2, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
    			for(int col = 0; col < groupData.getNumberOfColumns(); col++)
    			{
    				effectSizeTable.setWidget(row+2, col, new HTML(groupData.getValueString(row, col)));
    			}
    		}
    		// add outcomes to table
    		if (this.repeatedMeasuresList.size() <= 0)
    		{
        		// for non-repeated measures, we display all outcomes at once.
    			int startCol = groupData.getNumberOfColumns();
    			effectSizeTable.setWidget(0, 1, new HTML("Estimated Means"));
    			effectSizeTable.getFlexCellFormatter().setColSpan(0, startCol, 
    					outcomesList.size());
    			int col = startCol;
    			for(String outcome: outcomesList)
    			{
    				effectSizeTable.setWidget(1, col, new HTML(outcome));
    				for(int row = 2; row < effectSizeTable.getRowCount(); row++)
    				{
    					TextBox tb = new TextBox();
    					tb.setText("0");
    					effectSizeTable.setWidget(row, col, tb);
    				}
    				col++;
    			}
    		}
    		else
    		{
    			// for repeated measures, we display the possible "repeat" combinations at once, and 
    			// provide a drop down list to select the outcome
    		}
    		
    		
    		// add a deck panel to display values for each outcome measures
    		
    	}
    	
    	
    	
    }
   
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		outcomesList = outcomes;
	}

	//@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		this.repeatedMeasuresList = repeatedMeasures;
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap, DataTable groupData)
	{
		this.predictorMap = predictorMap;	
		this.groupData = groupData;
	}
	
	@Override
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
	}

	@Override
	public void validate(String value)
			throws IllegalArgumentException
	{
		try
		{
			Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException(Glimmpse.constants.errorInvalidNumber());
		}
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		int rows = groupData.getNumberOfRows();
		int cols = outcomesList.size(); // TODO: repeated MEASURES!!!!
		
		// convert the estimated means into a beta matrix
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		buffer.append(" ");
		buffer.append(GlimmpseConstants.ATTR_NAME);
		buffer.append("='");
		buffer.append(GlimmpseConstants.MATRIX_BETA);
		buffer.append("' combineHorizontal='false'>");
		// fixed part of the beta matrix
		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, rows, cols);

		for(int row = 0; row < rows; row++)
		{
			buffer.append("<r>");
			for(int col = 0; col < cols; col++)
			{
				buffer.append("<c>");
				
				TextBox tb = (TextBox) effectSizeTable.getWidget(row+2, predictorMap.size() + col);
				buffer.append(tb.getText());
				
				buffer.append("</c>");
			}
			buffer.append("</r>");
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

		// random part of the beta matrix
		if (hasCovariate)
		{
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, cols);
			
			buffer.append("<r>");
			for(int col = 0; col < cols; col++)
			{
				buffer.append("<c>1</c>");
			}
			buffer.append("<r>");
			
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		
		// close the fixed/rand beta matrix
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		
		// add the beta scale information
		buffer.append(betaScaleListPanel.toXML(GlimmpseConstants.TAG_BETA_SCALE_LIST));
		
		return buffer.toString();
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}

	@Override
	public void onMean(double mean)
	{
		this.mean = mean;
	}

	@Override
	public void onVariance(double variance)
	{
		this.variance = variance;
	}
}
