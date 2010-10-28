package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class MeanDifferencesScalePanel extends WizardStepPanel
implements ListValidator
{
  	// list of per group sample sizes
    protected ListEntryPanel betaScaleListPanel =
    	new ListEntryPanel(Glimmpse.constants.meanDifferenceTableColumn(), this);
    
	public MeanDifferencesScalePanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.meanDifferenceTitle());
        HTML description = new HTML(Glimmpse.constants.meanDifferenceDescription());
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		initWidget(panel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

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

}
