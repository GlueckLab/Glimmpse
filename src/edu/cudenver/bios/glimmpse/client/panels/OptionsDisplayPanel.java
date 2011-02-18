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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.OptionsListener;

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

	protected String radioGroupSuffix  = "";
	
	protected static final String XAXIS_RADIO_GROUP = "xAxis";
	protected ArrayList<OptionsListener> listeners = new ArrayList<OptionsListener>();
    
	// skip curve button
	protected CheckBox disableCheckbox = new CheckBox();
	
    // options for x-axis
    protected RadioButton xaxisTotalNRadioButton;
    protected RadioButton xaxisSigmaScaleRadioButton;
    protected RadioButton xaxisBetaScaleRadioButton;

    // select boxes for items that must be fixed for the curve
    
    
    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsDisplayPanel(String radioGroupSuffix)
	{
		super();
		this.radioGroupSuffix = radioGroupSuffix;
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(Glimmpse.constants.curveOptionsTitle());
		HTML description = new HTML(Glimmpse.constants.curveOptionsDescription());        

		// layout the overall panel
		panel.add(header);
		panel.add(description);
		panel.add(createDisablePanel());
		panel.add(createXAxisPanel());
		panel.add(createCurveTypePanel());
		panel.add(createFixedValuesPanel());
		
		// set defaults
		reset();

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		// initialize
		initWidget(panel);
	}
    
	private HorizontalPanel createDisablePanel()
	{
		HorizontalPanel panel = new HorizontalPanel();
		
		// add callbacks
		disableCheckbox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				CheckBox cb = (CheckBox) event.getSource();
				enableOptions(!cb.getValue());
			}
		});
		
		panel.add(disableCheckbox);
		panel.add(new HTML(Glimmpse.constants.curveOptionsNone()));
		
		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
    
	private void enableOptions(boolean enable)
	{
		
	}
	
    private VerticalPanel createXAxisPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
		String group = XAXIS_RADIO_GROUP + radioGroupSuffix;
	    xaxisTotalNRadioButton = new RadioButton(group, Glimmpse.constants.curveOptionsXAxisSampleSizeLabel());
	    xaxisSigmaScaleRadioButton = new RadioButton(group, Glimmpse.constants.curveOptionsXAxisSigmaScaleLabel());
	    xaxisBetaScaleRadioButton = new RadioButton(group, Glimmpse.constants.curveOptionsXAxisBetaScaleLabel());
		xaxisTotalNRadioButton.setValue(true);

		// build panel
	    panel.add(new HTML(Glimmpse.constants.curveOptionsXAxisLabel()));
		panel.add(xaxisTotalNRadioButton);
		panel.add(xaxisBetaScaleRadioButton);
		panel.add(xaxisSigmaScaleRadioButton);
		
		// add style
		xaxisTotalNRadioButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		xaxisBetaScaleRadioButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		xaxisSigmaScaleRadioButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
    }

    private VerticalPanel createCurveTypePanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	
    	
    	return panel;
    }
    
    private VerticalPanel createFixedValuesPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	return panel;
    }
    
    
	/**
	 * Clear the options panel
	 */
	public void reset()
	{		
		// set the display options to table only
		// TODO
		
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
//
//		// add display options - format: <display table=[T|F] curve=[T|F] xaxis=[totaln|effectSize|variance]/>
//		buffer.append("<");
//		buffer.append(TAG_DISPLAY);
//		buffer.append(" ");
//		buffer.append(ATTR_TABLE);
//		buffer.append("='");
//		buffer.append(showTableCheckBox.getValue());
//		buffer.append("' ");
//		buffer.append(ATTR_CURVE);
//		buffer.append("='");
//		buffer.append(showCurveCheckBox.getValue());
//		buffer.append("' ");
//		
//		buffer.append(ATTR_XAXIS);
//		buffer.append("='");
//		if (xaxisTotalNRadioButton.getValue())
//			buffer.append(ATTR_VALUE_XAXIS_TOTAL_N);
//		else if (xaxisEffectSizeRadioButton.getValue())
//			buffer.append(ATTR_VALUE_XAXIS_EFFECT_SIZE);
//		else
//			buffer.append(ATTR_VALUE_XAXIS_VARIANCE);
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
//		if (showTableCheckBox.getValue() || showCurveCheckBox.getValue())
//		{
//			notifyComplete();
//		}
//		else
//		{
//			notifyInProgress();
//		}
	}
	
	/**
	 * Notify options listeners of selected options as we exit the options screen
	 */
	@Override
	public void onExit()
	{
//		XAxisType xaxisType;
//		if (xaxisTotalNRadioButton.getValue())
//		{
//			xaxisType = XAxisType.TOTAL_N;
//		}
//		else if (xaxisVarianceRadioButton.getValue())
//		{
//			xaxisType = XAxisType.VARIANCE;
//		}
//		else
//		{
//			xaxisType = XAxisType.BETA_SCALE;
//		}
//		
//		
//		for(OptionsListener listener: listeners)
//		{
//			listener.onShowCurve(showCurveCheckBox.getValue(), xaxisType, null);
//		}
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
//		if (TAG_DISPLAY.equals(node.getNodeName()))
//		{
//			NamedNodeMap attrs = node.getAttributes();
//			try
//			{
//				Node tableNode = attrs.getNamedItem(ATTR_TABLE);
//				if (tableNode != null) 
//					showTableCheckBox.setValue(Boolean.parseBoolean(tableNode.getNodeValue()));
//
//				Node curveNode = attrs.getNamedItem(ATTR_CURVE);
//				if (curveNode != null) 
//					showCurveCheckBox.setValue(Boolean.parseBoolean(curveNode.getNodeValue()));
//
//				Node xaxisNode = attrs.getNamedItem(ATTR_XAXIS);
//				if (xaxisNode != null)
//				{
//					if (ATTR_VALUE_XAXIS_TOTAL_N.equals(xaxisNode.getNodeValue()))
//					{
//						xaxisTotalNRadioButton.setValue(true);
//					}
//					else if (ATTR_VALUE_XAXIS_EFFECT_SIZE.equals(xaxisNode.getNodeValue()))
//					{
//						xaxisEffectSizeRadioButton.setValue(true);
//					}
//					else if (ATTR_VALUE_XAXIS_VARIANCE.equals(xaxisNode.getNodeValue()))
//					{
//						xaxisVarianceRadioButton.setValue(true);
//					}
//				}
//			}
//			catch (Exception e)
//			{
//				// ignore parsing errors for now
//			}
//
//			// enable/disable the xaxis radio buttons
//			xaxisTotalNRadioButton.setEnabled(showCurveCheckBox.getValue());
//			xaxisEffectSizeRadioButton.setEnabled(showCurveCheckBox.getValue());
//			xaxisVarianceRadioButton.setEnabled(showCurveCheckBox.getValue());

			// check if the options are complete
			checkComplete();
//		}
	}
}
