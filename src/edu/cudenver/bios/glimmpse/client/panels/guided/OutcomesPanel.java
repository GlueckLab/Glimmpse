/* 
 * GLIMMPSE (General Linear Multivariate Model Power and Sample size Estimator)
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.RowCheckBox;
import edu.cudenver.bios.glimmpse.client.panels.RowTextBox;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * OutcomesPanel
 * 
 * @author Sarah Kreidler
 *
 */
public class OutcomesPanel extends WizardStepPanel
{
	protected static final int CHECKBOX_COLUMN = 0;
	protected static final int LABEL_COLUMN = 1;
    protected static final int REPEATS_TEXTBOX_COLUMN = 2;
    protected static final int UNITS_TEXTBOX_COLUMN = 3;
    
    protected HTML errorHTML = new HTML();
    protected FlexTable repeatedMeasuresTable = new FlexTable();
    
    // dynamic table of outcomes
    protected ListEntryPanel outcomesListPanel = 
    	new ListEntryPanel(Glimmpse.constants.outcomesTableColumn(), 
    			new ListValidator() {
    		public void validate(String value) throws IllegalArgumentException {}

    		public void onValidRowCount(int validRowCount)
    		{
    			if (validRowCount > 0)
    				notifyComplete();
    			else
    				notifyInProgress();
    		}
    	});
    
    // dynamic table of repeated measures
    protected String[] repeatedColumnNames = {
    		Glimmpse.constants.repeatedOverTableColumn(),
    		Glimmpse.constants.repetitionsTableColumn()
    		};
    protected DynamicListPanel repeatedMeasuresListPanel = 
    	new DynamicListPanel(repeatedColumnNames, 
    			new DynamicListValidator() {
    		public void validate(String value, int column) throws IllegalArgumentException 
    		{	
    			if (value == null || value.isEmpty()) 
    				throw new IllegalArgumentException("No value entered");
    			if (column == 1)
    			{
    				try 
    				{
    					TextValidation.parseInteger(value, 1, true);
    				}
    				catch (NumberFormatException nfe)
    				{
    					throw new IllegalArgumentException("The number of repetitions must be an integer greater than 2");
    				}
    			}
    		}

    		public void onValidRowCount(int validRowCount)
    		{
    		}
    	});

    // listeners for outcome events
    protected ArrayList<OutcomesListener> listeners = new ArrayList<OutcomesListener>();

    public OutcomesPanel()
    {
    	super(Glimmpse.constants.stepsLeftOutcomes());
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.outcomesTitle());
        HTML description = new HTML(Glimmpse.constants.outcomesDescription());

        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(outcomesListPanel);
        panel.add(buildRepeatedMeasuresPanel());
//        panel.add(repeatedHeader);
//        panel.add(repeatedDescription);
//        panel.add(repeatedMeasuresListPanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
    }
    
    private VerticalPanel buildRepeatedMeasuresPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
        // create the repeated measures header/instruction text
        HTML header = new HTML(Glimmpse.constants.repeatedMeasuresTitle());
        HTML description = new HTML(Glimmpse.constants.repeatedMeasuresDescription());

        // create a flexTable to display the repeated measures
        addRepeatedMeasuresTableRow(null, 0);
                
        panel.add(header);
        panel.add(description);
        panel.add(repeatedMeasuresTable);
        panel.add(errorHTML);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
    }

    private void updateParentMeasure(int row, String newParentMeasure)
    {
    	repeatedMeasuresTable.setWidget(row, LABEL_COLUMN, 
    			new HTML("Within each of the " + newParentMeasure + ", the outcomes were repeated for "));
    }
    
    private void addRepeatedMeasuresTableRow(String parentMeasure, int row)
    {
    	RowTextBox repeatsTextBox = new RowTextBox(row);
    	RowTextBox unitsTextBox = new RowTextBox(row);
    	RowCheckBox repeatedCheckBox = new RowCheckBox(row);

    	// add callbacks
    	repeatedCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				RowCheckBox rcb = (RowCheckBox) event.getSource();
				setRepeatedMeasuresRowEnabled(rcb.row, rcb.getValue());
			}
    	});
    	unitsTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				RowTextBox rtb = (RowTextBox) event.getSource();
				validateRepeatedMeasuresRow(rtb.row);
			}
    	});
    	repeatsTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				RowTextBox rtb = (RowTextBox) event.getSource();
				validateRepeatedMeasuresRow(rtb.row);
			}
    	});
    	
    	HTML label;
    	if (parentMeasure == null || parentMeasure.isEmpty())
    	{
    		label = new HTML("My outcomes were repeated for ");
    	}
    	else
    	{
    		label = new HTML("Within each of the " + parentMeasure + ", the outcomes were repeated for ");
    	}
    	
    	repeatedMeasuresTable.setWidget(row, 0, repeatedCheckBox);
    	repeatedMeasuresTable.setWidget(row, 1, label);
    	repeatedMeasuresTable.setWidget(row, REPEATS_TEXTBOX_COLUMN, repeatsTextBox);
    	repeatedMeasuresTable.setWidget(row, UNITS_TEXTBOX_COLUMN, unitsTextBox);    	
    }
    
    private void validateRepeatedMeasuresRow(int row)
    {
    	TextBox repeats = (TextBox) repeatedMeasuresTable.getWidget(row, REPEATS_TEXTBOX_COLUMN);
    	TextBox units = (TextBox) repeatedMeasuresTable.getWidget(row, UNITS_TEXTBOX_COLUMN);
    	
    	String unitValue = units.getValue();
    	String repeatsValue = repeats.getValue();
    	try
    	{
    		if (repeatsValue != null && !repeatsValue.isEmpty()) 
    		{
    			TextValidation.parseInteger(repeatsValue, 1, true);
    			if (unitValue != null && !unitValue.isEmpty())
    			{
    				if (row == repeatedMeasuresTable.getRowCount() - 1)
    					addRepeatedMeasuresTableRow(unitValue, row+1);
    				else
    					updateParentMeasure(row+1, unitValue);
    				TextValidation.displayOkay(errorHTML, "");
    			}
    		}
    	}
    	catch (NumberFormatException nfe)
    	{
    		repeats.setValue("");
    		TextValidation.displayError(errorHTML, "Repetitions should be a number greater than 1");
    	}

    }
    
    private void setRepeatedMeasuresRowEnabled(int row, boolean enabled)
    {
    	TextBox repeats = (TextBox) repeatedMeasuresTable.getWidget(row, REPEATS_TEXTBOX_COLUMN);
    	TextBox units = (TextBox) repeatedMeasuresTable.getWidget(row, UNITS_TEXTBOX_COLUMN);

    	repeats.setEnabled(enabled);
    	units.setEnabled(enabled);
    	if (!enabled)
    	{
    		repeats.setText("");
    		units.setText("");
    		// remove any rows after this in the table
    		for(int r = repeatedMeasuresTable.getRowCount()-1; r > row; r--)
    			repeatedMeasuresTable.removeRow(r);
    	}
    }
    
    /**
     * Add a listener for outcome events
     * 
     * @param listener listener implementing the OutcomesListener interface
     */
    public void addOutcomesListener(OutcomesListener listener)
    {
    	listeners.add(listener);
    }
    
    /**
     * Notify listeners about updated list of outcomes
     */
    private void notifyOutcomes()
    {
    	List<String> outcomes = outcomesListPanel.getValues();
    	for(OutcomesListener listener: listeners) listener.onOutcomes(outcomes);
    }
    
    
    private void notifyRepeatedMeasures()
    {
    	ArrayList<RepeatedMeasure> rmList = new ArrayList<RepeatedMeasure>();
    	for(int r = 0; r < repeatedMeasuresTable.getRowCount(); r++)
    	{
    		String repeatStr = 
    			((TextBox) repeatedMeasuresTable.getWidget(r, REPEATS_TEXTBOX_COLUMN)).getText();
    		String unitStr = 
    			((TextBox) repeatedMeasuresTable.getWidget(r, UNITS_TEXTBOX_COLUMN)).getText();
    		if (!repeatStr.isEmpty() && !unitStr.isEmpty())
    		{
        		rmList.add(new RepeatedMeasure(unitStr, Integer.parseInt(repeatStr)));
    		}
    	}
    	for(OutcomesListener listener: listeners) listener.onRepeatedMeasures(rmList);

    }
    
    public void reset()
    {
    	
    }
    
    public void validateWidget(String value) throws IllegalArgumentException
    {
    	
    }
    
    public void onValidRowCount(int validRowCount)
    {

    }
    
    @Override
    public void onExit()
    {
    	notifyOutcomes();
    	notifyRepeatedMeasures();
    }

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}


}
