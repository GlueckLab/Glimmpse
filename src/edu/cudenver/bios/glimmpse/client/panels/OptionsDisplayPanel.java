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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.OptionsListener;
import edu.cudenver.bios.glimmpse.client.listener.OptionsListener.XAxisType;

/**
 * Panel which allows user to select display options
 * for their power/sample size calculations.  Note that two instances of this 
 * class are created (one for matrix mode, one for guided mode) so any
 * radio groups or other unique identifiers must have a mode-specific prefix
 * 
 * @author Sarah Kreidler
 *
 */
public class OptionsDisplayPanel extends WizardStepPanel
implements ClickHandler
{
	// constants for xml parsing
	private static final String TAG_DISPLAY = "display";
	private static final String ATTR_TABLE = "table";
	private static final String ATTR_CURVE = "curve";
	private static final String ATTR_XAXIS = "xaxis";
	private static final String ATTR_VALUE_XAXIS_TOTAL_N = "totalN";
	private static final String ATTR_VALUE_XAXIS_EFFECT_SIZE = "effectSize";
	private static final String ATTR_VALUE_XAXIS_VARIANCE = "variance";

	protected static final String XAXIS_RADIO_GROUP = "xAxis";
	protected ArrayList<OptionsListener> listeners = new ArrayList<OptionsListener>();
    
    // options for results display
    protected CheckBox showTableCheckBox = new CheckBox();
    protected CheckBox showCurveCheckBox = new CheckBox();
    protected HTML xaxisLabel = new HTML(Glimmpse.constants.displayOptionsXAxisLabel());
    protected RadioButton xaxisTotalNRadioButton;
    protected RadioButton xaxisVarianceRadioButton;
    protected RadioButton xaxisEffectSizeRadioButton;

    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsDisplayPanel(String radioGroupPrefix)
	{
		super();
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(Glimmpse.constants.displayOptionsTitle());
		HTML description = new HTML(Glimmpse.constants.displayOptionsDescription());        

		String group = radioGroupPrefix + XAXIS_RADIO_GROUP;
	    xaxisTotalNRadioButton = new RadioButton(group, Glimmpse.constants.displayOptionsXAxisSampleSizeLabel());
	    xaxisVarianceRadioButton = new RadioButton(group, Glimmpse.constants.displayOptionsXAxisVarianceLabel());
	    xaxisEffectSizeRadioButton = new RadioButton(group, Glimmpse.constants.displayOptionsXAxisEffectSizeLabel());
		
		Grid grid = new Grid(6,2);
		grid.setWidget(0, 0, showTableCheckBox);
		grid.setWidget(0, 1, new HTML(Glimmpse.constants.displayOptionsTableLabel()));
		grid.setWidget(1, 0, showCurveCheckBox);
		grid.setWidget(1, 1, new HTML(Glimmpse.constants.displayOptionsCurveLabel()));
		grid.setWidget(2, 1, xaxisLabel);
		grid.setWidget(3, 1, xaxisTotalNRadioButton);
		grid.setWidget(4, 1, xaxisEffectSizeRadioButton);
		grid.setWidget(5, 1, xaxisVarianceRadioButton);
		xaxisTotalNRadioButton.setValue(true);
		
		// set conditional power on by default
		showTableCheckBox.setValue(true);
		
		// callback to enable/disable the X-axis options when the user clicks the display curve box
		showCurveCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				CheckBox cb = (CheckBox) event.getSource();
				xaxisTotalNRadioButton.setEnabled(cb.getValue());
				xaxisEffectSizeRadioButton.setEnabled(cb.getValue());
				xaxisVarianceRadioButton.setEnabled(cb.getValue());
			}
		});

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
		// set the display options to table only
		showTableCheckBox.setValue(true);
		showCurveCheckBox.setValue(false);
		xaxisTotalNRadioButton.setValue(true);
		xaxisTotalNRadioButton.setEnabled(false);
		xaxisEffectSizeRadioButton.setEnabled(false);
		xaxisVarianceRadioButton.setEnabled(false);
		
		notifyComplete();
	}

	/**
	 * Create an XML representation of the panel to be saved with
	 * the study design
	 * 
	 * @return study XML
	 */
	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();

		// add display options - format: <display table=[T|F] curve=[T|F] xaxis=[totaln|effectSize|variance]/>
		buffer.append("<");
		buffer.append(TAG_DISPLAY);
		buffer.append(" ");
		buffer.append(ATTR_TABLE);
		buffer.append("='");
		buffer.append(showTableCheckBox.getValue());
		buffer.append("' ");
		buffer.append(ATTR_CURVE);
		buffer.append("='");
		buffer.append(showCurveCheckBox.getValue());
		buffer.append("' ");
		
		buffer.append(ATTR_XAXIS);
		buffer.append("='");
		if (xaxisTotalNRadioButton.getValue())
			buffer.append(ATTR_VALUE_XAXIS_TOTAL_N);
		else if (xaxisEffectSizeRadioButton.getValue())
			buffer.append(ATTR_VALUE_XAXIS_EFFECT_SIZE);
		else
			buffer.append(ATTR_VALUE_XAXIS_VARIANCE);
		buffer.append("' ");
		buffer.append("/>");

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
		if (showTableCheckBox.getValue() || showCurveCheckBox.getValue())
		{
			notifyComplete();
		}
		else
		{
			notifyInProgress();
		}
	}
	
	/**
	 * Notify options listeners of selected options as we exit the options screen
	 */
	@Override
	public void onExit()
	{
		XAxisType xaxisType;
		if (xaxisTotalNRadioButton.getValue())
		{
			xaxisType = XAxisType.TOTAL_N;
		}
		else if (xaxisVarianceRadioButton.getValue())
		{
			xaxisType = XAxisType.VARIANCE;
		}
		else
		{
			xaxisType = XAxisType.EFFECT_SIZE;
		}
		
		
		for(OptionsListener listener: listeners)
		{
			listener.onShowTable(showTableCheckBox.getValue());
			listener.onShowCurve(showCurveCheckBox.getValue(), xaxisType, null);
		}
	}
	
	/**
	 * Add a listener for Options panel events
	 * @param listener class implementing the OptionsListener interface
	 */
	public void addOptionsListener(OptionsListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Parse the saved study design information and set the appropriate options
	 */
	@Override
	public void loadFromNode(Node node)
	{
		if (TAG_DISPLAY.equals(node.getNodeName()))
		{
			NamedNodeMap attrs = node.getAttributes();
			try
			{
				Node tableNode = attrs.getNamedItem(ATTR_TABLE);
				if (tableNode != null) 
					showTableCheckBox.setValue(Boolean.parseBoolean(tableNode.getNodeValue()));

				Node curveNode = attrs.getNamedItem(ATTR_CURVE);
				if (curveNode != null) 
					showCurveCheckBox.setValue(Boolean.parseBoolean(curveNode.getNodeValue()));

				Node xaxisNode = attrs.getNamedItem(ATTR_XAXIS);
				if (xaxisNode != null)
				{
					if (ATTR_VALUE_XAXIS_TOTAL_N.equals(xaxisNode.getNodeValue()))
					{
						xaxisTotalNRadioButton.setValue(true);
					}
					else if (ATTR_VALUE_XAXIS_EFFECT_SIZE.equals(xaxisNode.getNodeValue()))
					{
						xaxisEffectSizeRadioButton.setValue(true);
					}
					else if (ATTR_VALUE_XAXIS_VARIANCE.equals(xaxisNode.getNodeValue()))
					{
						xaxisVarianceRadioButton.setValue(true);
					}
				}
			}
			catch (Exception e)
			{
				// ignore parsing errors for now
			}

			// enable/disable the xaxis radio buttons
			xaxisTotalNRadioButton.setEnabled(showCurveCheckBox.getValue());
			xaxisEffectSizeRadioButton.setEnabled(showCurveCheckBox.getValue());
			xaxisVarianceRadioButton.setEnabled(showCurveCheckBox.getValue());

			// check if the options are complete
			checkComplete();
		}
	}
}
