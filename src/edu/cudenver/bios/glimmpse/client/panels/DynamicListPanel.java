package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;

public class DynamicListPanel extends Composite
implements ChangeHandler
{
    // dynamic table of alpha values
    protected FlexTable flexTable = new FlexTable();
    // error display
    protected HTML errorHTML = new HTML();
    // object for validating new entries
    protected DynamicListValidator validator;
    // counter of the number of valid rows in the table
    protected int validRowCount = 0;
    
	public DynamicListPanel(String title, DynamicListValidator validator)
	{
		this.validator = validator;
        // create the dynamic table for entering predictors
        VerticalPanel tablePanel = new VerticalPanel();
        
        // set up the input table for alpha values
        flexTable.setWidget(0,0,new HTML(title));
        addRow();

        // layout the panels
        tablePanel.add(flexTable);
        tablePanel.add(errorHTML);
        
        // set style
        tablePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);
        flexTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        flexTable.getRowFormatter().setStylePrimaryName(0, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_COLUMN_HEADER);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        
        // initialize the panel
        initWidget(tablePanel);
	}
	
	public void addRow()
	{
        int row = flexTable.getRowCount();
        TextBox tb = new TextBox();
        tb.addChangeHandler(this);
        flexTable.setWidget(row, 0, tb);
        tb.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_TEXTBOX);
        flexTable.getRowFormatter().setStylePrimaryName(row, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
        tb.setFocus(true);
	}
	
	public String toXML(String tagName)
	{
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("<" + tagName + ">");
    	for(int i = 0; i < flexTable.getRowCount(); i++)
    	{
    		buffer.append("<v>");
    		TextBox tb = (TextBox) flexTable.getWidget(i, 0);
    		if (tb != null) buffer.append(tb.getText());
    		buffer.append("</v>");
    	}
    	buffer.append("</" + tagName + ">");
    	return buffer.toString();
	}
	
    public void onChange(ChangeEvent e)
    {
        TextBox source = (TextBox) e.getSource();
        try
        {
        	// get current row index
        	int focusRow = 1;
        	for(; focusRow < flexTable.getRowCount(); focusRow++)
        	{
        		if (source == flexTable.getWidget(focusRow, 0)) break;
        	}
        	if (source.getText().isEmpty() && flexTable.getRowCount() > 2)
        	{
        		flexTable.removeRow(focusRow);
        		validRowCount--;
        	}
        	else
        	{
        		validator.validate(source.getText());
        		validRowCount++;
        		if (focusRow == flexTable.getRowCount()-1) addRow();
        	}
        	// remove any previously displayed error messages
        	TextValidation.displayError(errorHTML, "");
        }
        catch (IllegalArgumentException iae)
        {
        	// display an error message
        	TextValidation.displayError(errorHTML, iae.getMessage());
        	source.setText("");
        	
        }
        // let the validator know that the number of valid rows has changed
        validator.onValidRowCount(validRowCount);
    }
    
    public int getFilledRowCount()
    {
    	// minus 
    	return flexTable.getRowCount() - 1;
    }
}
