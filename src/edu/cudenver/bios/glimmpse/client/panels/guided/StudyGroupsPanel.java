package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class StudyGroupsPanel extends WizardStepPanel
implements PredictorsListener, OutcomesListener, DynamicListValidator
{
	
   	// list of per group sample sizes
	protected String[] columnNames = { Glimmpse.constants.perGroupSampleSizeTableColumn() };
    protected DynamicListPanel perGroupNListPanel =
    	new DynamicListPanel(columnNames, this);
    
    protected CheckBox equalSizesCheckBox = new CheckBox();
    
    protected VerticalPanel groupSizesPanel = new VerticalPanel();
    
    public StudyGroupsPanel()
    {
    	super(Glimmpse.constants.stepsLeftGroups());
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.studyGroupsTitle());
        HTML description = new HTML(Glimmpse.constants.studyGroupsDescription());
        
        // checkbox for equal group sizes
        HorizontalPanel checkBoxPanel = new HorizontalPanel();
        equalSizesCheckBox.setValue(true);
        checkBoxPanel.add(equalSizesCheckBox);
        checkBoxPanel.add(new HTML(Glimmpse.constants.equalGroupsLabel()));
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        panel.add(header);
        panel.add(description);
        panel.add(checkBoxPanel);
        panel.add(perGroupNListPanel);
        panel.add(groupSizesPanel);
        initWidget(panel);
    }
    
    private void buildRatioPanel()
    {
    	
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
}
