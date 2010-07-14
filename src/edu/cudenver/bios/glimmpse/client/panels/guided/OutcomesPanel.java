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

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListManager;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * OutcomesPanel
 * 
 * @author Sarah Kreidler
 *
 */
public class OutcomesPanel extends WizardStepPanel
{
    protected static final int MAX_REPEATED_MEASURES = 10;
    
    // dynamic table of outcomes
    protected String[] outcomesColumnNames = {Glimmpse.constants.outcomesTableTitle()};
    protected DynamicListPanel outcomesListPanel = 
    	new DynamicListPanel(outcomesColumnNames, 
    			new DynamicListValidator() {
    		public void validate(String value, int column) throws IllegalArgumentException {}

    		public void onValidRowCount(int validRowCount)
    		{
    			if (validRowCount > 0)
    				notifyComplete();
    			else
    				notifyInProgress();
    		}
    	});
    
    // dynamic table of repeated measures
    protected String[] repeatedColumnNames = {"Repeated Over", "#repetitions"};
    protected DynamicListPanel repeatedMeasuresListPanel = 
    	new DynamicListPanel(repeatedColumnNames, 
    			new DynamicListValidator() {
    		public void validate(String value, int column) throws IllegalArgumentException {}

    		public void onValidRowCount(int validRowCount)
    		{
    			if (validRowCount > 0)
    				notifyComplete();
    			else
    				notifyInProgress();
    		}
    	},
    	new DynamicListManager() {

			@Override
			public Widget createListWidget(ChangeHandler handler, int column)
			{
				if (column ==  0)
				{
			        TextBox tb = new TextBox();
			        tb.addChangeHandler(handler);
			        tb.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_TEXTBOX);
			        tb.setFocus(true);
			        return tb;
				}
				else
				{
					ListBox lb = new ListBox();
					for(int i = 1; i <= MAX_REPEATED_MEASURES; i++)
						lb.addItem(Integer.toString(i));
					return lb;
				}
			}

			@Override
			public String getValue(Widget w, int column)
			{
				String value = null;
				if (column ==  0)
				{
					TextBox tb = (TextBox) w;
					value = tb.getText();
				}
				else if (column == 1)
				{
					ListBox lb = (ListBox) w;
					value = lb.getItemText(lb.getSelectedIndex());
				}
				return value;
			}

			@Override
			public void clear(Widget w, int column)
			{
				if (column == 0)
				{
					TextBox tb = (TextBox) w;
					tb.setText("");
				}
				else if (column == 1)
				{
					ListBox lb = (ListBox) w;
					lb.setSelectedIndex(0);
				}
				
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
        
        // create the dynamic table for entering predictors
        VerticalPanel outcomesTablePanel = new VerticalPanel();
        outcomesTablePanel.add(outcomesListPanel);
        outcomesTablePanel.add(repeatedMeasuresListPanel);
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(outcomesTablePanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        outcomesTablePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);

        initWidget(panel);
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
    	ArrayList<String> outcomes = new ArrayList<String>();
//    	for(int r = 1; r < outcomesTable.getRowCount()-1; r++)
//    	{
//    		TextBox tb = (TextBox) outcomesTable.getWidget(r, 0);
//    		String text = tb.getText();
//    		if (!text.isEmpty()) outcomes.add(tb.getText());
//    	}
    	for(OutcomesListener listener: listeners) listener.onOutcomes(outcomes);
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
}
