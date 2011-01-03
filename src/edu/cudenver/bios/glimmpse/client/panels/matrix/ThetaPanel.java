package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class ThetaPanel extends WizardStepPanel
implements MatrixResizeListener
{   
    
    protected ResizableMatrix theta = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_THETA,
    			GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_B, "0", Glimmpse.constants.thetaNullMatrixName()); 
    
	public ThetaPanel()
	{
		super();
		// regardless of user input, this panel allows forward navigation
		complete = true;
		
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.thetaNullTitle());
        HTML description = new HTML(Glimmpse.constants.thetaNullDescription());

        // disabled resizing to ensure matrix conformance
        theta.setEnabledColumnDimension(false);
        theta.setEnabledRowDimension(false);
        
        panel.add(header);
        panel.add(description);
        panel.add(theta);
        
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
	}  
	
	public void reset()
	{
		theta.reset(GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_B);
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
		if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST.equals(name))
		{
			theta.setRowDimension(newRows);
		}
		
	}
	
	
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(theta.toXML());
		return buffer.toString();
	}

	@Override
	public void loadFromNode(Node node)
	{
		theta.loadFromDomNode(node);
	}
}
