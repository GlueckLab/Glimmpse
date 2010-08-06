package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;

public class ListEntryPanel extends Composite
{
	protected static final int HEADER_ROW = 0;
	protected static final int LISTBOX_ROW = 1;
	protected static final int DELETE_ROW = 2;
	
    // text entry box
    protected TextBox listEntryTextBox = new TextBox();
    // list of current entries
    protected ListBox listBox = new ListBox(true);    
    // error display
    protected HTML errorHTML = new HTML();
    // object for validating new entries
    protected ListValidator validator;
    // counter of the number of valid rows in the table
    protected int validRowCount = 0;
    
	public ListEntryPanel(String columnName, ListValidator validator)
	{
		this.validator = validator;
		
        // create the dynamic table for entering predictors
        VerticalPanel tablePanel = new VerticalPanel();
        
        // set up the input table 
        Grid layoutGrid = new Grid(3, 1);
        layoutGrid.setWidget(HEADER_ROW,0,createTextEntry(columnName));
        layoutGrid.setWidget(LISTBOX_ROW,0, listBox);
        Button deleteButton = new Button(("Delete "), 
        		new ClickHandler() {
        	@Override
        	public void onClick(ClickEvent event)
        	{
        		removeSelectedListItems();
        	}
        });
        layoutGrid.setWidget(DELETE_ROW, 0, deleteButton);
        //addRow();

        // layout the panels
        tablePanel.add(layoutGrid);
        tablePanel.add(errorHTML);
        
        // set style
        listBox.setWidth("100%"); // TODO: stylesheet this info!
        listBox.setVisibleItemCount(5);
        tablePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);
        layoutGrid.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        layoutGrid.getRowFormatter().setStylePrimaryName(0, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_COLUMN_HEADER);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        
        // initialize the panel
        initWidget(tablePanel);
	}
	
	private HorizontalPanel createTextEntry(String columnName)
	{
		HorizontalPanel panel = new HorizontalPanel();
		listEntryTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				addListIem();
			}
		});
		panel.add(new HTML(columnName + ": "));
		panel.add(listEntryTextBox);
		return panel;
	}
	
	private void addListIem()
	{
		String value = listEntryTextBox.getValue();
		if (value != null && !value.isEmpty())
		{
			try
			{
				validator.validate(value);
				listBox.addItem(value);
				TextValidation.displayOkay(errorHTML, "");
				validator.onValidRowCount(listBox.getItemCount());
			}
			catch (IllegalArgumentException iae)
			{
				TextValidation.displayError(errorHTML, iae.getMessage());
			}
			finally
			{
				listEntryTextBox.setText("");
			}
		}
	}
	
	private void removeSelectedListItems()
	{
		for(int i = listBox.getItemCount()-1; i >= 0; i--)
		{
			if (listBox.isItemSelected(i)) listBox.removeItem(i);
		}
	}
	
	public String toXML(String tagName)
	{
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("<" + tagName + ">");
    	for(int i = 0; i < listBox.getItemCount(); i++)
    	{
        		buffer.append("<v>");
        		buffer.append(listBox.getItemText(i));
        		buffer.append("</v>");	
    	}
    	buffer.append("</" + tagName + ">");
    	return buffer.toString();
	}
	
    public int getValidRowCount()
    {
    	return listBox.getItemCount();
    }
    
    public void reset()
    {
    	listBox.clear();
    }
}
