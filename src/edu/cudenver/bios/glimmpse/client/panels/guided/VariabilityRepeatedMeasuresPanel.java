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

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.ListUtilities;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class VariabilityRepeatedMeasuresPanel extends WizardStepPanel
implements OutcomesListener
{
	public VariabilityRepeatedMeasuresPanel()
	{
		super();
		skip = true;
		VerticalPanel panel = new VerticalPanel();
		
		initWidget(panel);
	}
	
	@Override
	public void reset()
	{
		skip = true;
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		// TODO Auto-generated method stub

	}

	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		return buffer.toString();
	}
}
