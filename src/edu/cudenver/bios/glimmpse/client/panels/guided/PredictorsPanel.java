package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.SubpanelHeader;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class PredictorsPanel extends WizardStepPanel
{
    // styles for the variable entry tables
    private static final String STYLE_TABLE_PANEL = "variableTablePanel";
    private static final String STYLE_TABLE_COLUMN_HEADER = "variableTableColumnHeader";
    private static final String STYLE_ROW = "variableTableRow";
    private static final String STYLE_ROW_ODD = "variableTableRow-odd";
    private static final String STYLE_ROW_EVEN = "variableTableRow-even";
    
    protected SubpanelHeader predictorsHeader = new SubpanelHeader("predictors", 
            "predictros");
    protected ListBox numPredictorsListbox = new ListBox();
    protected FlexTable predictorTable = new FlexTable();
    protected PopupPanel categoryPopup = new PopupPanel();
    protected FlexTable categoryTable = new FlexTable();
    protected HTML categoryTarget = null;
    
    public PredictorsPanel()
    {
    	super(Glimmpse.constants.stepsLeftPredictors());
        VerticalPanel panel = new VerticalPanel();
        
        // create the dynamic table for entering predictors
        VerticalPanel predictorTablePanel = new VerticalPanel();

        // build the category editting popup
        VerticalPanel categoryPanel = new VerticalPanel();
        categoryTable.setWidget(0, 0, new HTML("Categories:"));
        addCategoryRow(null);
        categoryPanel.add(categoryTable);
        categoryPanel.add(new Button("Done", new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                // populate the category list
                StringBuffer buffer = new StringBuffer();
                for(int r = 1; r < categoryTable.getRowCount(); r++)
                {
                    TextBox tb = (TextBox) categoryTable.getWidget(r, 0);
                    String text = tb.getText();
                    if (!text.isEmpty()) 
                    {
                        if (r != 1) buffer.append(",");
                        buffer.append(text);
                    }
                    if (categoryTarget != null) categoryTarget.setText(buffer.toString());
                }
                // clear the table
                for(int r = categoryTable.getRowCount()-1; r >= 1; r--) 
                    categoryTable.removeRow(r);
                addCategoryRow(null);
                // close the popup
                categoryPopup.hide();
                categoryTarget = null;
            }
        }));
        categoryPopup.add(categoryPanel);
        // add column headers to the predictor input table 
        predictorTable.setWidget(0,0,new HTML("Predictor:"));
        predictorTable.setWidget(0,1,new HTML("Categories:"));
        addPredictorsRow();
        predictorTablePanel.add(predictorTable);

        // set style
        predictorTablePanel.setStyleName(STYLE_TABLE_PANEL);
        predictorTable.getRowFormatter().setStylePrimaryName(0, STYLE_TABLE_COLUMN_HEADER);

        // layout the predictor subpanel
        panel.add(predictorsHeader);
        panel.add(new HTML("Please enter any fixed predictors and the number of possible values for each predictor."));
        panel.add(predictorTablePanel);
        initWidget(panel);
    }
    
    private void addCategoryRow(String value)
    {
        int row = categoryTable.getRowCount();
        TextBox tb = new TextBox();
        tb.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                TextBox source = (TextBox) e.getSource();
                // get current row index
                int focusRow = 1;
                for(; focusRow < categoryTable.getRowCount(); focusRow++)
                {
                    if (source == categoryTable.getWidget(focusRow, 0)) break;
                }
                if (source.getText().isEmpty())
                    categoryTable.removeRow(focusRow);
                else
                    if (focusRow == categoryTable.getRowCount()-1) addCategoryRow(null);
            }
        });
        categoryTable.setWidget(row, 0, tb);
        if (value != null) tb.setText(value); 
        tb.setFocus(true);
    }
    
    private void addPredictorsRow()
    {
        int row = predictorTable.getRowCount();
        
        // name entry text box
        TextBox tb = new TextBox();
        tb.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                TextBox source = (TextBox) e.getSource();
                // get current row index
                int focusRow = 1;
                for(; focusRow < predictorTable.getRowCount(); focusRow++)
                {
                    if (source == predictorTable.getWidget(focusRow, 0)) break;
                }
                if (source.getText().isEmpty())
                    predictorTable.removeRow(focusRow);
                else
                {
                    HTML catEdit = (HTML) predictorTable.getWidget(focusRow, 1);
                    if (catEdit.getHTML().isEmpty()) catEdit.setHTML("label");
                    if (focusRow == predictorTable.getRowCount()-1) addPredictorsRow();
                }
            }
        });
        
        // category editting popup
        HTML categories = new HTML();
        categories.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                HTML h = (HTML) e.getSource();
                if ("label".equals(h.getHTML()))
                    showCategoryPopup(null, h);
                else
                    showCategoryPopup(h.getHTML(), h);
            }
        });

        // add the widgets and set focus to the new row
        predictorTable.setWidget(row, 0, tb);
        predictorTable.setWidget(row, 1, categories);
        tb.setFocus(true);        
    }
    
    private void showCategoryPopup(String list, HTML target)
    {
        if (list != null)
        {
            String[] categories = list.split(",");
            for(int i = 0; i < categories.length; i++)
            {
                if (!categories[i].isEmpty())
                    addCategoryRow(categories[i]);
            }
        }
        categoryTarget = target;
        categoryPopup.showRelativeTo(target);
        
    }
    
    public void reset() {}
}
