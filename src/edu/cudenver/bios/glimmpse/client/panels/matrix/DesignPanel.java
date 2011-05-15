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

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.listener.RelativeGroupSizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Matrix mode panel which allows entry of the design essence matrix
 */
public class DesignPanel extends WizardStepPanel
implements CovariateListener
{    	
    protected ResizableMatrix essenceFixed = new ResizableMatrix(GlimmpseConstants.MATRIX_DESIGN,
			GlimmpseConstants.DEFAULT_N, 
			GlimmpseConstants.DEFAULT_Q, "0", Glimmpse.constants.matrixCategoricalEffectsLabel());
    
   	boolean hasCovariate = false;
   	
   	protected ArrayList<RelativeGroupSizeListener> listeners = new ArrayList<RelativeGroupSizeListener>();
   	
	public DesignPanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.matrixDesignTitle());
        HTML description = new HTML(Glimmpse.constants.matrixDesignDescription());
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(essenceFixed);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // add style
        initWidget(panel);
	}
    
	public void reset()
	{
		essenceFixed.reset(GlimmpseConstants.DEFAULT_N, GlimmpseConstants.DEFAULT_Q);
	}
	
	@Override
    public void onHasCovariate(boolean hasCovariate) 
    {
    	this.hasCovariate = hasCovariate;
    }
	
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseInteger(value, 0, Integer.MAX_VALUE);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
    	}
	}
	
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
	}
	
	
	public void addMatrixResizeListener(MatrixResizeListener listener)
	{
		essenceFixed.addMatrixResizeListener(listener);
	}
	
	public String toXML()
	{
		return essenceFixed.toXML(GlimmpseConstants.MATRIX_DESIGN);
	}

	@Override
	public void loadFromNode(Node node)
	{
		essenceFixed.loadFromDomNode(node);
	}

}
