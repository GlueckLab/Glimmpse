package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class VariabilityCovariatePanel extends WizardStepPanel
implements CovariateListener, OutcomesListener
{
	public VariabilityCovariatePanel()
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
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = !hasCovariate;
	}

	@Override
	public void onMean(double mean)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVariance(double variance)
	{
		// TODO Auto-generated method stub
		
	}

	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();

		return buffer.toString();
	}

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		// TODO Auto-generated method stub
		
	}
}
