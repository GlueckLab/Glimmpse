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
package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;

/**
 * OutcomesPanel
 * 
 * @author Sarah Kreidler
 *
 */
public class OutcomesPanel extends Composite
implements Clearable
{
    protected static final int MAX_REPEATED_MEASURES = 10;
    
    // dynamic table of outcomes
    protected FlexTable outcomesTable = new FlexTable();
    // number of repeated measures
    protected ListBox numRepeatedListBox = new ListBox();

    // listeners for outcome events
    protected ArrayList<OutcomesListener> listeners = new ArrayList<OutcomesListener>();

    public OutcomesPanel()
    {
        VerticalPanel panel = new VerticalPanel();
                
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.outcomesTitle());
        HTML description = new HTML(Glimmpse.constants.outcomesDescription());
        
        // create the dynamic table for entering predictors
        VerticalPanel outcomesTablePanel = new VerticalPanel();
                
        outcomesTable.setWidget(0,0,new HTML("Outcomes"));
        addOutcomesRow();
        outcomesTablePanel.add(outcomesTable);

        // create the repeated measures panel
        HorizontalPanel repeatPanel = new HorizontalPanel();
        repeatPanel.add(new HTML(Glimmpse.constants.outcomesLabelRepeated()));
        repeatPanel.add(numRepeatedListBox);
        // fill the repeated list box
        for(int i = 1; i <= MAX_REPEATED_MEASURES; i++) 
            numRepeatedListBox.addItem(Integer.toString(i));
        // notify listeners when the number of repetitions changes
        numRepeatedListBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
            	int reps = Integer.parseInt(numRepeatedListBox.getItemText(numRepeatedListBox.getSelectedIndex()));
                for(OutcomesListener listener: listeners) listener.onRepetitions(reps);
            }
        });

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        outcomesTablePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);
        outcomesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        outcomesTable.getRowFormatter().setStylePrimaryName(0, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_COLUMN_HEADER);

        // layout the overall subpanel
        panel.add(header);
        panel.add(description);
        panel.add(outcomesTablePanel);
        panel.add(repeatPanel);
        
        initWidget(panel);
    }
    
    
    /**
     * Add a new row to the outcomes table
     */
    private void addOutcomesRow()
    {
        int row = outcomesTable.getRowCount();
        TextBox tb = new TextBox();
        tb.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                TextBox source = (TextBox) e.getSource();
                // get current row index
                int focusRow = 1;
                for(; focusRow < outcomesTable.getRowCount(); focusRow++)
                {
                    if (source == outcomesTable.getWidget(focusRow, 0)) break;
                }
                if (source.getText().isEmpty() && outcomesTable.getRowCount() > 2)
                    outcomesTable.removeRow(focusRow);
                else
                    if (focusRow == outcomesTable.getRowCount()-1) addOutcomesRow();
                notifyOutcomes();
            }
        });
        outcomesTable.setWidget(row, 0, tb);
        tb.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_TEXTBOX);
        outcomesTable.getRowFormatter().setStylePrimaryName(row, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
        tb.setFocus(true);
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
    	for(int r = 1; r < outcomesTable.getRowCount()-1; r++)
    	{
    		TextBox tb = (TextBox) outcomesTable.getWidget(r, 0);
    		String text = tb.getText();
    		if (!text.isEmpty()) outcomes.add(tb.getText());
    	}
    	for(OutcomesListener listener: listeners) listener.onOutcomes(outcomes);
    }
    
    public void clear()
    {
    	
    }
}
