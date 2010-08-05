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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;

/**
 * WizardStepPanel which allows the user to select whether they are solving for
 * power, sample size, or effect size (i.e. beta matrix scale factor)
 * 
 * @author Sarah Kreidler
 *
 */
public class SolvingForPanel extends WizardStepPanel
implements ClickHandler, DynamicListValidator
{
	protected static final String SOLVE_FOR_RADIO_GROUP = "SolvingFor";
	
	// "solving for" check boxes
	protected RadioButton solvingForPowerRadioButton; 
	protected RadioButton solvingForSampleSizeRadioButton;
	protected RadioButton solvingForEffectSizeRadioButton;
	
	// list of nominal power values.  Only displayed when solving for effect size or sample size
	protected VerticalPanel nominalPowerPanel = new VerticalPanel();
	String[] columnNames = { Glimmpse.constants.solvingForNominalPowerTableColumn()};
    protected DynamicListPanel nominalPowerListPanel = 
    	new DynamicListPanel(columnNames, this);
    
	// listeners for changes to the solution type
	protected ArrayList<SolvingForListener> listeners = new ArrayList<SolvingForListener>();
	
	/**
	 * Constructor
	 * 
	 * @param radioGroupPrefix prefix to ensure uniqueness of the radio button group
	 */
	public SolvingForPanel(String radioGroupPrefix)
	{
		super(Glimmpse.constants.stepsLeftSolvingFor());
		// since one of the radio buttons will always be checked, this wizardsteppanel
		// is always considered complete (complete member var is from superclass WizardStepPanel)
		complete = true;
		
		VerticalPanel panel = new VerticalPanel();
		
		HTML header = new HTML(Glimmpse.constants.solvingForTitle());
		HTML description = new HTML(Glimmpse.constants.solvingForDescription());

		// build the nominal power subpanel
		buildNominalPowerPanel();
		
		// create the radio buttons - note, we add a prefix to the radio group name since multiple
		// instances of this class are created for matrix and guided mode
		String group = radioGroupPrefix + SOLVE_FOR_RADIO_GROUP;
		solvingForPowerRadioButton = 
			new RadioButton(group, Glimmpse.constants.solvingForPowerLabel());
		solvingForSampleSizeRadioButton = 
			new RadioButton(group, Glimmpse.constants.solvingForSampleSizeLabel());
		solvingForEffectSizeRadioButton = 
			new RadioButton(group, Glimmpse.constants.solvingForEffectSizeLabel());
		
		// layout the radio buttons
		Grid grid = new Grid(3,1);
		grid.setWidget(0, 0, solvingForPowerRadioButton);
		grid.setWidget(1, 0, solvingForSampleSizeRadioButton);
		grid.setWidget(2, 0, solvingForEffectSizeRadioButton);
		// select power by default
		solvingForPowerRadioButton.setValue(true);
		nominalPowerPanel.setVisible(false);
		
		// notify the listeners when a radio button is selected
		solvingForPowerRadioButton.addClickHandler(this);
		solvingForSampleSizeRadioButton.addClickHandler(this);
		solvingForEffectSizeRadioButton.addClickHandler(this);
		
		// layout the panel
		panel.add(header);
		panel.add(description);
		panel.add(grid);
		panel.add(nominalPowerPanel);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		
		// initialize - required by gwt
		initWidget(panel);
	}
	
	/**
	 * Build the panel for entering nominal power values.  This panel is 
	 * only used when solving for effect size or sample size
	 */
	public void buildNominalPowerPanel()
	{
    	// TODO: constants
		HTML header = new HTML(Glimmpse.constants.solvingForNominalPowerTitle());
		HTML description = new HTML(Glimmpse.constants.solvingForNominalPowerDescription());
		
		nominalPowerPanel.add(header);
		nominalPowerPanel.add(description);
		nominalPowerPanel.add(nominalPowerListPanel);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		nominalPowerPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
	}
	
	/**
	 * Reset the panel to the default (solve for power), and clear the nominal 
	 * power list
	 */
	public void reset()
	{
		solvingForPowerRadioButton.setValue(true);
		nominalPowerListPanel.reset();
		nominalPowerPanel.setVisible(false);
		notifyComplete();
	}
	
	/**
	 * Add a listener for the solution type (power, sample size, effect size)
	 * 
	 * @param listener
	 */
	public void addSolvingForListener(SolvingForListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Notify solving for listeners when one of the solution type radio buttons
	 * is clicked.  Also notify the current number of rows in the power list
	 * 
	 * @param event the click event
	 */
	@Override
	public void onClick(ClickEvent event)
	{
		nominalPowerPanel.setVisible(!solvingForPowerRadioButton.getValue());
		
		SolvingForListener.SolutionType type = null;
		if (solvingForPowerRadioButton.getValue())
		{
			type = SolvingForListener.SolutionType.POWER;
			notifyComplete();
		}
		else if (solvingForSampleSizeRadioButton.getValue())
		{
			type = SolvingForListener.SolutionType.TOTAL_N;
			onValidRowCount(nominalPowerListPanel.getValidRowCount());
		}
		else if (solvingForEffectSizeRadioButton.getValue())
		{
			type = SolvingForListener.SolutionType.EFFECT_SIZE;
			onValidRowCount(nominalPowerListPanel.getValidRowCount());
		}
		if (type != null)
		{
			for(SolvingForListener listener: listeners) listener.onSolvingFor(type);
		}
	}
	
	/**
	 * Validate new entries in the alpha list
	 * @see DynamicListValidator
	 */
	public void validate(String value, int column) throws IllegalArgumentException
	{
		try
		{
			TextValidation.parseDouble(value, 0, 1, false);
		}
		catch (NumberFormatException nfe)
		{
			throw new IllegalArgumentException(Glimmpse.constants.errorInvalidPower());
		}
	}

    /**
     * Callback when the number of valid entries in the list of
     * alpha values changes
     * 
     * @see DynamicListValidator
     */
    public void onValidRowCount(int validRowCount)
    {
    	if (validRowCount > 0)
    		notifyComplete();
    	else
    		notifyInProgress();
    }
    
    /**
     * 
     * @return
     */
    public String toXML()
    {
    	if (!solvingForPowerRadioButton.getValue())
    		return nominalPowerListPanel.toXML("powerList");
    	else
    		return "";
    }
}
