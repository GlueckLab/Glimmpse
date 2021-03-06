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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.BetaScaleListener;
import edu.cudenver.bios.glimmpse.client.listener.SigmaScaleListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Guided mode equivalent of sigma scale panel
 * @author Sarah Kreidler
 *
 */
public class VariabilityScalePanel extends WizardStepPanel
{
    protected CheckBox scaleCheckBox = new CheckBox();
    protected ArrayList<SigmaScaleListener> listeners = new ArrayList<SigmaScaleListener>();
    
	public VariabilityScalePanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.variabilityScaleTitle());
        HTML description = new HTML(Glimmpse.constants.variabilityScaleDescription());
        // create the beta scale checkbox - asks if the user wants to test 0.5,1,and 2 times the estimated
        // mean difference
        HorizontalPanel checkBoxContainer = new HorizontalPanel();
        checkBoxContainer.add(scaleCheckBox);
        checkBoxContainer.add(new HTML(Glimmpse.constants.variabilityScaleAnswer()));

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(new HTML(Glimmpse.constants.variabilityScaleQuestion()));
        panel.add(checkBoxContainer);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		initWidget(panel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reset()
	{
		scaleCheckBox.setValue(false);
	}

	@Override
	public void loadFromNode(Node node)
	{
		if (GlimmpseConstants.TAG_SIGMA_SCALE_LIST.equalsIgnoreCase(node.getNodeName()))
		{
			NodeList children = node.getChildNodes();
			scaleCheckBox.setValue(children.getLength() > 1);
		}
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_SIGMA_SCALE_LIST);
		buffer.append("<v>1</v>");
		if (scaleCheckBox.getValue())
		{
			buffer.append("<v>0.5</v><v>2</v>");
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_SIGMA_SCALE_LIST);
		return buffer.toString();
	}
	
	public String toStudyXML()
	{
		return toRequestXML();
	}
	
	@Override
	public void onExit()
	{
    	ArrayList<String> values = new ArrayList<String>();
    	values.add("1");
		if (scaleCheckBox.getValue())
		{
			values.add("0.5");
			values.add("2");
		}
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

}
