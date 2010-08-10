package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class StudyGroupsPanel extends WizardStepPanel
implements SolvingForListener, PredictorsListener, OutcomesListener, ListValidator
{
	protected static final int MAX_RELATIVE_SIZE = 10;
   	// list of per group sample sizes
    protected ListEntryPanel perGroupNListPanel =
    	new ListEntryPanel(Glimmpse.constants.perGroupSampleSizeTableColumn(), this);
    // panel containing group sample size list
    protected VerticalPanel perGroupSampleSizePanel = new VerticalPanel();
    
    // data table to display possible groups
    protected FlexTable groupSizesTable = new FlexTable();
    
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
    	
        HTML header = new HTML("Relative group sizes");
        HTML description = new HTML("Group size instructions...");
        
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
    		groupSizesTable.setWidget(0, 0, new HTML("Relative Group Size"));
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
}
