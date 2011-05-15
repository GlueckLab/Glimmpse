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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Matrix mode panel which allows input of the between-subject contrast matrix (C)
 */
public class BetweenSubjectContrastPanel extends WizardStepPanel
implements MatrixResizeListener, CovariateListener
{
    protected ResizableMatrix betweenSubjectFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST,
    			GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_Q, "0", Glimmpse.constants.betweenSubjectContrastMatrixName()); 
    protected boolean hasCovariate = false;
    
	public BetweenSubjectContrastPanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		betweenSubjectFixed.setMaxRows(GlimmpseConstants.DEFAULT_A);
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.betweenSubjectContrastTitle());
        HTML description = new HTML(Glimmpse.constants.betweenSubjectContrastDescription());

        // layout the panel
        panel.add(header);
        panel.add(description);
        panel.add(betweenSubjectFixed);

        // only allow resize of the row dimension of the fixed matrix since this depends on beta
        betweenSubjectFixed.setEnabledColumnDimension(false);

        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		betweenSubjectFixed.reset(GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_Q);
	}

	@Override
	public void loadFromNode(Node node)
	{
		if (GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX.equalsIgnoreCase(node.getNodeName()))
		{
			NodeList children = node.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				String childName = child.getNodeName();
				if (GlimmpseConstants.TAG_MATRIX.equals(childName))
				{
					NamedNodeMap attrs = child.getAttributes();
					Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
					if (nameNode != null)
					{
						if (GlimmpseConstants.MATRIX_FIXED.equals(nameNode.getNodeValue()))
						{
							betweenSubjectFixed.loadFromDomNode(child);
						}
					}
				}
			}
		}
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		if (GlimmpseConstants.MATRIX_DESIGN.equals(name))
		{
			betweenSubjectFixed.setColumnDimension(newCols);
			betweenSubjectFixed.notifyOnColumns(newCols);
		}
	}

	@Override
	public void onRows(String name, int newRows)
	{
		if (GlimmpseConstants.MATRIX_DESIGN.equals(name))
		{
			betweenSubjectFixed.setMaxRows(newRows - 1);
			if (betweenSubjectFixed.getRowDimension() > newRows - 1)
			{
				betweenSubjectFixed.setRowDimension(newRows - 1);
				betweenSubjectFixed.notifyOnRows(newRows-1);
			}
		}
		else if (GlimmpseConstants.MATRIX_BETA.equals(name))
		{
			betweenSubjectFixed.setColumnDimension(newRows);
			betweenSubjectFixed.notifyOnColumns(newRows);
		}
	}
	
	public void addMatrixResizeListener(MatrixResizeListener listener)
	{
		betweenSubjectFixed.addMatrixResizeListener(listener);
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}

	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<fixedRandomMatrix name='betweenSubjectContrast' combineHorizontal='true' >");
		buffer.append(betweenSubjectFixed.toXML(GlimmpseConstants.MATRIX_FIXED));
		if (hasCovariate)
		{
			int rows = betweenSubjectFixed.getRowDimension();
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, 1);
			for(int i = 0; i < rows; i++) buffer.append("<r><c>0</c></r>");
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		buffer.append("</fixedRandomMatrix>");
		return buffer.toString();
	}
}
