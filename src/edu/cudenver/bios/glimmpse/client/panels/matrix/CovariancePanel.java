package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class CovariancePanel extends WizardStepPanel
implements CovariateListener, MatrixResizeListener
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
    
    public void setCovariate(boolean hasCovariate) 
    {

    }

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		// TODO Auto-generated method stub
		
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

	@Override
	public void onColumns(String name, int newCols)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRows(String name, int newRows)
	{
		// TODO Auto-generated method stub
		
	}
}
