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
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.ChartRequestBuilder;
import edu.cudenver.bios.glimmpse.client.ChartRequestBuilder.AxisType;
import edu.cudenver.bios.glimmpse.client.ChartRequestBuilder.StratificationType;
import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.AlphaListener;
import edu.cudenver.bios.glimmpse.client.listener.BetaScaleListener;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.ChartOptionsListener;
import edu.cudenver.bios.glimmpse.client.listener.PowerMethodListener;
import edu.cudenver.bios.glimmpse.client.listener.QuantileListener;
import edu.cudenver.bios.glimmpse.client.listener.RelativeGroupSizeListener;
import edu.cudenver.bios.glimmpse.client.listener.PerGroupSampleSizeListener;
import edu.cudenver.bios.glimmpse.client.listener.SigmaScaleListener;
import edu.cudenver.bios.glimmpse.client.listener.TestListener;

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
implements ClickHandler, AlphaListener, BetaScaleListener,
SigmaScaleListener, TestListener, PowerMethodListener,
QuantileListener, PerGroupSampleSizeListener, RelativeGroupSizeListener,
CovariateListener
{
	// constants for xml parsing
	private static final String TAG_DISPLAY = "display";
	private static final String ATTR_TABLE = "table";
	private static final String ATTR_CURVE = "curve";
	private static final String ATTR_XAXIS = "xaxis";
	private static final String ATTR_VALUE_TOTAL_N = "totalN";
	private static final String ATTR_VALUE_BETA_SCALE = "betaScale";
	private static final String ATTR_VALUE_SIGMA_SCALE = "sigmaScale";
	private static final String ATTR_VALUE_ALPHA = "alpha";
	private static final String ATTR_VALUE_TEST = "test";
	private static final String ATTR_VALUE_POWER_METHOD = "powerMethod";
	private static final String ATTR_VALUE_QUANTILE = "quantile";
	private static final String ATTR_VALUE_POWER = "power";
	
	protected String radioGroupSuffix  = "";
	
	protected static final String XAXIS_RADIO_GROUP = "xAxis";
	protected static final String CURVE_TYPE_RADIO_GROUP = "curve";
	protected ArrayList<ChartOptionsListener> listeners = new ArrayList<ChartOptionsListener>();
    
	// mutliplier to get total sample size from relative sizes
	protected int totalSampleSizeMultiplier = 1;
	protected ArrayList<Integer> perGroupNList = new ArrayList<Integer>();
	
	// skip curve button
	protected CheckBox disableCheckbox = new CheckBox();
	
    // options for x-axis
	protected ListBox xaxisListBox = new ListBox();

    // options for the stratification variable
	protected ListBox stratifyListBox = new ListBox();
    
    // need to hang onto the power method and quantile labels
    // so we can actively hide / show depending on whether we are controlling
    // for a baseline covariate
    protected HTML powerMethodLabel = new HTML(Glimmpse.constants.curveOptionsPowerMethodLabel());
    protected HTML quantileLabel = new HTML(Glimmpse.constants.curveOptionsQuantileLabel());
    
    // select boxes for items that must be fixed for the curve
    protected ListBox totalNListBox = new ListBox();
    protected ListBox betaScaleListBox = new ListBox();
    protected ListBox sigmaScaleListBox = new ListBox();
    protected ListBox testListBox = new ListBox();
    protected ListBox alphaListBox = new ListBox();
    protected ListBox powerMethodListBox = new ListBox();
    protected ListBox quantileListBox = new ListBox();
    
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
		panel.add(createStratificationPanel());
		panel.add(createFixValuesPanel());

		// set defaults
		reset();

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		// initialize
		initWidget(panel);
	}
    
	private void enableOptions(boolean enabled)
	{
		
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
	
	private VerticalPanel createXAxisPanel()
	{
		VerticalPanel panel = new VerticalPanel();

		// create the listbox for the x-axis values
		xaxisListBox.addItem(Glimmpse.constants.curveOptionsSampleSizeLabel(), 
				ATTR_VALUE_TOTAL_N);
		xaxisListBox.addItem(Glimmpse.constants.curveOptionsBetaScaleLabel(), 
				ATTR_VALUE_BETA_SCALE);
		xaxisListBox.addItem(Glimmpse.constants.curveOptionsSigmaScaleLabel(), 
				ATTR_VALUE_SIGMA_SCALE);
		
		// add callback
		xaxisListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		// layout the panel
		panel.add(new HTML(Glimmpse.constants.curveOptionsXAxisLabel()));
		panel.add(xaxisListBox);
		
		// set style
		xaxisListBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
	
	private VerticalPanel createStratificationPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		// create the radio buttons for the curve types
		stratifyListBox.addItem(Glimmpse.constants.curveOptionsSampleSizeLabel(), 
				ATTR_VALUE_TOTAL_N);
		stratifyListBox.addItem(Glimmpse.constants.curveOptionsBetaScaleLabel(), 
				ATTR_VALUE_BETA_SCALE);
		stratifyListBox.addItem(Glimmpse.constants.curveOptionsSigmaScaleLabel(), 
				ATTR_VALUE_SIGMA_SCALE);
		
		stratifyListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		// layout the panel
		panel.add(new HTML(Glimmpse.constants.curveOptionsStratifyLabel()));
		panel.add(stratifyListBox);
		
		// set style
		stratifyListBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
	
	private VerticalPanel createFixValuesPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Grid grid = new Grid(7,2);
		// add drop down lists for remaining values that need to be fixed
		grid.setWidget(0, 1, totalNListBox);
		grid.setWidget(1, 1, betaScaleListBox);
		grid.setWidget(2, 1, sigmaScaleListBox);
		grid.setWidget(3, 1, testListBox);
		grid.setWidget(4, 1, alphaListBox);
		grid.setWidget(5, 1, powerMethodListBox);
		grid.setWidget(6, 1, quantileListBox);
		grid.setWidget(0, 0, new HTML(Glimmpse.constants.curveOptionsSampleSizeLabel()));
		grid.setWidget(1, 0, new HTML(Glimmpse.constants.curveOptionsBetaScaleLabel()));
		grid.setWidget(2, 0, new HTML(Glimmpse.constants.curveOptionsSigmaScaleLabel()));
		grid.setWidget(3, 0, new HTML(Glimmpse.constants.curveOptionsTestLabel()));
		grid.setWidget(4, 0, new HTML(Glimmpse.constants.curveOptionsAlphaLabel()));
		grid.setWidget(5, 0, powerMethodLabel);
		grid.setWidget(6, 0, quantileLabel);
		
		// layout the panel
		panel.add(new HTML(Glimmpse.constants.curveOptionsFixValuesLabel()));
		panel.add(grid);
		
		// set style
		grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
	
    
	/**
	 * Clear the options panel
	 */
	public void reset()
	{		
		// set the display options to table only
		disableCheckbox.setValue(true);
		enableOptions(false);
		// set defaults
//		xaxisTotalNRadioButton.setValue(true);
//		curveTotalNRadioButton.setEnabled(false);
//		totalNListBox.setEnabled(false);
//		xaxisBetaScaleRadioButton.setEnabled(false);
//		curveBetaScaleRadioButton.setValue(true);
//		betaScaleListBox.setEnabled(false);
//	    // hide the power method and quantile related boxes
//	    // only visible when controlling for a baseline covariate
//		powerMethodLabel.setVisible(false);
//		curvePowerMethodRadioButton.setVisible(false);
//		powerMethodListBox.setVisible(false);
//		quantileLabel.setVisible(false);
//		curveQuantileRadioButton.setVisible(false);
//		quantileListBox.setVisible(false);
		
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
		// create the ChartRequestBuilder and notify listeners
		ChartRequestBuilder builder = null;
		if (!disableCheckbox.getValue())
		{
			builder = new ChartRequestBuilder();
			
			setBuilderAxisType(builder);
			setBuilderCurveType(builder);
			
			// curve group description			
			builder.setAlpha(Double.parseDouble(alphaListBox.getItemText(alphaListBox.getSelectedIndex())));
			builder.setTest(testListBox.getItemText(testListBox.getSelectedIndex()));
			builder.setSampleSize(Integer.parseInt(totalNListBox.getItemText(totalNListBox.getSelectedIndex())));
			builder.setBetaScale(Double.parseDouble(betaScaleListBox.getItemText(betaScaleListBox.getSelectedIndex())));
			builder.setSigmaScale(Double.parseDouble(sigmaScaleListBox.getItemText(sigmaScaleListBox.getSelectedIndex())));
			if (powerMethodListBox.isVisible())
				builder.setPowerMethod(powerMethodListBox.getItemText(powerMethodListBox.getSelectedIndex()));
			if (quantileListBox.isVisible())
				builder.setQuantile(Double.parseDouble(quantileListBox.getItemText(quantileListBox.getSelectedIndex())));
		}

		for(ChartOptionsListener listener: listeners) listener.onShowCurve(builder);
	}
	
	private void setBuilderAxisType(ChartRequestBuilder builder)
	{
		String value = xaxisListBox.getItemText(xaxisListBox.getSelectedIndex());
		if (ATTR_VALUE_BETA_SCALE.equals(value))
		{
			builder.setXAxisType(AxisType.BETA_SCALE);
			builder.addAxisLabel("Regression Coefficient Scale Factor");
		}
		else if (ATTR_VALUE_SIGMA_SCALE.equals(value))
		{
			builder.setXAxisType(AxisType.SIGMA_SCALE);
			builder.addAxisLabel("Variance Scale Factor");
		}
		else if (ATTR_VALUE_TOTAL_N.equals(value))
		{
			builder.setXAxisType(AxisType.SAMPLE_SIZE);
			builder.addAxisLabel("Total Sample Size");
		}
		
		builder.addAxisLabel("Power");
	}
	
	private void setBuilderCurveType(ChartRequestBuilder builder)
	{
		String value = stratifyListBox.getItemText(stratifyListBox.getSelectedIndex());
	    if (ATTR_VALUE_TOTAL_N.equals(value))
	    {
	    	builder.setStratificationType(StratificationType.TOTAL_N);
	    	for(int i = 0; i < totalNListBox.getItemCount(); i++)
	    	{
		    	builder.addLegendLabel("Total N = " + totalNListBox.getItemText(i));
	    	}
	    }
	    else if (ATTR_VALUE_BETA_SCALE.equals(value))
	    {
	    	builder.setStratificationType(StratificationType.BETA_SCALE);
	    	for(int i = 0; i < betaScaleListBox.getItemCount(); i++)
	    	{
		    	builder.addLegendLabel("Beta Scale = " + betaScaleListBox.getItemText(i));
	    	}
	    }
	    else if (ATTR_VALUE_SIGMA_SCALE.equals(value))
	    {
	    	builder.setStratificationType(StratificationType.SIGMA_SCALE);
	    	for(int i = 0; i < sigmaScaleListBox.getItemCount(); i++)
	    	{
		    	builder.addLegendLabel("Sigma Scale = " + sigmaScaleListBox.getItemText(i));
	    	}
	    }
	    else if (ATTR_VALUE_TEST.equals(value))
	    {
	    	builder.setStratificationType(StratificationType.TEST);
	    	for(int i = 0; i < testListBox.getItemCount(); i++)
	    	{
		    	builder.addLegendLabel("Test = " + testListBox.getItemText(i));
	    	}
	    }
	    else if (ATTR_VALUE_ALPHA.equals(value))
	    {
	    	builder.setStratificationType(StratificationType.ALPHA);
	    	for(int i = 0; i < alphaListBox.getItemCount(); i++)
	    	{
		    	builder.addLegendLabel("Alpha = " + alphaListBox.getItemText(i));
	    	}
	    }
	    else if (ATTR_VALUE_POWER_METHOD.equals(value))
	    {
	    	builder.setStratificationType(StratificationType.POWER_METHOD);
	    	for(int i = 0; i < powerMethodListBox.getItemCount(); i++)
	    	{
		    	builder.addLegendLabel("Method = " + powerMethodListBox.getItemText(i));
	    	}
	    }
	    else if (ATTR_VALUE_QUANTILE.equals(value))
	    {
	    	builder.setStratificationType(StratificationType.QUANTILE);
	    	for(int i = 0; i < quantileListBox.getItemCount(); i++)
	    	{
		    	builder.addLegendLabel("Quantile = " + quantileListBox.getItemText(i));
	    	}
	    }
	}
	
	/**
	 * Add a listener for Options panel events
	 * @param listener class implementing the OptionsListener interface
	 */
	public void addChartOptionsListener(ChartOptionsListener listener)
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

	/**
	 * Fill the alpha list box with the current alpha values
	 * @param alphaList current list of alpha values entered by the user
	 */
	@Override
	public void onAlphaList(List<String> alphaList)
	{
		alphaListBox.clear();
		for(String alpha: alphaList) alphaListBox.addItem(alpha);
	}

	@Override
	public void onBetaScaleList(List<String> betaScaleList)
	{
		betaScaleListBox.clear();
		for(String betaScale: betaScaleList) betaScaleListBox.addItem(betaScale);
	}

	@Override
	public void onSigmaScaleList(List<String> sigmaScaleList)
	{
		sigmaScaleListBox.clear();
		for(String sigmaScale: sigmaScaleList) sigmaScaleListBox.addItem(sigmaScale);
	}

	@Override
	public void onTestList(List<String> testList)
	{
		testListBox.clear();
		for(String test: testList) testListBox.addItem(test);
	}

	@Override
	public void onQuantileList(List<String> quantileList)
	{
		quantileListBox.clear();
		for(String quantile: quantileList) quantileListBox.addItem(quantile);
	}

	@Override
	public void onPowerMethodList(List<String> powerMethodList)
	{
		powerMethodListBox.clear();
		for(String powerMethod: powerMethodList) powerMethodListBox.addItem(powerMethod);
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
//		powerMethodLabel.setVisible(hasCovariate);
//		curvePowerMethodRadioButton.setVisible(hasCovariate);
//		powerMethodListBox.setVisible(hasCovariate);
//		
//		quantileLabel.setVisible(hasCovariate);
//		curveQuantileRadioButton.setVisible(hasCovariate);
//		quantileListBox.setVisible(hasCovariate);
	}

	@Override
	public void onRelativeGroupSize(List<Integer> relativeSizes)
	{
		totalSampleSizeMultiplier = 0;
		for(Integer size: relativeSizes) totalSampleSizeMultiplier += size;
	}

	@Override
	public void onPerGroupSampleSizeList(List<String> perGroupSampleSizeList)
	{
		perGroupNList.clear();
		for(String value: perGroupSampleSizeList)
		{
			perGroupNList.add(Integer.parseInt(value));
		}
	}
	
	@Override
	public void onEnter()
	{
		// we need to combine the relative group sizes and per group sample sizes
		// on enter since we get this info from two difference screens
		totalNListBox.clear();
		for(Integer perGroupSize: perGroupNList)
		{
			int totalN = perGroupSize * totalSampleSizeMultiplier;
			totalNListBox.addItem(Integer.toString(totalN));
		}
	}
}
