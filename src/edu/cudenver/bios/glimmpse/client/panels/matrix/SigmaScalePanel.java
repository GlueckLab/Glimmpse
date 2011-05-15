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
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.SigmaScaleListener;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Matrix mode panel allowing entry of scale factors for the error covariance
 * 
 * @author Sarah Kreidler
 *
 */
public class SigmaScalePanel extends WizardStepPanel
implements ListValidator
{
    // list of sigma scale factors
    protected ListEntryPanel sigmaScaleListPanel = 
    	new ListEntryPanel(Glimmpse.constants.sigmaScaleTableColumn(), this);
    
    protected ArrayList<SigmaScaleListener> listeners = new ArrayList<SigmaScaleListener>();
    
	public SigmaScalePanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(Glimmpse.constants.sigmaScaleTitle());
        HTML description = new HTML(Glimmpse.constants.sigmaScaleDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(sigmaScaleListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		sigmaScaleListPanel.reset();
	}

	@Override
	public void loadFromNode(Node node)
	{
		sigmaScaleListPanel.loadFromNode(node);
		onValidRowCount(sigmaScaleListPanel.getValidRowCount());
	}

	@Override
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseDouble(value, 0.0, Double.POSITIVE_INFINITY, false);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidNumber());
    	}
	}
	
	@Override
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
	}

	@Override
	public void onExit()
	{
    	List<String> values = sigmaScaleListPanel.getValues();
    	for(SigmaScaleListener listener: listeners) listener.onSigmaScaleList(values);
	}
	
    /**
     * Add a listener for the list of sigma scale values
     * @param listener sigma scale listener object
     */
    public void addSigmaScaleListener(SigmaScaleListener listener)
    {
    	listeners.add(listener);
    }
    
	public String toXML()
	{
		return sigmaScaleListPanel.toXML(GlimmpseConstants.TAG_SIGMA_SCALE_LIST);
	}
    
}
