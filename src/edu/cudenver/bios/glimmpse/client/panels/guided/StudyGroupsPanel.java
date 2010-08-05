package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class StudyGroupsPanel extends WizardStepPanel
implements SolvingForListener, PredictorsListener, OutcomesListener, DynamicListValidator
{
	
   	// list of per group sample sizes
	protected String[] columnNames = { Glimmpse.constants.perGroupSampleSizeTableColumn() };
    protected DynamicListPanel perGroupNListPanel =
    	new DynamicListPanel(columnNames, this);
    
    protected CheckBox equalSizesCheckBox = new CheckBox();
    
    protected VerticalPanel groupSizesPanel = new VerticalPanel();
    
    protected VerticalPanel perGroupSampleSizePanel = new VerticalPanel();
    
    protected Grid groupSizesTable = new Grid(1,1);
    
    public StudyGroupsPanel()
    {
    	super(Glimmpse.constants.stepsLeftGroups());
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.studyGroupsTitle());
        HTML description = new HTML(Glimmpse.constants.studyGroupsDescription());

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        groupSizesPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);
        groupSizesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        groupSizesTable.getRowFormatter().setStylePrimaryName(0, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_COLUMN_HEADER);
    
        groupSizesPanel.add(groupSizesTable);
        panel.add(header);
        panel.add(description);
        panel.add(groupSizesPanel);
        panel.add(perGroupSampleSizePanel);

        initWidget(panel);
    }
    
    private void buildRatioPanel()
    {
    	
    }
    
    private void buildPerGroupSampleSizePanel()
    {
        HTML header = new HTML(Glimmpse.constants.perGroupSampleSizeTitle());
        HTML description = new HTML(Glimmpse.constants.perGroupSampleSizeDescription());
        
    	perGroupSampleSizePanel.add(header);
    	perGroupSampleSizePanel.add(description);
    	perGroupSampleSizePanel.add(perGroupNListPanel);
    	
    	perGroupSampleSizePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
    }
    
    public void reset()
    {
    	
    }

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onRepeatedMeasures(List<String> repeatedMeasures)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap)
	{
		Set<String>predictors = predictorMap.keySet();
		if (predictors.size() > 0)
		{
			int rows = 1;
			for(ArrayList<String> categories: predictorMap.values())
			{
				rows *= categories.size();
			}
			groupSizesTable.resize(rows+1, predictors.size()+1);
			groupSizesTable.setWidget(0, 0, new HTML("Relative<br>Group<br>Size"));
			for(int r = 1; r < rows+1; r++)
			{
				ListBox sizeTb = new ListBox();
				for(int size = 1; size <= 10; size++) sizeTb.addItem(Integer.toString(size));
				groupSizesTable.setWidget(r, 0, sizeTb);
			}
			int previousRepeat = 0;
			int col = 1;
			for(String predictor: predictors)
			{
				ArrayList<String> categories = predictorMap.get(predictor);
				groupSizesTable.setWidget(0, col, new HTML(predictor));
				int r = 1;
				if (previousRepeat == 0)
				{
					previousRepeat = rows / categories.size();
					for(String category: categories)
					{
						for (int reps = 0; reps < previousRepeat; reps++, r++) 
							groupSizesTable.setWidget(r, col, new HTML(category));
					}
				}
				else
				{
					int categorylistRepeat = rows / previousRepeat;
					previousRepeat = previousRepeat / categories.size();
					groupSizesTable.setWidget(0, col, new HTML(predictor));
					for(int categoryListRep = 0; categoryListRep < categorylistRepeat; categoryListRep++)
					{
						for(String category: categories)
						{
							for (int reps = 0; reps < previousRepeat; reps++, r++) 
								groupSizesTable.setWidget(r, col, new HTML(category));
						}
					}
				}
				col++;
			}

		}
		
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
	public void validate(String value, int column)
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
