package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class BetaScalePanel extends WizardStepPanel
implements ListValidator, SolvingForListener
{
   	// list of per group sample sizes
    protected ListEntryPanel betaScaleListPanel =
    	new ListEntryPanel(Glimmpse.constants.betaScaleTableColumn(), this);
    
	public BetaScalePanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(Glimmpse.constants.betaScaleTitle());
        HTML description = new HTML(Glimmpse.constants.betaScaleDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		betaScaleListPanel.reset();
	}

	@Override
	public void loadFromNode(Node node)
	{
		betaScaleListPanel.loadFromNode(node);
	}

	@Override
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseDouble(value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
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
	public void onSolvingFor(SolutionType solutionType)
	{
		switch (solutionType)
		{
		case EFFECT_SIZE:
			skip = true;
			break;
		default:	
			skip = false;
		}
	}
	
	public String toXML()
	{
		return betaScaleListPanel.toXML(GlimmpseConstants.TAG_BETA_SCALE_LIST);
	}

}
