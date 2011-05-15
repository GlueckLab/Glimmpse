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
 * Matrix mode panel for entering the within-subject contrast matrix, U.
 * 
 * @author Sarah Kreidler
 *
 */
public class WithinSubjectContrastPanel extends WizardStepPanel
implements MatrixResizeListener
{
    protected ResizableMatrix withinSubjectMatrix = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_WITHIN_CONTRAST,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_B, "1", Glimmpse.constants.withinSubjectContrastMatrixName()); 
    
	public WithinSubjectContrastPanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.withinSubjectContrastTitle());
        HTML description = new HTML(Glimmpse.constants.withinSubjectContrastDescription());

        // layout the panel
        panel.add(header);
        panel.add(description);
        panel.add(withinSubjectMatrix);

        // only allow resize of the row dimension of the fixed matrix since this depends on beta
        withinSubjectMatrix.setEnabledRowDimension(false);

        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		withinSubjectMatrix.reset(GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_B);
	}

	@Override
	public void loadFromNode(Node node)
	{
		withinSubjectMatrix.loadFromDomNode(node);
	}

	public void addMatrixResizeListener(MatrixResizeListener listener)
	{
		withinSubjectMatrix.addMatrixResizeListener(listener);
	}
	
	public String toXML()
	{
		return withinSubjectMatrix.toXML();
	}

	@Override
	public void onRows(String name, int newRows)
	{
		// no action needed
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		if (GlimmpseConstants.MATRIX_BETA.equals(name))
		{
			withinSubjectMatrix.setRowDimension(newCols);
			withinSubjectMatrix.notifyOnRows(newCols);
		}	
	}
	
	
}
