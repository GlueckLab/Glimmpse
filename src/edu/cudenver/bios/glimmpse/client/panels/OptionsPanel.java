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
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OptionsListener;
import edu.cudenver.bios.glimmpse.client.listener.OptionsListener.XAxisType;

/**
 * Panel which allows user to select statistical tests, display options
 * for their power/sample size calculations.  Note that two instances of this 
 * class are created (one for matrix mode, one for guided mode) so any
 * radio groups or other unique identifiers must have a mode-specific prefix
 * 
 * @author Sarah Kreidler
 *
 */
public class OptionsPanel extends WizardStepPanel
implements CovariateListener, ClickHandler
{
	// constants for xml parsing
	private static final String TAG_DISPLAY = "display";
    private static final String TAG_QUANTILE_LIST = "quantileList";
    private static final String TAG_TEST_LIST = "testList";
    private static final String TAG_POWER_METHOD_LIST = "powerMethodList";
	private static final String ATTR_TABLE = "table";
	private static final String ATTR_CURVE = "curve";
	private static final String ATTR_XAXIS = "xaxis";
	private static final String ATTR_VALUE_XAXIS_TOTAL_N = "totalN";
	private static final String ATTR_VALUE_XAXIS_EFFECT_SIZE = "effectSize";
	private static final String ATTR_VALUE_XAXIS_VARIANCE = "variance";

	protected static final String XAXIS_RADIO_GROUP = "xAxis";
	protected ArrayList<OptionsListener> listeners = new ArrayList<OptionsListener>();

	// subpanels
	protected VerticalPanel testSubpanel = new VerticalPanel();
	protected VerticalPanel powerMethodSubpanel = new VerticalPanel();
	protected VerticalPanel displaySubpanel = new VerticalPanel();
	
	// check boxes for statistical tests
	protected CheckBox hotellingLawleyCheckBox = new CheckBox();
	protected CheckBox pillaiBartlettCheckBox = new CheckBox();
	protected CheckBox wilksCheckBox = new CheckBox();
	protected CheckBox unirepCheckBox = new CheckBox();
	protected CheckBox unirepGGCheckBox = new CheckBox();
	protected CheckBox unirepHFCheckBox = new CheckBox();
	protected CheckBox unirepBoxCheckBox = new CheckBox();

	// check boxes for power methods (only used when a baseline covariate is specified)
	protected CheckBox conditionalPowerCheckBox = new CheckBox();
	protected CheckBox unconditionalPowerCheckBox = new CheckBox();
	protected CheckBox quantilePowerCheckBox = new CheckBox();
	protected int numQuantiles = 0;
	
	// dynamic list of quantile values
    protected ListEntryPanel quantileListPanel = 
    	new ListEntryPanel(Glimmpse.constants.quantilesTableColumn(), new ListValidator() {

			@Override
			public void onValidRowCount(int validRowCount)
			{
				numQuantiles = validRowCount;
				checkComplete();
			}

			@Override
			public void validate(String value)
					throws IllegalArgumentException
			{
		    	try
		    	{
		    		TextValidation.parseDouble(value, 0, 1, false);
		    	}
		    	catch (NumberFormatException nfe)
		    	{
		    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidQuantile());
		    	}
			}
    	});
    
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
    public OptionsPanel(String mode)
	{
		super();
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(Glimmpse.constants.optionsTitle());
		HTML description = new HTML(Glimmpse.constants.optionsDescription());        

		// layout the subpanels
		buildTestSubpanel();
		buildPowerMethodSubpanel();
		buildDisplaySubpanel(mode);

		// layout the overall panel
		panel.add(header);
		panel.add(description);
		panel.add(testSubpanel);
		panel.add(powerMethodSubpanel);
		panel.add(displaySubpanel);
		
		// set defaults
		reset();
		// hide the power methods - only visible when controlling for a covariate
		powerMethodSubpanel.setVisible(false);

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		// initialize
		initWidget(panel);
	}
    
    /**
     * Build the subpanel to select statistical tests
     */
	private void buildTestSubpanel()
	{
		HTML header = new HTML(Glimmpse.constants.testTitle());
		HTML description = new HTML(Glimmpse.constants.testDescription());

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
		
		testSubpanel.add(header);
		testSubpanel.add(description);
		testSubpanel.add(grid);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
		testSubpanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		testSubpanel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
	}

	/**
	 * Build the subpanel for selection methods of power calculation
	 */
	private void buildPowerMethodSubpanel()
	{
		HTML header = new HTML(Glimmpse.constants.powerMethodTitle());
		HTML description = new HTML(Glimmpse.constants.powerMethodDescription());

		Grid grid = new Grid(4,2);
		grid.setWidget(0, 0, conditionalPowerCheckBox);
		grid.setWidget(0, 1, new HTML(Glimmpse.constants.powerMethodConditionalLabel()));
		grid.setWidget(1, 0, unconditionalPowerCheckBox);
		grid.setWidget(1, 1, new HTML(Glimmpse.constants.powerMethodUnconditionalLabel()));
		grid.setWidget(2, 0, quantilePowerCheckBox);
		grid.setWidget(2, 1, new HTML(Glimmpse.constants.powerMethodQuantileLabel()));
		grid.setWidget(3, 1, quantileListPanel);
		
		quantilePowerCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				quantileListPanel.setVisible(quantilePowerCheckBox.getValue());
			}
		});
		quantileListPanel.setVisible(false);
		
		// add callback to check if screen is complete
		conditionalPowerCheckBox.addClickHandler(this);
		unconditionalPowerCheckBox.addClickHandler(this);
		quantilePowerCheckBox.addClickHandler(this);
		
		// set conditional power on by default
		conditionalPowerCheckBox.setValue(true);
		
		// layout the subpanel
		powerMethodSubpanel.add(header);
		powerMethodSubpanel.add(description);
		powerMethodSubpanel.add(grid);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
		powerMethodSubpanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		powerMethodSubpanel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
	}

	/**
	 * Build the subpanel for indicating if results should be displayed graphically 
	 * or in table form
	 * 
	 * @param radioGroupPrefix ensures this instance of the radio group is unique
	 */
	private void buildDisplaySubpanel(String radioGroupPrefix)
	{
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
		
		// layout the subpanel
		displaySubpanel.add(header);
		displaySubpanel.add(description);
		displaySubpanel.add(grid);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
		displaySubpanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		displaySubpanel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
	}

	/**
	 * Clear the options panel
	 */
	public void reset()
	{
		// set the power method to conditional
		conditionalPowerCheckBox.setValue(true);
		unconditionalPowerCheckBox.setValue(false);
		quantilePowerCheckBox.setValue(false);
		numQuantiles = 0;
		quantileListPanel.reset();
		
		// clear the statistical tests
		hotellingLawleyCheckBox.setValue(false);
		pillaiBartlettCheckBox.setValue(false);
		wilksCheckBox.setValue(false);
		unirepCheckBox.setValue(false);
		unirepGGCheckBox.setValue(false);
		unirepHFCheckBox.setValue(false);
		unirepBoxCheckBox.setValue(false);
		
		// set the display options to table only
		showTableCheckBox.setValue(true);
		showCurveCheckBox.setValue(false);
		xaxisTotalNRadioButton.setValue(true);
		xaxisTotalNRadioButton.setEnabled(false);
		xaxisEffectSizeRadioButton.setEnabled(false);
		xaxisVarianceRadioButton.setEnabled(false);
		
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
		powerMethodSubpanel.setVisible(hasCovariate);
		if (hasCovariate)
		{
			conditionalPowerCheckBox.setValue(false);
			unconditionalPowerCheckBox.setValue(false);
			quantilePowerCheckBox.setValue(false);
			quantileListPanel.reset();
			quantileListPanel.setVisible(false);
		}
		else
		{
			// we always set conditional power for fixed designs
			conditionalPowerCheckBox.setValue(true);
		}
	}

	/**
	 * No action required by this panel for onMean callback from CovariateListener
	 */
	@Override
	public void onMean(double mean)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * No action required by this panel for onVariance callback from CovariateListener
	 */
	@Override
	public void onVariance(double variance)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Create an XML representation of the list of selected power methods
	 * 
	 * @return XML representation of the power methods
	 */
	public String powerMethodListToXML()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("<");
		buffer.append(TAG_POWER_METHOD_LIST);
		buffer.append(">");
		if (conditionalPowerCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.POWER_METHOD_CONDITIONAL);
			buffer.append("</v>");
		}
		if (unconditionalPowerCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.POWER_METHOD_UNCONDITIONAL);
			buffer.append("</v>");
		}
		if (quantilePowerCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.POWER_METHOD_QUANTILE);
			buffer.append("</v>");
		}
		buffer.append("</");
		buffer.append(TAG_POWER_METHOD_LIST);
		buffer.append(">");
		return buffer.toString();
	}

	/**
	 * Create an XML representation of the currently selected 
	 * statistical test list
	 * 
	 * @return XML representation of statistical test list
	 */
	public String testListToXML()
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
		StringBuffer buffer = new StringBuffer();
		// open tag
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_OPTIONS);
		buffer.append(">");
		// list of tests, power methods, quantiles
		buffer.append(toRequestXML());
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
		// close tag
		buffer.append("</");
		buffer.append(GlimmpseConstants.TAG_OPTIONS);
		buffer.append(">");

		return buffer.toString();
	}
	
	/**
	 * Create an XML representation of the panel for sending to the
	 * Power web service
	 * 
	 * @return
	 */
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(testListToXML());
		buffer.append(powerMethodListToXML());
		buffer.append(quantileListPanel.toXML(TAG_QUANTILE_LIST));
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
		// check if continue is allowed
		// must have at least one test checked, at least one power method
		if ((hotellingLawleyCheckBox.getValue() || wilksCheckBox.getValue() || 
				pillaiBartlettCheckBox.getValue() || unirepCheckBox.getValue() ||
				unirepGGCheckBox.getValue() || unirepHFCheckBox.getValue() ||
				unirepBoxCheckBox.getValue()) &&
				(conditionalPowerCheckBox.getValue() || unconditionalPowerCheckBox.getValue() || 
						quantilePowerCheckBox.getValue()))
		{
			if (!quantilePowerCheckBox.getValue() || numQuantiles > 0)
			{
				notifyComplete();
			}
			else
			{
				notifyInProgress();
			}
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
		if (GlimmpseConstants.TAG_OPTIONS.equals(node.getNodeName()))
		{
			NodeList children = node.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if (TAG_QUANTILE_LIST.equals(child.getNodeName()))
				{
					quantileListPanel.loadFromNode(child);
				}
				else if (TAG_TEST_LIST.equals(child.getNodeName()))
				{
					NodeList testChildren = child.getChildNodes();
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
				else if (TAG_POWER_METHOD_LIST.equals(child.getNodeName()))
				{
					NodeList pmChildren = child.getChildNodes();
					for(int pmi = 0; pmi < pmChildren.getLength(); pmi++)
					{
						Node pmChild = pmChildren.item(pmi);
						Node pmNode = pmChild.getFirstChild();
						if (pmNode != null)
						{
							if (GlimmpseConstants.POWER_METHOD_CONDITIONAL.equals(pmNode.getNodeValue()))
								conditionalPowerCheckBox.setValue(true);
							else if (GlimmpseConstants.POWER_METHOD_UNCONDITIONAL.equals(pmNode.getNodeValue()))
								unconditionalPowerCheckBox.setValue(true);
							else if (GlimmpseConstants.POWER_METHOD_QUANTILE.equals(pmNode.getNodeValue()))
							{
								quantilePowerCheckBox.setValue(true);
								quantileListPanel.setVisible(true);
							}
						}
					}
				}
				else if (TAG_DISPLAY.equals(child.getNodeName()))
				{
					NamedNodeMap attrs = child.getAttributes();
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
						// ignore parsing errors here
					}
				}
			}
		}
		
		// enable/disable the xaxis radio buttons
		xaxisTotalNRadioButton.setEnabled(showCurveCheckBox.getValue());
		xaxisEffectSizeRadioButton.setEnabled(showCurveCheckBox.getValue());
		xaxisVarianceRadioButton.setEnabled(showCurveCheckBox.getValue());

		
		// check if the options are complete
		checkComplete();
	}
}
