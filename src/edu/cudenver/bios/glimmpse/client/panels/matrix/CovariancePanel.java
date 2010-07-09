package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class CovariancePanel extends WizardStepPanel
{
	public CovariancePanel()
	{
		super(Glimmpse.constants.stepsLeftSigma());
		VerticalPanel panel = new VerticalPanel();
		
		panel.add(new HTML("covariance panel"));
		
		initWidget(panel);
	}

    public void reset()
    {
    }
}
