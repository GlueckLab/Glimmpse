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
package edu.cudenver.bios.glimmpse.client.panels.guided;

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
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Panel for entering type I error values
 * 
 * @author Sarah Kreidler
 *
 */
public class SimpleAlphaPanel extends WizardStepPanel
implements ListValidator, ClickHandler
{
	protected CheckBox alpha05CheckBox = new CheckBox();
	protected CheckBox alpha01CheckBox = new CheckBox();
	protected CheckBox alpha10CheckBox = new CheckBox();  
    
    /**
     * Create an empty type I error panel
     */
    public SimpleAlphaPanel()
    {
    	super();
        VerticalPanel panel = new VerticalPanel();

        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.simpleAlphaTitle());
        HTML description = new HTML(Glimmpse.constants.simpleAlphaDescription());

        // build the selection check boxes
        Grid grid = new Grid(3,2);
        grid.setWidget(0, 0, alpha01CheckBox);
        grid.setWidget(0, 1, new HTML("0.01"));
        grid.setWidget(1, 0, alpha05CheckBox);
        grid.setWidget(1, 1, new HTML("0.05"));
        grid.setWidget(2, 0, alpha10CheckBox);
        grid.setWidget(2, 1, new HTML("0.10"));
      
        // add click callbacks
        alpha05CheckBox.addClickHandler(this);
        alpha01CheckBox.addClickHandler(this);
        alpha10CheckBox.addClickHandler(this);

        // layout the panels
        panel.add(header);
        panel.add(description);
    	panel.add(grid);
        
        // set style
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        // initialize the panel
        initWidget(panel);
    }
    
    /**
     * Return an XML representation of the alpha list
     * @return XML representation of the alpha list
     */
    public String toXML()
    {
    	StringBuffer buffer = new StringBuffer();
    	XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ALPHA_LIST);
    	if (alpha05CheckBox.getValue())
    	{
        	buffer.append("<c>0.05</c>");
    	}
    	else if (alpha01CheckBox.getValue())
    	{
        	buffer.append("<c>0.01</c>");
    	}
    	else if (alpha10CheckBox.getValue())
    	{
        	buffer.append("<c>0.10</c>");
    	}
    	XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ALPHA_LIST);
    	return buffer.toString();
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
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
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
     * Clear the list of alpha values.  Note, the onValidRowCount
     * callback will fire when reset is called
     */
    public void reset()
    {
    	alpha05CheckBox.setValue(false);
    	alpha01CheckBox.setValue(false);
    	alpha10CheckBox.setValue(false);
    	notifyInProgress();
    }

    /**
     * Load the alpha panel from an "alphaList" Dom node
     * 
     * @param node "alphalist" node
     */
    @Override
    public void loadFromNode(Node node)
    {
    	if (GlimmpseConstants.TAG_ALPHA_LIST.equals(node.getNodeName()))
    	{
    		NodeList children = node.getChildNodes();
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node child = children.item(i);
    			Node valueNode = child.getFirstChild();
    			if (valueNode != null)
    			{
    				try
    				{
    					double alpha = Double.parseDouble(valueNode.getNodeValue());
    					if (alpha == 0.05)
    						alpha05CheckBox.setValue(true);
    					else if (alpha == 0.01)
    						alpha01CheckBox.setValue(true);
    					else if (alpha == 0.10)
    						alpha10CheckBox.setValue(true);
    				}
    				catch (Exception e)
    				{
    					// ignore parse errors
    				}
    			}
    		}
    	}
    }

	@Override
	public void onClick(ClickEvent event)
	{
		if (alpha05CheckBox.getValue() || 
				alpha01CheckBox.getValue() ||
				alpha10CheckBox.getValue())
		{
			notifyComplete();
		}
		else
		{
			notifyInProgress();
		}
	}
}
