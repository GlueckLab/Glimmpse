package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener.SolutionType;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class PerGroupSampleSizePanel extends WizardStepPanel
implements ListValidator, SolvingForListener
{
   	// list of per group sample sizes
    protected ListEntryPanel perGroupNListPanel =
    	new ListEntryPanel(Glimmpse.constants.perGroupSampleSizeTableColumn(), this);
    
	public PerGroupSampleSizePanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(Glimmpse.constants.perGroupSampleSizeTitle());
        HTML description = new HTML(Glimmpse.constants.perGroupSampleSizeDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(perGroupNListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
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
		skip = (solutionType == SolutionType.TOTAL_N);
	}
	
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		perGroupNListPanel.reset();
	}

	@Override
	public void loadFromNode(Node node)
	{
		perGroupNListPanel.loadFromNode(node);
		onValidRowCount(perGroupNListPanel.getValidRowCount());
	}
	
	public String toXML()
	{
		return perGroupNListPanel.toXML(GlimmpseConstants.TAG_SAMPLE_SIZE_LIST);
	}

}
