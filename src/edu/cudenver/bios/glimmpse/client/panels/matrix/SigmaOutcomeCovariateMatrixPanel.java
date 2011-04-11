package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class SigmaOutcomeCovariateMatrixPanel extends WizardStepPanel
implements CovariateListener, MatrixResizeListener
{
    protected ResizableMatrix sigmaYG = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P, "0", Glimmpse.constants.sigmaOutcomeCovariateMatrixName()); 
    
    public SigmaOutcomeCovariateMatrixPanel()
    {
		super();
		// regardless of input, forward navigation is allowed from this panel
		complete = true;
		skip = true;
        HTML header = new HTML(Glimmpse.constants.sigmaOutcomeCovariateTitle());
        HTML description = new HTML(Glimmpse.constants.sigmaOutcomeCovariateDescription());
		VerticalPanel panel = new VerticalPanel();
		
        panel.add(header);
        panel.add(description);
		panel.add(sigmaYG);        
        
		// disable resize
		sigmaYG.setEnabledColumnDimension(false);
		sigmaYG.setEnabledRowDimension(false);
		
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
    }
    
	@Override
	public void reset()
	{
		sigmaYG.reset(GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P);
	}

	@Override
	public void loadFromNode(Node node)
	{
		sigmaYG.loadFromDomNode(node);
	}
	
	public String toXML()
	{
		if (skip)
			return "";
		else
			return sigmaYG.toXML(GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE);
	}

	@Override
	public void onRows(String name, int newRows)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		// resize when beta columns change
		if (GlimmpseConstants.MATRIX_BETA.equals(name))
		{
			sigmaYG.setRowDimension(newCols);
		}
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = !hasCovariate;
	}

}
