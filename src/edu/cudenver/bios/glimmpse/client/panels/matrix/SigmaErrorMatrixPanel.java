/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Matrix mode panel for entering the covariance of random errors
 * 
 * @author Sarah Kreidler
 *
 */
public class SigmaErrorMatrixPanel extends WizardStepPanel
implements CovariateListener, MatrixResizeListener
{
    protected ResizableMatrix sigmaError = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_ERROR,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P,
    			"0",
    			Glimmpse.constants.sigmaErrorMatrixName(),
    			true); 
    
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
		// nothin' to do here, folks.
		
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		// resize when beta columns change
		if (GlimmpseConstants.MATRIX_BETA.equals(name))
		{
			sigmaError.setRowDimension(newCols);
		}
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = hasCovariate;
	}

}
