package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;

public class IntroPanel extends WizardStepPanel
{

	public IntroPanel(String title, String description)
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
		HTML titleHTML = new HTML(title);
		titleHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		panel.add(titleHTML);
		
		HTML pgHTML = new HTML(description);
		pgHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.add(pgHTML);
		
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		initWidget(panel);
	}

	@Override
	public void reset()
	{
		// static page, no resetting to do
	}

	@Override
	public void loadFromNode(Node node)
	{
		// static page, no loading to do
	}

}
