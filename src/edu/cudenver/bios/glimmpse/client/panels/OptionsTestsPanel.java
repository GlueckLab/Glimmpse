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
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;

public class OptionsTestsPanel extends WizardStepPanel
implements ClickHandler, CovariateListener
{
	// constants for xml parsing
    private static final String TAG_TEST_LIST = "testList";
	
	// check boxes for statistical tests
	protected CheckBox hotellingLawleyCheckBox = new CheckBox();
	protected CheckBox pillaiBartlettCheckBox = new CheckBox();
	protected CheckBox wilksCheckBox = new CheckBox();
	protected CheckBox unirepCheckBox = new CheckBox();
	protected CheckBox unirepGGCheckBox = new CheckBox();
	protected CheckBox unirepHFCheckBox = new CheckBox();
	protected CheckBox unirepBoxCheckBox = new CheckBox();

	protected boolean hasCovariate = false;
	
    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsTestsPanel(String mode)
	{
		super();
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(Glimmpse.constants.testTitle());
		HTML description = new HTML(Glimmpse.constants.testDescription());        

		// list of available tests
		Grid grid = new Grid(7,2);
		grid.setWidget(0, 0, unirepCheckBox);
		grid.setWidget(0, 1, new HTML(Glimmpse.constants.testUnirepLabel()));
		grid.setWidget(1, 0, unirepGGCheckBox);
		grid.setWidget(1, 1, new HTML(Glimmpse.constants.testUnirepGeisserGreenhouseLabel()));
		grid.setWidget(2, 0, unirepHFCheckBox);
		grid.setWidget(2, 1, new HTML(Glimmpse.constants.testUnirepHuynhFeldtLabel()));
		grid.setWidget(3, 0, unirepBoxCheckBox);
		grid.setWidget(3, 1, new HTML(Glimmpse.constants.testUnirepBoxLabel()));
		grid.setWidget(4, 0, hotellingLawleyCheckBox);
		grid.setWidget(4, 1, new HTML(Glimmpse.constants.testHotellingLawleyTraceLabel()));
		grid.setWidget(5, 0, pillaiBartlettCheckBox);
		grid.setWidget(5, 1, new HTML(Glimmpse.constants.testPillaiBartlettTraceLabel()));
		grid.setWidget(6, 0, wilksCheckBox);
		grid.setWidget(6, 1, new HTML(Glimmpse.constants.testWilksLambdaLabel()));
		
		// add callback to check if screen is complete
		unirepCheckBox.addClickHandler(this);
		unirepGGCheckBox.addClickHandler(this);
		unirepHFCheckBox.addClickHandler(this);
		unirepBoxCheckBox.addClickHandler(this);
		hotellingLawleyCheckBox.addClickHandler(this);
		pillaiBartlettCheckBox.addClickHandler(this);
		wilksCheckBox.addClickHandler(this);
		// set defaults
		reset();
		
		// layout the overall panel
		panel.add(header);
		panel.add(description);
		panel.add(grid);
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
		// clear the statistical tests
		hotellingLawleyCheckBox.setValue(false);
		pillaiBartlettCheckBox.setValue(false);
		wilksCheckBox.setValue(false);
		unirepCheckBox.setValue(false);
		unirepGGCheckBox.setValue(false);
		unirepHFCheckBox.setValue(false);
		unirepBoxCheckBox.setValue(false);
		
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
		pillaiBartlettCheckBox.setEnabled(!hasCovariate);
		wilksCheckBox.setEnabled(!hasCovariate);
		// TODO: make the text look disabled
	}

	/**
	 * No action required by this panel for onMean callback from CovariateListener
	 */
	@Override
	public void onMean(double mean)
	{
		// no action
	}

	/**
	 * No action required by this panel for onVariance callback from CovariateListener
	 */
	@Override
	public void onVariance(double variance)
	{
		// no action
	}

	/**
	 * Create an XML representation of the currently selected 
	 * statistical test list
	 * 
	 * @return XML representation of statistical test list
	 */
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("<");
		buffer.append(TAG_TEST_LIST);
		buffer.append(">");
		if (hotellingLawleyCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_HOTELLING_LAWLEY_TRACE);
			buffer.append("</v>");
		}
		if (pillaiBartlettCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_PILLAI_BARTLETT_TRACE);
			buffer.append("</v>");
		}
		if (wilksCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_WILKS_LAMBDA);
			buffer.append("</v>");
		}
		if (unirepCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP);
			buffer.append("</v>");
		}
		if (unirepGGCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP_GEISSER_GRENNHOUSE);
			buffer.append("</v>");
		}
		if (unirepHFCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP_HUYNH_FELDT);
			buffer.append("</v>");
		}
		if (unirepBoxCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP_BOX);
			buffer.append("</v>");
		}
		buffer.append("</");
		buffer.append(TAG_TEST_LIST);
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
		if ((hotellingLawleyCheckBox.getValue() || wilksCheckBox.getValue() || 
				pillaiBartlettCheckBox.getValue() || unirepCheckBox.getValue() ||
				unirepGGCheckBox.getValue() || unirepHFCheckBox.getValue() ||
				unirepBoxCheckBox.getValue()))
		{
			notifyComplete();
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
		if (TAG_TEST_LIST.equals(node.getNodeName()))
		{
			NodeList testChildren = node.getChildNodes();
			for(int ti = 0; ti < testChildren.getLength(); ti++)
			{
				Node testChild = testChildren.item(ti);
				Node testNode = testChild.getFirstChild();
				if (testNode != null)
				{
					if (GlimmpseConstants.TEST_HOTELLING_LAWLEY_TRACE.equals(testNode.getNodeValue()))
						hotellingLawleyCheckBox.setValue(true);
					else if (GlimmpseConstants.TEST_PILLAI_BARTLETT_TRACE.equals(testNode.getNodeValue()))
						pillaiBartlettCheckBox.setValue(true);
					else if (GlimmpseConstants.TEST_WILKS_LAMBDA.equals(testNode.getNodeValue()))
						wilksCheckBox.setValue(true);
					else if (GlimmpseConstants.TEST_UNIREP.equals(testNode.getNodeValue()))
						unirepCheckBox.setValue(true);
					else if (GlimmpseConstants.TEST_UNIREP_BOX.equals(testNode.getNodeValue()))
						unirepBoxCheckBox.setValue(true);
					else if (GlimmpseConstants.TEST_UNIREP_GEISSER_GRENNHOUSE.equals(testNode.getNodeValue()))
						unirepGGCheckBox.setValue(true);
					else if (GlimmpseConstants.TEST_UNIREP_HUYNH_FELDT.equals(testNode.getNodeValue()))
						unirepHFCheckBox.setValue(true);
				}
			}
		}
		// check if the options are complete
		checkComplete();
	}
}
