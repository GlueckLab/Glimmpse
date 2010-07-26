package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class BetaPanel extends WizardStepPanel
implements MatrixResizeListener, CovariateListener, DynamicListValidator, SolvingForListener
{
    protected ResizableMatrix betaFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA_FIXED,
    			GlimmpseConstants.DEFAULT_Q, 
    			GlimmpseConstants.DEFAULT_P, "0", "&beta; Fixed"); 
    protected ResizableMatrix betaRandom = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA_RANDOM,
    			1, GlimmpseConstants.DEFAULT_P, 
    			"0", "&beta; Random"); 
    
   	// list of per group sample sizes
	String[] columnNames = { Glimmpse.constants.betaScaleTableColumn() };
    protected DynamicListPanel betaScaleListPanel =
    	new DynamicListPanel(columnNames, this);

	public BetaPanel()
	{
		super(Glimmpse.constants.stepsLeftBeta());
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML("Specify your &beta; Matrix");
        HTML description = new HTML("The &beta; matrix represents your estimated regression coefficients for each cell in your design matrix.  ");

        panel.add(header);
        panel.add(description);
        panel.add(betaFixed);
        panel.add(betaRandom);
		panel.add(betaScaleListPanel);
        betaFixed.addMatrixResizeListener(new MatrixResizeListener() {
			public void onColumns(String name, int newCols)
			{
				betaRandom.setColumnDimension(newCols);	
			}

			public void onRows(String name, int newRows) {}
        });
        // disable the row dimension for beta, since this depends on the design matrix
        betaFixed.setEnabledRowDimension(false);
        betaRandom.setVisible(false);
        // disable both dimensions on the random beta matrix
        betaRandom.setEnabledRowDimension(false);
        betaRandom.setEnabledColumnDimension(false);

        
		initWidget(panel);
	}
	
    public void reset()
    {
    	
    }
    
    
    
    
	public void validate(String value, int column) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseDouble(value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
    	}
	}
	
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
	}
	
	public void addMatrixResizeListener(MatrixResizeListener listener)
	{
		betaFixed.addMatrixResizeListener(listener);
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		if (GlimmpseConstants.MATRIX_DESIGN_FIXED.equals(name))
		{
			betaFixed.setRowDimension(newCols);
		}
		
	}

	@Override
	public void onRows(String name, int newRows)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		betaRandom.setVisible(hasCovariate);		
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
	
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (betaScaleListPanel.isVisible()) buffer.append(betaScaleListPanel.toXML("betaScaleList"));
		buffer.append(betaFixed.toXML());
		if (betaRandom.isVisible())
		{
			buffer.append(betaRandom.toXML());
		}
		return buffer.toString();
	}

	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		betaScaleListPanel.setVisible(solutionType != SolutionType.EFFECT_SIZE);
	}
}
