package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class CategoricalPredictorsPanel extends WizardStepPanel
{
    // listeners for outcome events
    protected ArrayList<PredictorsListener> listeners = new ArrayList<PredictorsListener>();

    // list box displaying predictors
    protected ListBox predictorList = new ListBox();
    protected ListBox categoryList = new ListBox();
    // text boxes for entering predictor / category values
    protected TextBox predictorTextBox = new TextBox();
    protected TextBox categoryTextBox = new TextBox();
    // control buttons for predictor / category add
    protected Button predictorAddButton = new Button(Glimmpse.constants.buttonAdd());
    protected Button categoryAddButton = new Button(Glimmpse.constants.buttonAdd());
    // control buttons for predictor/category deletion
    protected Button predictorDeleteButton = new Button(Glimmpse.constants.buttonDelete());
    protected Button categoryDeleteButton = new Button(Glimmpse.constants.buttonDelete());
    
    protected HashMap<String,ArrayList<String>> predictorCategoryMap = 
    	new HashMap<String,ArrayList<String>>();
    
    public CategoricalPredictorsPanel()
    {
    	super();
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.categoricalTitle());
        HTML description = new HTML(Glimmpse.constants.categoricalDescription());        


        // disable category text box and delete buttons
        categoryTextBox.setEnabled(false);
        categoryDeleteButton.setEnabled(false);
        predictorDeleteButton.setEnabled(false);

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(buildCascadingList());
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
    }    
        
    private VerticalPanel buildCascadingList()
    {
    	VerticalPanel panel = new VerticalPanel();

    	predictorList.setVisibleItemCount(5);
    	predictorList.setWidth("100%");
    	categoryList.setVisibleItemCount(5);
    	categoryList.setWidth("100%");
    	
    	// add callbacks
    	predictorTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				String value = predictorTextBox.getText();
				if (!value.isEmpty())
				{
					addPredictor(value);
					int selectedIndex = predictorList.getItemCount()-1;
					predictorList.setSelectedIndex(selectedIndex);
					selectPredictor(selectedIndex);
					predictorTextBox.setText("");
				}
			}
    	});
    	predictorList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				selectPredictor(predictorList.getSelectedIndex());
			}
    	});
    	predictorDeleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				int selectedIndex = predictorList.getSelectedIndex();
				if (selectedIndex != -1)
				{
					deletePredictor(predictorList.getItemText(selectedIndex));
					predictorList.removeItem(selectedIndex);
					categoryList.clear();
				}
			}
    	});
    	categoryTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				String value = categoryTextBox.getText();
				if (!value.isEmpty())
				{
					String predictor = predictorList.getItemText(predictorList.getSelectedIndex());
					if (predictor != null && !predictor.isEmpty())
					{
						addCategory(predictor, value);
					}
					categoryTextBox.setText("");
				}
			}
    	});
    	categoryList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event)
			{
				categoryDeleteButton.setEnabled((categoryList.getSelectedIndex() != -1));
			}
    	});
    	categoryDeleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				int predictorIndex = predictorList.getSelectedIndex();
				if (predictorIndex != -1)
				{
					String predictor = predictorList.getItemText(predictorIndex);
					int categoryIndex = categoryList.getSelectedIndex();
					if (categoryIndex != -1)
					{
						String category = categoryList.getItemText(categoryIndex);
						deleteCategory(predictor, category);
						categoryList.removeItem(categoryIndex);
					}
				}
			}
    	});
    	// build text entry panels
    	HorizontalPanel predictorPanel = new HorizontalPanel();
    	predictorPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
    	VerticalPanel predictorTextContainer = new VerticalPanel();
    	predictorTextContainer.add(new HTML(Glimmpse.constants.predictorsTableColumn()));
    	predictorTextContainer.add(predictorTextBox);
    	predictorPanel.add(predictorTextContainer);
    	predictorPanel.add(predictorAddButton);
    	predictorPanel.add(predictorDeleteButton);

    	HorizontalPanel categoryPanel = new HorizontalPanel();
    	categoryPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
    	VerticalPanel categoryTextContainer = new VerticalPanel();
    	categoryTextContainer.add(new HTML(Glimmpse.constants.categoriesTableColumn()));
    	categoryTextContainer.add(categoryTextBox);
    	categoryPanel.add(categoryTextContainer);
    	categoryPanel.add(categoryAddButton);
    	categoryPanel.add(categoryDeleteButton);
    	
    	// layout the panels
    	Grid grid = new Grid(2,2);
    	grid.setWidget(0, 0, predictorPanel);
    	grid.setWidget(0, 1, categoryPanel);
    	grid.setWidget(1, 0, predictorList);
    	grid.setWidget(1, 1, categoryList);
    	panel.add(grid);
    	
    	// set style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_PANEL);
    	predictorAddButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
    	predictorDeleteButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
    	categoryAddButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
    	categoryDeleteButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
    	
    	//grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST);
        grid.getRowFormatter().setStylePrimaryName(0, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_LIST_HEADER);
    	
    	
    	return panel;
    }
    
    private void showCategories(String predictor)
    {
    	ArrayList<String> categories = predictorCategoryMap.get(predictor);
		categoryList.clear();
    	for(String category: categories)
    	{
    		categoryList.addItem(category);
    	}
    	// disable the delete categories button
    	categoryDeleteButton.setEnabled(false);
    }
    
    private void addPredictor(String name)
    {
    	if (!predictorCategoryMap.containsKey(name))
    	{
    		predictorList.addItem(name);
    		predictorCategoryMap.put(name, new ArrayList<String>());
    	}
    }
    
    private void selectPredictor(int selectedIndex)
    {
		predictorDeleteButton.setEnabled((selectedIndex != -1));
		categoryTextBox.setEnabled((selectedIndex != -1));
		if (selectedIndex != -1)
		{
			String predictorName = predictorList.getItemText(selectedIndex);
			showCategories(predictorName);
		}
    }
    
    private void deletePredictor(String name)
    {
    	predictorCategoryMap.remove(name);
    }
    
    private void addCategory(String predictor, String category)
    {
    	ArrayList<String> categories = predictorCategoryMap.get(predictor);
    	if (categories != null) 
    	{
    		categories.add(category);
			categoryList.addItem(category);
    	}
		checkComplete();
    }
    
    private void deleteCategory(String predictor, String category)
    {
    	ArrayList<String> categories = predictorCategoryMap.get(predictor);
    	if (categories != null)  categories.remove(category);
		checkComplete();
    }
    
    private DataTable buildGroupTable()
    {
    	DataTable data = DataTable.create();
    	
    	if (predictorCategoryMap.size() > 0)
    	{
    		int rows = 1;
    		int col = 0;
    		for(String predictor: predictorCategoryMap.keySet())
    		{
    			data.addColumn(ColumnType.STRING, predictor);
    			rows *= predictorCategoryMap.get(predictor).size();
    		}
    		data.addRows(rows);
    		
    		int previousRepeat = 0;
    		col = 0;
    		for(String predictor: predictorCategoryMap.keySet())
    		{
    			int row = 0;
				ArrayList<String> categories = predictorCategoryMap.get(predictor);
				if (previousRepeat == 0)
				{
					previousRepeat = rows / categories.size();
					for(String category: categories)
					{
						for (int reps = 0; reps < previousRepeat; reps++, row++) 
						{
							data.setCell(row, col, category, category, null);
						}
					}
				}
				else
				{
					int categorylistRepeat = rows / previousRepeat;
					previousRepeat = previousRepeat / categories.size();
					for(int categoryListRep = 0; categoryListRep < categorylistRepeat; categoryListRep++)
					{
						for(String category: categories)
						{
							for (int reps = 0; reps < previousRepeat; reps++, row++) 
							{
								data.setCell(row, col, category, category, null);
							}
						}
					}
				}
				col++;
    		}
    	}
    	return data;
    }
    
    
    public void onExit()
    {
    	DataTable groups = buildGroupTable();
    	for(PredictorsListener listener: listeners) listener.onPredictors(predictorCategoryMap, groups);
    }
    
    
    public void addPredictorsListener(PredictorsListener listener)
    {
    	listeners.add(listener);
    }
    
    public void checkComplete()
    {
    	boolean isComplete = true;
    	for(ArrayList<String> categories: predictorCategoryMap.values())
    	{
    		if (categories.size() < 1) 
    		{
    			isComplete = false;
    			break;
    		}
    	}
    	if (isComplete)
    		notifyComplete();
    	else
    		notifyInProgress();
    }
    
    public void reset() 
    {
    	predictorList.clear();
    	categoryList.clear();
    	predictorCategoryMap.clear();
    }

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}

}
