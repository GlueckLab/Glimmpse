package edu.cudenver.bios.glimmpse.client.panels;

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

public class OutcomesPanel extends Composite
{
    private static final String STYLE_TABLE_PANEL = "variableTablePanel";
    private static final String STYLE_TABLE_COLUMN_HEADER = "variableTableColumnHeader";
    private static final String STYLE_ROW = "variableTableRow";
    private static final String STYLE_ROW_ODD = "variableTableRow-odd";
    private static final String STYLE_ROW_EVEN = "variableTableRow-even";
    
    protected FlexTable outcomesTable = new FlexTable();
    protected ListBox numRepeatedListBox = new ListBox();
    private static final int MAX_REPEATED_MEASURES = 10;
    
    // sub panel headers
    protected SubpanelHeader outcomesHeader = new SubpanelHeader("Outcomes", 
            "dependent variables");

    

    public OutcomesPanel()
    {
        VerticalPanel panel = new VerticalPanel();
                
        // create the dynamic table for entering predictors
        VerticalPanel outcomesTablePanel = new VerticalPanel();
                
        outcomesTable.setWidget(0,0,new HTML("Outcomes"));
        addOutcomesRow();
        outcomesTablePanel.add(outcomesTable);

        // create the repeated measures panel
        HorizontalPanel repeatPanel = new HorizontalPanel();
        repeatPanel.add(new HTML("How many times was this set of outcomes repeated for each subject?"));
        repeatPanel.add(numRepeatedListBox);
        // fill the repeated list box
        for(int i = 1; i < MAX_REPEATED_MEASURES; i++) 
            numRepeatedListBox.addItem(Integer.toString(i));
        numRepeatedListBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                //updateQuestions();
            }
        });
        // set style
        outcomesTablePanel.setStyleName(STYLE_TABLE_PANEL);
        outcomesTable.getRowFormatter().setStylePrimaryName(0, STYLE_TABLE_COLUMN_HEADER);
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        // layout the overall subpanel
        panel.add(outcomesHeader);
        panel.add(new HTML("Please enter the outcomes (dependent variables) you measured."));
        panel.add(outcomesTablePanel);
        panel.add(repeatPanel);
        
        initWidget(panel);
    }
    
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
                //updateQuestions();
            }
        });
        outcomesTable.setWidget(row, 0, tb);
        tb.setFocus(true);
    }
}
