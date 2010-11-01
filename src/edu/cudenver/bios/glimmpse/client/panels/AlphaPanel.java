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
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;

/**
 * Panel for entering type I error values
 * 
 * @author Sarah Kreidler
 *
 */
public class AlphaPanel extends WizardStepPanel
implements ListValidator
{
	private static final String TAG_ALPHA_LIST = "alphaList";
    // list of alpha values
    protected ListEntryPanel alphaListPanel = 
    	new ListEntryPanel(Glimmpse.constants.alphaTableColumn() , this);

    /**
     * Create an empty type I error panel
     */
    public AlphaPanel()
    {
    	super();
        VerticalPanel panel = new VerticalPanel();

        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.alphaTitle());
        HTML description = new HTML(Glimmpse.constants.alphaDescription());

        // layout the panels
        panel.add(header);
        panel.add(description);
        panel.add(alphaListPanel);
        
        // specify the maximum rows in the listbox
        alphaListPanel.setMaxRows(5);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        // initialize the panel
        initWidget(panel);
    }
    
    /**
     * Return an XML representation of the alpha list
     * @return XML representation of the alpha list
     */
    public String toXML()
    {
    	return alphaListPanel.toXML(TAG_ALPHA_LIST);
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
    	alphaListPanel.reset();
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
    	if (TAG_ALPHA_LIST.equals(node.getNodeName()))
    	{
    		alphaListPanel.loadFromNode(node);
    	}
    	onValidRowCount(alphaListPanel.getValidRowCount());
    }
}
