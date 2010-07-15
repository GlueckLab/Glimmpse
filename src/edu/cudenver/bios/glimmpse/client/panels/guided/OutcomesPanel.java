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

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
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
    protected String[] outcomesColumnNames = {Glimmpse.constants.outcomesTableColumn()};
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
    				TextValidation.parseInteger(value, 1, true);
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
        // create the repeated measures header/instruction text
        HTML repeatedHeader = new HTML(Glimmpse.constants.repeatedMeasuresTitle());
        HTML repeatedDescription = new HTML(Glimmpse.constants.repeatedMeasuresDescription());
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(outcomesListPanel);
        panel.add(repeatedHeader);
        panel.add(repeatedDescription);
        panel.add(repeatedMeasuresListPanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        repeatedHeader.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        repeatedDescription.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
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
    	List<String> outcomes = outcomesListPanel.getColumnValues(0);
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
    
    @Override
    public void onExit()
    {
    	notifyOutcomes();
    }
}
