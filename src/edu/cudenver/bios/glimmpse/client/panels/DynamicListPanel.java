package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;

public class DynamicListPanel extends Composite
implements ChangeHandler
{
	public class RowTextBox extends TextBox
	{
		public int row = -1;
		
		public RowTextBox(int row)
		{
			super();
			this.row = row;
		}
	}
	
    // dynamic table of alpha values
    protected FlexTable flexTable = new FlexTable();
    // error display
    protected HTML errorHTML = new HTML();
    // object for validating new entries
    protected DynamicListValidator validator;
    // counter of the number of valid rows in the table
    protected int validRowCount = 0;
    
	public DynamicListPanel(String[] columnNames, DynamicListValidator validator)
	{
		this.validator = validator;
		
        // create the dynamic table for entering predictors
        VerticalPanel tablePanel = new VerticalPanel();
        
        // set up the input table for alpha values
        int col = 0;
        for(String columnName : columnNames) 
        {
        	flexTable.setWidget(0,col,new HTML(columnName));
        	col++;
        }
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
	
	private void addRow()
	{
        int row = flexTable.getRowCount();
        int cols = flexTable.getCellCount(0);
        
        for(int col = 0; col < cols; col++)
        {
        	RowTextBox rtb = new RowTextBox(row);
        	rtb.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_TEXTBOX);
        	rtb.addChangeHandler(this);
        	flexTable.setWidget(row, col, rtb);
        	if (col == 0) rtb.setFocus(true);
        }
        flexTable.getRowFormatter().setStylePrimaryName(row, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
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
		int columns = flexTable.getCellCount(0);
    	// get current row index
		RowTextBox source = (RowTextBox) e.getSource();
		int row = source.row;
		
		// validate the row
		boolean rowEmpty = true;
		boolean rowValid = true;
		for(int c = 0; c < columns; c++)
		{
			RowTextBox rtb = (RowTextBox) flexTable.getWidget(row, c);	
			if (!rtb.getText().isEmpty()) rowEmpty = false;		
			try 
			{
				validator.validate(rtb.getText(), c);
			}
			catch (IllegalArgumentException iae)
			{
				rtb.setText("");
				rowValid = false;
				if (rtb == source) TextValidation.displayError(errorHTML, iae.getMessage());
			}
		}

		// if the row is completely empty, remove it
		if (rowEmpty && flexTable.getRowCount() > 2)
		{
			flexTable.removeRow(row);
			validRowCount--;
        	TextValidation.displayOkay(errorHTML, "");
		}
		else if (rowValid)
		{
			if (row == flexTable.getRowCount()-1)
			{
				addRow();
				validRowCount++;
			}
        	TextValidation.displayOkay(errorHTML, "");
		}

        // let the validator know that the number of valid rows has changed
        validator.onValidRowCount(validRowCount);
    }
    
    public List<String> getColumnValues(int column)
    {
    	if (column < 0 || column > flexTable.getCellCount(0))
    		return null;
    	
    	ArrayList<String> list = new ArrayList<String>();
    	for(int i = 1; i < flexTable.getRowCount(); i++)
    	{
    		RowTextBox rtb = (RowTextBox)  flexTable.getWidget(i, column);	
    		String value = rtb.getText();
    		if (value != null && !value.isEmpty()) list.add(value);
    	}
    	return list;
    }
    
    public void reset()
    {
    	for(int i = flexTable.getRowCount() - 1; i > 0; i--)
    	{
    		flexTable.removeRow(i);
    	}
    	addRow();
    }
}
