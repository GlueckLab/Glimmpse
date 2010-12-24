package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.ListUtilities;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class VariabilityRepeatedMeasuresPanel extends WizardStepPanel
implements OutcomesListener
{
	public VariabilityRepeatedMeasuresPanel()
	{
		super();
		skip = true;
		VerticalPanel panel = new VerticalPanel();
		
		initWidget(panel);
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
	public void onOutcomes(List<String> outcomes)
	{
		// TODO Auto-generated method stub

	}

	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		return buffer.toString();
	}
}
