package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class SigmaErrorMatrixPanel extends WizardStepPanel
implements CovariateListener, MatrixResizeListener
{
    protected ResizableMatrix sigmaError = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_ERROR,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P, "0", Glimmpse.constants.sigmaErrorMatrixName()); 
    
    public SigmaErrorMatrixPanel()
    {
		super();
		// regardless of input, forward navigation is allowed from this panel
		complete = true;
		
        HTML header = new HTML(Glimmpse.constants.sigmaErrorTitle());
        HTML description = new HTML(Glimmpse.constants.sigmaErrorDescription());
		VerticalPanel panel = new VerticalPanel();
		
        panel.add(header);
        panel.add(description);
		panel.add(sigmaError);        
        
		// disable resize
		sigmaError.setEnabledColumnDimension(false);
		sigmaError.setEnabledRowDimension(false);
		
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
    }
    
	@Override
	public void reset()
	{
		sigmaError.reset(GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P);
	}

	@Override
	public void loadFromNode(Node node)
	{
		sigmaError.loadFromDomNode(node);
	}

	public String toXML()
	{
		if (skip)
			return "";
		else
			return sigmaError.toXML();
	}

	@Override
	public void onRows(String name, int newRows)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = hasCovariate;
	}

}
