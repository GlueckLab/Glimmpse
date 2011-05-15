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
import edu.cudenver.bios.glimmpse.client.listener.AlphaListener;
import edu.cudenver.bios.glimmpse.client.listener.BetaScaleListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Matrix Mode panel which allows entry of beta-scale factors
 *
 */
public class BetaScalePanel extends WizardStepPanel
implements ListValidator, SolvingForListener
{
   	// list of per group sample sizes
    protected ListEntryPanel betaScaleListPanel =
    	new ListEntryPanel(Glimmpse.constants.betaScaleTableColumn(), this);
    
    protected ArrayList<BetaScaleListener> listeners = new ArrayList<BetaScaleListener>();
    
	public BetaScalePanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(Glimmpse.constants.betaScaleTitle());
        HTML description = new HTML(Glimmpse.constants.betaScaleDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		betaScaleListPanel.reset();
	}

	@Override
	public void loadFromNode(Node node)
	{
		betaScaleListPanel.loadFromNode(node);
		onValidRowCount(betaScaleListPanel.getValidRowCount());
	}

	@Override
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseDouble(value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
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
	public void onSolvingFor(SolutionType solutionType)
	{
		switch (solutionType)
		{
		case DETECTABLE_DIFFERENCE:
			skip = true;
			break;
		default:	
			skip = false;
		}
	}
	
    /**
     * Notify alpha listeners as we exit the screen
     */
    @Override
    public void onExit()
    {
    	List<String> values = betaScaleListPanel.getValues();
    	for(BetaScaleListener listener: listeners) listener.onBetaScaleList(values);
    }
	
    /**
     * Add a listener for the list of beta scale values
     * @param listener beta scale listener object
     */
    public void addBetaScaleListener(BetaScaleListener listener)
    {
    	listeners.add(listener);
    }
    
	public String toXML()
	{
		return betaScaleListPanel.toXML(GlimmpseConstants.TAG_BETA_SCALE_LIST);
	}

}
