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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;

/**
 * Panel for indicating of a baseline covariate should be included.
 * 
 * @author Sarah Kreidler
 *
 */
public class BaselineCovariatePanel extends WizardStepPanel
{
    protected CheckBox covariateCheckBox = new CheckBox();

    protected ArrayList<CovariateListener> listeners = new ArrayList<CovariateListener>();
    
    public BaselineCovariatePanel()
    {
    	super();
    	// build covariate panel
        VerticalPanel panel = new VerticalPanel();
        
        // create header, description HTML
        HTML header = new HTML(Glimmpse.constants.covariateTitle());
        HTML description = new HTML(Glimmpse.constants.covariateDescription());
        
        // build the checkbox / label to contorl for a covariate
        HorizontalPanel includeCovariatePanel = new HorizontalPanel();
        includeCovariatePanel.add(covariateCheckBox);
        includeCovariatePanel.add(new HTML(Glimmpse.constants.covariateCheckBoxLabel()));
        

        // add handlers 
        covariateCheckBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                // notify listeners
                for(CovariateListener listener : listeners) listener.onHasCovariate(covariateCheckBox.getValue());
            }
        });
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(includeCovariatePanel);
        
        // add style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
  
        initWidget(panel);
    }
    
    public void addCovariateListener(CovariateListener listener)
    {
        listeners.add(listener);
    }
    
    public boolean hasCovariate()
    {
        return covariateCheckBox.getValue();
    }
    
    public boolean isComplete()
    {
    	return true;
    }

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		covariateCheckBox.setValue(false);
	}

	@Override
	public void loadFromNode(Node node)
	{
		
		if (GlimmpseConstants.TAG_ESSENCE_MATRIX.equals(node.getNodeName())) 
		{
			NodeList children = node.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if (GlimmpseConstants.TAG_RANDOM_COLUMN_META_DATA.equals(child.getNodeName()))
				{
					covariateCheckBox.setValue(true);
					for(CovariateListener listener : listeners) listener.onHasCovariate(covariateCheckBox.getValue());
					break;
				}
			}
		}
	}

}
