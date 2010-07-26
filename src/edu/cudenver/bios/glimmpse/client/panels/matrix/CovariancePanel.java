package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class CovariancePanel extends WizardStepPanel
implements CovariateListener, MatrixResizeListener
{
	private static final int FIXED_INDEX = 0;
	private static final int RANDOM_INDEX = 0;
	
    protected ResizableMatrix sigmaError = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_ERROR,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P, "0", "&Sigma; Error"); 
    
    protected ResizableMatrix sigmaY = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_OUTCOME,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P, "0", "&Sigma; Y (Outcomes)"); 
    protected ResizableMatrix sigmaYG = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P, "0", "&Sigma; YG (Outcomes)"); 
    protected ResizableMatrix sigmaG = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_OUTCOME,
    			1, 1, "0", "&Sigma; G (Covariate)"); 
    
	protected DeckPanel deckPanel = new DeckPanel(); 
	
	public CovariancePanel()
	{
		super(Glimmpse.constants.stepsLeftSigma());
		// regardless of input, forward navigation is allowed from this panel
		complete = true;
		
		VerticalPanel panel = new VerticalPanel();
		
        HTML header = new HTML("Variability");
        HTML description = new HTML("Enter the covariance matrices below");

        deckPanel.add(buildFixedCovariancePanel());
        deckPanel.add(buildRandomCovariancePanel());
        panel.add(header);
        panel.add(description);
        panel.add(deckPanel);
        deckPanel.showWidget(FIXED_INDEX);
        
        // set all matrices as square / symmetric
        sigmaError.setIsSquare(true, true);
        sigmaY.setIsSquare(true, true);
        sigmaYG.setIsSquare(true, true);
        sigmaG.setIsSquare(true, true);

        // disable resizing of these matrices since this is dependent on beta
        sigmaError.setEnabledColumnDimension(false);
        sigmaError.setEnabledRowDimension(false);
        sigmaY.setEnabledColumnDimension(false);
        sigmaY.setEnabledRowDimension(false);
        sigmaYG.setEnabledColumnDimension(false);
        sigmaYG.setEnabledRowDimension(false);
        sigmaG.setEnabledColumnDimension(false);
        sigmaG.setEnabledRowDimension(false);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
	}

	private VerticalPanel buildFixedCovariancePanel()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(sigmaError);
		return panel;
	}
	
	private VerticalPanel buildRandomCovariancePanel()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(sigmaG);
		panel.add(sigmaY);
		panel.add(sigmaYG);
		return panel;
	}
	
    public void reset()
    {
    }

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
    	if (hasCovariate)
    		deckPanel.showWidget(RANDOM_INDEX);
    	else
    		deckPanel.showWidget(FIXED_INDEX);
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
		if (GlimmpseConstants.MATRIX_BETA_FIXED.equals(name))
		{
			sigmaError.setRowDimension(newCols);
		}		
	}

	@Override
	public void onRows(String name, int newRows)
	{
		// TODO Auto-generated method stub
		
	}
	
	
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (deckPanel.getVisibleWidget() == FIXED_INDEX)
		{
			buffer.append(sigmaError.toXML());
		}
		else
		{
			buffer.append(sigmaG.toXML());
			buffer.append(sigmaYG.toXML());
			buffer.append(sigmaY.toXML());
		}
		return buffer.toString();
	}
}
