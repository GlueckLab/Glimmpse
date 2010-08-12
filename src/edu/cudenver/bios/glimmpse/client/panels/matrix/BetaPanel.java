package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class BetaPanel extends WizardStepPanel
implements MatrixResizeListener, CovariateListener, ListValidator, SolvingForListener
{
    protected ResizableMatrix betaFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA,
    			GlimmpseConstants.DEFAULT_Q, 
    			GlimmpseConstants.DEFAULT_P, "0", Glimmpse.constants.matrixBetaFixedMatrixName()); 
    protected ResizableMatrix betaRandom = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA_RANDOM,
    			1, GlimmpseConstants.DEFAULT_P, 
    			"0", Glimmpse.constants.matrixBetaGaussianMatrixName()); 
    
   	// list of per group sample sizes
    protected ListEntryPanel betaScaleListPanel =
    	new ListEntryPanel(Glimmpse.constants.matrixBetaScaleTableColumn(), this);

	public BetaPanel()
	{
		super(Glimmpse.constants.stepsLeftBeta());
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.matrixBetaTitle());
        HTML description = new HTML(Glimmpse.constants.matrixBetaDescription());

        // layout the panel
        panel.add(header);
        panel.add(description);
        panel.add(createBetaMatrixPanel());
        panel.add(createBetaScalePanel());
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
	}
	
	private VerticalPanel createBetaMatrixPanel()
	{
    	VerticalPanel panel = new VerticalPanel();
    	
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
    	
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
	}
	
    private VerticalPanel createBetaScalePanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
        HTML header = new HTML(Glimmpse.constants.matrixBetaScalingTitle());
        HTML description = new HTML(Glimmpse.constants.matrixBetaScalingDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);

    	// add style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    	    	
    	return panel;
    }
	
	
    public void reset()
    {
    	
    }
    
    
    
    
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
		buffer.append("<fixedRandomMatrix name='beta' combineHorizontal='false' >");
		buffer.append(betaFixed.toXML(GlimmpseConstants.MATRIX_FIXED));
		if (betaRandom.isVisible())
		{
			buffer.append(betaRandom.toXML(GlimmpseConstants.MATRIX_RANDOM));
		}
		buffer.append("</fixedRandomMatrix>");

		return buffer.toString();
	}

	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		betaScaleListPanel.setVisible(solutionType != SolutionType.EFFECT_SIZE);
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
}
