package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class BetweenSubjectContrastPanel extends WizardStepPanel
implements MatrixResizeListener, CovariateListener
{
    protected ResizableMatrix betweenSubjectFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST,
    			GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_Q, "0", "Between Subject (C)<br>Fixed"); 
    protected boolean hasCovariate = false;
    
	public BetweenSubjectContrastPanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML("Between Subject Contrast (C)");
        HTML description = new HTML("Specify your between subject contrast");

        // layout the panel
        panel.add(header);
        panel.add(description);
        panel.add(betweenSubjectFixed);

        // only allow resize of the row dimension of the fixed matrix since this depends on beta
        betweenSubjectFixed.setEnabledColumnDimension(false);

        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		betweenSubjectFixed.reset(GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_Q);
	}

	@Override
	public void loadFromNode(Node node)
	{
		betweenSubjectFixed.loadFromDomNode(node);
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		if (GlimmpseConstants.MATRIX_DESIGN_FIXED.equals(name))
		{
			betweenSubjectFixed.setColumnDimension(newCols);
			betweenSubjectFixed.notifyOnColumns(newCols);
		}
	}

	@Override
	public void onRows(String name, int newRows)
	{
		if (GlimmpseConstants.MATRIX_DESIGN_FIXED.equals(name))
		{
			if (betweenSubjectFixed.getRowDimension() > newRows - 1)
			{
				betweenSubjectFixed.setRowDimension(newRows - 1);
				betweenSubjectFixed.notifyOnRows(newRows-1);
			}
		}		
	}
	
	public void addMatrixResizeListener(MatrixResizeListener listener)
	{
		betweenSubjectFixed.addMatrixResizeListener(listener);
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}

	@Override
	public void onMean(double mean)
	{
		// n/a
		
	}

	@Override
	public void onVariance(double variance)
	{
		// n/a
		
	}

	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<fixedRandomMatrix name='betweenSubjectContrast' combineHorizontal='true' >");
		buffer.append(betweenSubjectFixed.toXML(GlimmpseConstants.MATRIX_FIXED));
		if (hasCovariate)
		{
			int rows = betweenSubjectFixed.getRowDimension();
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, 1);
			for(int i = 0; i < rows; i++) buffer.append("<r><c>1</c></r>");
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		buffer.append("</fixedRandomMatrix>");
		return buffer.toString();
	}
}
