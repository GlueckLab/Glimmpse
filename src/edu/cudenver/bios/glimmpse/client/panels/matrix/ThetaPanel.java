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
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Matrix mode panel for entering the null hypothesis matrix (theta null)
 * @author Sarah
 *
 */
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
