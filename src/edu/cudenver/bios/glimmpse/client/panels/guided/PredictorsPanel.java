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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.CovariatePanel;

public class PredictorsPanel extends WizardStepPanel
{        
    // covariate panel
    protected CovariatePanel covariatePanel = new CovariatePanel();
    // listeners for outcome events
    protected ArrayList<PredictorsListener> listeners = new ArrayList<PredictorsListener>();

    // list box displaying predictors
    protected ListBox predictorList = new ListBox();
    protected ListBox categoryList = new ListBox();
    // text boxes for entering predictor / category values
    protected TextBox predictorTextBox = new TextBox();
    protected TextBox categoryTextBox = new TextBox();
    // control buttons for predictor/category deletion
    protected Button predictorDeleteButton = new Button("Delete Predictor");
    protected Button categoryDeleteButton = new Button("Delete Category");
    
    protected HashMap<String,ArrayList<String>> predictorCategoryMap = 
    	new HashMap<String,ArrayList<String>>();
    
    public PredictorsPanel()
    {
    	super(Glimmpse.constants.stepsLeftPredictors());
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.predictorsTitle());
        HTML description = new HTML(Glimmpse.constants.predictorsDescription());        
        HTML covariateHeader = new HTML("Baseline Covariate");
        HTML covariateDescription = new HTML("A baseline covariate is...");

        // disable category text box and delete buttons
        categoryTextBox.setEnabled(false);
        categoryDeleteButton.setEnabled(false);
        predictorDeleteButton.setEnabled(false);

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(buildCascadingList());
        panel.add(covariateHeader);
        panel.add(covariateDescription);
        panel.add(covariatePanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        covariateHeader.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        covariateDescription.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        initWidget(panel);
    }    
    
    private VerticalPanel buildCascadingList()
    {
    	VerticalPanel panel = new VerticalPanel();

    	predictorList.setVisibleItemCount(10);
    	predictorList.setWidth("100%");
    	categoryList.setVisibleItemCount(10);
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
    	predictorPanel.add(new HTML("Predictor: "));
    	predictorPanel.add(predictorTextBox);
    	HorizontalPanel categoryPanel = new HorizontalPanel();
    	categoryPanel.add(new HTML("Catgeory: "));
    	categoryPanel.add(categoryTextBox);
    	
    	// layout the panels
    	Grid grid = new Grid(3,2);
    	grid.setWidget(0, 0, predictorPanel);
    	grid.setWidget(0, 1, categoryPanel);
    	grid.setWidget(1, 0, predictorList);
    	grid.setWidget(1, 1, categoryList);
    	grid.setWidget(2, 0, predictorDeleteButton);
    	grid.setWidget(2, 1, categoryDeleteButton);
    	panel.add(grid);
    	
    	// set style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);
    	grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        grid.getRowFormatter().setStylePrimaryName(0, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_COLUMN_HEADER);
    	
    	
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
    
    public void onExit()
    {
    	for(PredictorsListener listener: listeners) listener.onPredictors(predictorCategoryMap);
    }
    
    public void addPredictorsListener(PredictorsListener listener)
    {
    	listeners.add(listener);
    }
    
    public void addCovariateListener(CovariateListener listener)
    {
    	covariatePanel.addCovariateListener(listener);
    }
    
    public void checkComplete()
    {
    	boolean isComplete = false;
    	for(ArrayList<String> categories: predictorCategoryMap.values())
    	{
    		if (categories.size() > 1) 
    		{
    			isComplete = true;
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
}
