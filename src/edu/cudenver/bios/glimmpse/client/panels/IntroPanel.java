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

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;

/**
 * Generic panel for subsection introduction screens
 * @author Sarah Kreidler
 *
 */
public class IntroPanel extends WizardStepPanel
{

	public IntroPanel(String title, String description)
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
		HTML titleHTML = new HTML(title);
		titleHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		panel.add(titleHTML);
		
		HTML pgHTML = new HTML(description);
		pgHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.add(pgHTML);
		
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		initWidget(panel);
	}

	@Override
	public void reset()
	{
		// static page, no resetting to do
	}

	@Override
	public void loadFromNode(Node node)
	{
		// static page, no loading to do
	}

}
