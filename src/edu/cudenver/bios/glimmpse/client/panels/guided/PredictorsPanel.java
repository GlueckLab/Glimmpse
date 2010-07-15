package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.CovariatePanel;

public class PredictorsPanel extends WizardStepPanel
{    
    // dynamic table of repeated measures
    protected String[] predictorsColumnNames = {
    		Glimmpse.constants.predictorsTableColumn(),
    		Glimmpse.constants.categoriesTableColumn(),
    		};
    protected DynamicListPanel predictorsListPanel = 
    	new DynamicListPanel(predictorsColumnNames, 
    			new DynamicListValidator() {
    		public void validate(String value, int column) throws IllegalArgumentException {}

    		public void onValidRowCount(int validRowCount)
    		{}
    	});

    // popup panel for editing categories
    protected PopupPanel categoryPopup = new PopupPanel();    
    protected HTML categoryTarget = null;
    protected String[] categoryColumnNames = {
    	Glimmpse.constants.categoriesTableColumn()	
    };
    protected DynamicListPanel categoryListPanel = 
    	new DynamicListPanel(categoryColumnNames, new DynamicListValidator()
    	{
			@Override
			public void onValidRowCount(int validRowCount)
			{}

			@Override
			public void validate(String value, int column)
					throws IllegalArgumentException
			{
				if (value.contains(",")) 
					throw new IllegalArgumentException("Category names cannot contain comma's");		
			}
    	});
    
    // covariate panel
    protected CovariatePanel covariatePanel = new CovariatePanel();
    // listeners for outcome events
    protected ArrayList<PredictorsListener> listeners = new ArrayList<PredictorsListener>();
    
    public PredictorsPanel()
    {
    	super(Glimmpse.constants.stepsLeftPredictors());
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.predictorsTitle());
        HTML description = new HTML(Glimmpse.constants.predictorsDescription());
        
        // build the category panel
        VerticalPanel categoryPanel = new VerticalPanel();
        Button doneButton = new Button("Done", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				hideCategories();
			}
        });
        categoryPanel.add(categoryListPanel);
        categoryPanel.add(doneButton);
        categoryPopup.add(categoryPanel);

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(predictorsListPanel);
        panel.add(covariatePanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        initWidget(panel);
    }
    
    private void showCategories(HTML target)
    {
    	categoryTarget = target;
    	categoryPopup.showRelativeTo(target);
    	categoryListPanel.reset();
    }
    
    private void hideCategories()
    {
    	List<String> categories = categoryListPanel.getColumnValues(0);
    	if (categories.size() > 0)
    	{
    		StringBuffer buffer = new StringBuffer();
    		boolean first = true;
    		for(String category: categories)
    		{
    			if (!first) 
    				buffer.append(", ");
    			else
    				first = false;
    			buffer.append(category);
    		}
    		categoryTarget.setText(buffer.toString());
    	}
    	else
    	{
    		categoryTarget.setText("(click to edit)");
    	}

    	categoryPopup.hide();
    }
    
    public void onExit()
    {
    	// TODO: send notifications
    }
    
    public void reset() 
    {
    	predictorsListPanel.reset();
    }
}
