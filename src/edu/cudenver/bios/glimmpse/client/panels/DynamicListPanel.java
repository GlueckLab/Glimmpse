package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
	public class DefaultWidgetManager implements DynamicListManager {

		@Override
		public Widget createListWidget(ChangeHandler handler, int column)
		{
	        TextBox tb = new TextBox();
	        tb.addChangeHandler(handler);
	        tb.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_TEXTBOX);
	        if (column == 0) tb.setFocus(true);
	        return tb;
		}

		@Override
		public String getValue(Widget w, int column)
		{
			TextBox tb = (TextBox) w;
			return tb.getText();
		}

		@Override
		public void clear(Widget w, int column)
		{
			TextBox tb = (TextBox) w;
			tb.setText("");
		}
	};
	
    // dynamic table of alpha values
    protected FlexTable flexTable = new FlexTable();
    // error display
    protected HTML errorHTML = new HTML();
    // object for validating new entries
    protected DynamicListValidator validator;
    // object which creates / accesses widgets in each column
    protected DynamicListManager manager;
    // counter of the number of valid rows in the table
    protected int validRowCount = 0;
    
	public DynamicListPanel(String[] columnNames, DynamicListValidator validator,
			DynamicListManager manager)
	{
		this.validator = validator;
		if (manager != null)
			this.manager = manager;
		else
			this.manager = new DefaultWidgetManager();
		
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
	
	public DynamicListPanel(String[] columnNames, DynamicListValidator validator)
	{
		this(columnNames, validator, null);
	}
	
	private void addRow()
	{
        int row = flexTable.getRowCount();
        int cols = flexTable.getCellCount(0);
        
        for(int col = 0; col < cols; col++)
        {
        	flexTable.setWidget(row, col, manager.createListWidget(this, col));
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
        Widget source = (Widget) e.getSource();
    	int focusRow = 1;
    	for(; focusRow < flexTable.getRowCount(); focusRow++)
    	{
    		if (source == flexTable.getWidget(focusRow, 0)) break;
    	}
    	
        try
        {
        	String value = manager.getValue(source, 0);
        	// use the first widget in the row to determine if we delete the row
        	if (value.isEmpty() && flexTable.getRowCount() > 2)
        	{
        		flexTable.removeRow(focusRow);
        		validRowCount--;
        	}
        	else
        	{
        		// validate over entire row
        		for(int c = 0; c < columns; c++)
        		{
        			Widget w = flexTable.getWidget(focusRow, c);
            		validator.validate(manager.getValue(w, c), c);
        		}
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
        	for(int c = 0; c < columns; c++) 
        		manager.clear(flexTable.getWidget(focusRow, c), c);
        	
        }
        // let the validator know that the number of valid rows has changed
        validator.onValidRowCount(validRowCount);
    }
    
    public int getFilledRowCount()
    {
    	// minus 
    	return flexTable.getRowCount() - 1;
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
