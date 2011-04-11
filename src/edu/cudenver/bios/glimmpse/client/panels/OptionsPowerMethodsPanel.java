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


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.PowerMethodListener;
import edu.cudenver.bios.glimmpse.client.listener.QuantileListener;

/**
 * Panel which allows user to select statistical tests, display options
 * for their power/sample size calculations.  Note that two instances of this 
 * class are created (one for matrix mode, one for guided mode) so any
 * radio groups or other unique identifiers must have a mode-specific prefix
 * 
 * @author Sarah Kreidler
 *
 */
public class OptionsPowerMethodsPanel extends WizardStepPanel
implements CovariateListener, ClickHandler
{
	// check boxes for power methods (only used when a baseline covariate is specified)
	protected CheckBox unconditionalPowerCheckBox = new CheckBox();
	protected CheckBox quantilePowerCheckBox = new CheckBox();
	protected int numQuantiles = 0;
	
	// listeners for power methods
	protected ArrayList<PowerMethodListener> powerMethodListeners = 
		new ArrayList<PowerMethodListener>();
	// listeners for quantile list changes
	protected ArrayList<QuantileListener> quantileListeners = new ArrayList<QuantileListener>();
	
	// dynamic list of quantile values
    protected ListEntryPanel quantileListPanel = 
    	new ListEntryPanel(Glimmpse.constants.quantilesTableColumn(), new ListValidator() {
			@Override
			public void onValidRowCount(int validRowCount)
			{
				numQuantiles = validRowCount;
				checkComplete();
			}
			@Override
			public void validate(String value)
					throws IllegalArgumentException
			{
		    	try
		    	{
		    		TextValidation.parseDouble(value, 0, 1, false);
		    	}
		    	catch (NumberFormatException nfe)
		    	{
		    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidQuantile());
		    	}
			}
    	});

    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsPowerMethodsPanel(String mode)
	{
		super();
		skip = true;
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(Glimmpse.constants.powerMethodTitle());
		HTML description = new HTML(Glimmpse.constants.powerMethodDescription());        

		// list of power methods
		Grid grid = new Grid(3,2);
		grid.setWidget(0, 0, unconditionalPowerCheckBox);
		grid.setWidget(0, 1, new HTML(Glimmpse.constants.powerMethodUnconditionalLabel()));
		grid.setWidget(1, 0, quantilePowerCheckBox);
		grid.setWidget(1, 1, new HTML(Glimmpse.constants.powerMethodQuantileLabel()));
		grid.setWidget(2, 1, quantileListPanel);
		
		// only show quantile list when quantile power is selected
		quantilePowerCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				quantileListPanel.setVisible(quantilePowerCheckBox.getValue());
			}
		});
		quantileListPanel.setVisible(false);
		
		// add callback to check if screen is complete
		unconditionalPowerCheckBox.addClickHandler(this);
		quantilePowerCheckBox.addClickHandler(this);

		// layout the overall panel
		panel.add(header);
		panel.add(description);
		panel.add(grid);
		
		// set defaults
		reset();

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		// initialize
		initWidget(panel);
	}

	/**
	 * Clear the options panel
	 */
	public void reset()
	{
		// set the power method to conditional
		unconditionalPowerCheckBox.setValue(false);
		quantilePowerCheckBox.setValue(false);
		quantileListPanel.setVisible(false);
		numQuantiles = 0;
		quantileListPanel.reset();
		
		checkComplete();
	}

	/**
	 * Displays the power method selection panel if the study design
	 * includes a baseline covariate
	 * 
	 * @param hasCovariate indicates if the user is controlling for a covariate
	 */
	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = !hasCovariate;
	}

	/**
	 * Create an XML representation of the list of selected power methods
	 * 
	 * @return XML representation of the power methods
	 */
	public String powerMethodListToXML()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_POWER_METHOD_LIST);
		buffer.append(">");
		if (skip)
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.POWER_METHOD_CONDITIONAL);
			buffer.append("</v>");
		}
		else
		{
			if (unconditionalPowerCheckBox.getValue())
			{
				buffer.append("<v>");
				buffer.append(GlimmpseConstants.POWER_METHOD_UNCONDITIONAL);
				buffer.append("</v>");
			}
			if (quantilePowerCheckBox.getValue())
			{
				buffer.append("<v>");
				buffer.append(GlimmpseConstants.POWER_METHOD_QUANTILE);
				buffer.append("</v>");
			}
		}
		buffer.append("</");
		buffer.append(GlimmpseConstants.TAG_POWER_METHOD_LIST);
		buffer.append(">");
		return buffer.toString();
	}


	/**
	 * Create an XML representation of the panel to be saved with
	 * the study design
	 * 
	 * @return study XML
	 */
	public String toStudyXML()
	{
		return toRequestXML();
	}
	
	/**
	 * Create an XML representation of the panel for sending to the
	 * Power web service
	 * 
	 * @return
	 */
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(powerMethodListToXML());
		buffer.append(quantileListPanel.toXML(GlimmpseConstants.TAG_QUANTILE_LIST));
		return buffer.toString();
	}
	
	/**
	 * Click handler for all checkboxes on the Options screen.
	 * Determines if the current selections represent a complete
	 * set of options.
	 */
	@Override
	public void onClick(ClickEvent event)
	{
		checkComplete();			
	}
	
	/**
	 * Check if the user has selected a complete set of options, and
	 * if so notify that forward navigation is allowed
	 */
	private void checkComplete()
	{
		// check if continue is allowed
		// must have at least one test checked, at least one power method
		if (unconditionalPowerCheckBox.getValue() || 
				quantilePowerCheckBox.getValue())
		{
			if (!quantilePowerCheckBox.getValue() || numQuantiles > 0)
			{
				notifyComplete();
			}
			else
			{
				notifyInProgress();
			}
		}
		else
		{
			notifyInProgress();
		}
	}

	/**
	 * Parse the saved study design information and set the appropriate options
	 */
	@Override
	public void loadFromNode(Node node)
	{
		if (GlimmpseConstants.TAG_POWER_METHOD_LIST.equalsIgnoreCase(node.getNodeName()))
		{
			unconditionalPowerCheckBox.setValue(false);
			quantilePowerCheckBox.setValue(false);
			
			NodeList pmChildren = node.getChildNodes();
			for(int pmi = 0; pmi < pmChildren.getLength(); pmi++)
			{
				Node pmChild = pmChildren.item(pmi);
				Node pmNode = pmChild.getFirstChild();
				if (pmNode != null)
				{
					if (GlimmpseConstants.POWER_METHOD_UNCONDITIONAL.equals(pmNode.getNodeValue()))
						unconditionalPowerCheckBox.setValue(true);
					else if (GlimmpseConstants.POWER_METHOD_QUANTILE.equals(pmNode.getNodeValue()))
					{
						quantilePowerCheckBox.setValue(true);
						quantileListPanel.setVisible(true);
					}
				}
			}
		}
		else if (GlimmpseConstants.TAG_QUANTILE_LIST.equalsIgnoreCase(node.getNodeName()))
		{
			quantileListPanel.loadFromNode(node);
			numQuantiles = quantileListPanel.getValidRowCount();
		}

		// check if the options are complete
		checkComplete();
	}
	
    /**
     * Add a listener for the list of power methods
     * @param listener power method listener object
     */
    public void addPowerMethodListener(PowerMethodListener listener)
    {
    	powerMethodListeners.add(listener);
    }
    
    /**
     * Add a listener for the list of quantiles
     * @param listener quantile listener object
     */
    public void addQuantileListener(QuantileListener listener)
    {
    	quantileListeners.add(listener);
    }
	
    /**
     * Notify power method and quantile listeners of any changes
     * as we leave this screen
     */
    @Override
    public void onExit()
    {
    	ArrayList<String> powerMethods = new ArrayList<String>();
    	
    	if (unconditionalPowerCheckBox.getValue())
    		powerMethods.add(GlimmpseConstants.POWER_METHOD_UNCONDITIONAL);
    	if (quantilePowerCheckBox.getValue())
    	{
    		powerMethods.add(GlimmpseConstants.POWER_METHOD_QUANTILE);
    		List<String> values = quantileListPanel.getValues();
    		for(QuantileListener listener: quantileListeners) listener.onQuantileList(values);
    	}
    	
		for(PowerMethodListener listener: powerMethodListeners) listener.onPowerMethodList(powerMethods);
    }
}
