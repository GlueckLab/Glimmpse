package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.listener.RelativeGroupSizeListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class StudyGroupsPanel extends WizardStepPanel
implements SolvingForListener, PredictorsListener, 
OutcomesListener, CovariateListener, ListValidator
{
	protected static final int MAX_RELATIVE_SIZE = 10;
   	// list of per group sample sizes
    protected ListEntryPanel perGroupNListPanel =
    	new ListEntryPanel(Glimmpse.constants.perGroupSampleSizeTableColumn(), this);
    // panel containing group sample size list
    protected VerticalPanel perGroupSampleSizePanel = new VerticalPanel();
    
    // data table to display possible groups
    protected FlexTable groupSizesTable = new FlexTable();
    
    // listeners for relative size events
    protected ArrayList<RelativeGroupSizeListener> listeners = new ArrayList<RelativeGroupSizeListener>();
    
    // covariate information - mostly to make it easier to build the essence matrix xml
    protected boolean hasCovariate = false;
    protected double mean = Double.NaN;
    protected double variance = Double.NaN;
    
    public StudyGroupsPanel()
    {
    	super(Glimmpse.constants.stepsLeftGroups());
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.studyGroupsTitle());
        HTML description = new HTML(Glimmpse.constants.studyGroupsDescription());

        // build the per group sample size list
        buildPerGroupSampleSizePanel();
    
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(createRatioPanel());
        panel.add(perGroupSampleSizePanel);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        initWidget(panel);
    }
    
    /**
     * Create panel containing all possible group sizes
     * 
     * @return group sizes panel
     */
    private VerticalPanel createRatioPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	VerticalPanel tablePanel = new VerticalPanel();
    	
        HTML header = new HTML(Glimmpse.constants.relativeGroupSizeTitle());
        HTML description = new HTML(Glimmpse.constants.relativeGroupSizeDescription());
        
        panel.add(header);
        panel.add(description);
        tablePanel.add(groupSizesTable);
        panel.add(tablePanel);
        
    	// add style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        tablePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);
        groupSizesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);

    	return panel;
    }
    
    /**
     * Layout the widgets in the per group sample size panel
     */
    private void buildPerGroupSampleSizePanel()
    {
        HTML header = new HTML(Glimmpse.constants.perGroupSampleSizeTitle());
        HTML description = new HTML(Glimmpse.constants.perGroupSampleSizeDescription());
        
    	perGroupSampleSizePanel.add(header);
    	perGroupSampleSizePanel.add(description);
    	perGroupSampleSizePanel.add(perGroupNListPanel);
    	
    	perGroupSampleSizePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	perGroupSampleSizePanel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);

    }
    
    public void reset()
    {
    	groupSizesTable.removeAllRows();
    }
    
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		// TODO
	}

	@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap, DataTable groups)
	{
    	reset();
    	if (predictorMap.size() > 0)
    	{
    		groupSizesTable.getRowFormatter().setStyleName(0, 
    				GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
    		groupSizesTable.setWidget(0, 0, new HTML(Glimmpse.constants.relativeGroupSizeTableColumn()));
    		for(int col = 0; col < groups.getNumberOfColumns(); col++)
    		{
    			groupSizesTable.setWidget(0, col+1, new HTML(groups.getColumnLabel(col)));
    		}
    		for(int row = 0; row < groups.getNumberOfRows(); row++)
    		{
    			groupSizesTable.setWidget(row+1, 0, createGroupSizeListBox());
    			groupSizesTable.getRowFormatter().setStyleName(row+1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
    			for(int col = 0; col < groups.getNumberOfColumns(); col++)
    			{
    				groupSizesTable.setWidget(row+1, col+1, new HTML(groups.getValueString(row, col)));
    			}
    		}
    	}
	}
	
	private ListBox createGroupSizeListBox()
	{
		ListBox lb = new ListBox();
		for(int i = 1; i <= MAX_RELATIVE_SIZE; i++) lb.addItem(Integer.toString(i));
		return lb;
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
    		TextValidation.parseInteger(value, 0, true);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
    	}
	}

	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		perGroupSampleSizePanel.setVisible(solutionType != SolutionType.TOTAL_N);
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ESSENCE_MATRIX);

		// build the "fixed" cell means matrix.  Essentially, this is an identity matrix
		// with rows & columns equal to the number of study sub groups
		int size = groupSizesTable.getRowCount() -1;  // skip header

		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, size, size);

		// identity matrix
		for(int row = 0; row < size; row++)
		{
			buffer.append("<r>");
			for(int col = 0; col < size; col++)
			{
				buffer.append("<c>");
				if (row == col)
					buffer.append(1);
				else
					buffer.append(0);
				buffer.append("</c>");
			}
			buffer.append("</r>");
		}
		
		// close tag
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		
		// build row meta data list
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW_META_DATA);

		for(int row = 1; row <= size; row++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(row, 0);
			if (lb != null) buffer.append("<r ratio='" + lb.getItemText(lb.getSelectedIndex()) + "' />");
		}

		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW_META_DATA);
		
		// check if there is a random matrix
		if (hasCovariate)
		{
			// list random column meta data
			buffer.append("<randomColumnMetaData>");
			buffer.append("<c mean='");
			buffer.append(mean);
			buffer.append("' variance='");
			buffer.append(variance);
			buffer.append("'></c></randomColumnMetaData>");

			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, size, 1);
			for(int row = 0; row < size; row++)
			{
				buffer.append("<r><c>1</c></r>");
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
			
		}
		
		// close tag for essence matrix
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ESSENCE_MATRIX);
		
		// add sample size list information
		buffer.append(perGroupNListPanel.toXML(GlimmpseConstants.TAG_SAMPLE_SIZE_LIST));
		
		return buffer.toString();
	}
	
	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		this.perGroupNListPanel.toXML(GlimmpseConstants.TAG_SAMPLE_SIZE_LIST);
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
	
	public void addRelativeGroupSizeListener(RelativeGroupSizeListener listener)
	{
		listeners.add(listener);
	}
	
	@Override
	public void onExit()
	{
		ArrayList<Integer> relativeSizes = new ArrayList<Integer>();
		for(int i = 1; i < groupSizesTable.getRowCount(); i++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(i, 0);
			int value = Integer.parseInt(lb.getItemText(lb.getSelectedIndex()));
			relativeSizes.add(value);
		}
		
		for(RelativeGroupSizeListener listener: listeners) listener.onRelativeGroupSize(relativeSizes);

	}
}
