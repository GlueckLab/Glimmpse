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
implements MatrixResizeListener, CovariateListener
{
    protected ResizableMatrix betaFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA,
    			GlimmpseConstants.DEFAULT_Q, 
    			GlimmpseConstants.DEFAULT_P, "0", Glimmpse.constants.betaFixedMatrixName()); 
    protected ResizableMatrix betaRandom = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA_RANDOM,
    			1, GlimmpseConstants.DEFAULT_P, 
    			"0", Glimmpse.constants.betaGaussianMatrixName()); 
    
    boolean hasCovariate;
    
	public BetaPanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.betaTitle());
        HTML description = new HTML(Glimmpse.constants.betaDescription());

        // layout the panel
        panel.add(header);
        panel.add(description);
        panel.add(createBetaMatrixPanel());
        
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
		
    public void reset()
    {
    	betaFixed.reset(); // TODO
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
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
}
