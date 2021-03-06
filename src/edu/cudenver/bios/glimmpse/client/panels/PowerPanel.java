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
package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;

/**
 * Panel for entering nominal power values when performing
 * sample size calculations
 *
 */
public class PowerPanel extends WizardStepPanel
implements ListValidator, SolvingForListener
{
	// list of nominal power values.  Only displayed when solving for effect size or sample size
    protected ListEntryPanel nominalPowerListPanel = 
    	new ListEntryPanel(Glimmpse.constants.solvingForNominalPowerTableColumn(), this);
    
	public PowerPanel()
	{
		super();
		skip=true;
		VerticalPanel panel = new VerticalPanel();

		HTML header = new HTML(Glimmpse.constants.solvingForNominalPowerTitle());
		HTML description = new HTML(Glimmpse.constants.solvingForNominalPowerDescription());

		panel.add(header);
		panel.add(description);
		panel.add(nominalPowerListPanel);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		
		initWidget(panel);
	}
	
	/**
	 * Validate new entries in the alpha list
	 * @see DynamicListValidator
	 */
	public void validate(String value) throws IllegalArgumentException
	{
		try
		{
			TextValidation.parseDouble(value, 0, 1, false);
		}
		catch (NumberFormatException nfe)
		{
			throw new IllegalArgumentException(Glimmpse.constants.errorInvalidPower());
		}
	}

    /**
     * Callback when the number of valid entries in the list of
     * alpha values changes
     * 
     * @see DynamicListValidator
     */
    public void onValidRowCount(int validRowCount)
    {
    	if (validRowCount > 0)
    		notifyComplete();
    	else
    		notifyInProgress();
    }
    
    /**
     * Return an XML representation of the nominal power list, or null if 
     * solving for power
     * 
     * @return XML representation of the nominal power list
     */
    public String toXML()
    {
    	if (skip)
    		return "";
    	else
    		return nominalPowerListPanel.toXML(GlimmpseConstants.TAG_POWER_LIST);
    }
	
	@Override
	public void reset()
	{
		nominalPowerListPanel.reset();
    	onValidRowCount(nominalPowerListPanel.getValidRowCount());
    	skip = true;
	}

	@Override
	public void loadFromNode(Node node)
	{
    	if (GlimmpseConstants.TAG_POWER_LIST.equalsIgnoreCase(node.getNodeName()))
    	{
    		nominalPowerListPanel.loadFromNode(node);
    	}
    	onValidRowCount(nominalPowerListPanel.getValidRowCount());
	}

	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		skip = (solutionType == SolutionType.POWER);
	}

}
