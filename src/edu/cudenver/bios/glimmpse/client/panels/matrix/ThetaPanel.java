package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class ThetaPanel extends WizardStepPanel
implements MatrixResizeListener
{   
    
    protected ResizableMatrix theta = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_THETA,
    			GlimmpseConstants.DEFAULT_Q, 
    			GlimmpseConstants.DEFAULT_P, "0", "&Theta; (null hypotheses)"); 
    
	public ThetaPanel()
	{
		super(Glimmpse.constants.stepsLeftTheta());
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML("Specify your &Theta; Matrix");
        HTML description = new HTML("The &Theta; matrix represents your null hypotheses. ");

        panel.add(header);
        panel.add(description);
        panel.add(theta);
        
		initWidget(panel);
	}  
	
	public void reset()
	{
		
	}
	
	@Override
	public void onColumns(String name, int newCols)
	{
		if (GlimmpseConstants.MATRIX_WITHIN_CONTRAST.equals(name))
		{
			theta.setColumnDimension(newCols);
		}
		
	}

	@Override
	public void onRows(String name, int newRows)
	{
		if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST_FIXED.equals(name))
		{
			theta.setColumnDimension(newRows);
		}
		
	}
}
