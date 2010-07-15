package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class WizardPanel extends Composite
implements NavigationListener, StepStatusListener
{
	// style
	protected static final String STYLE_TOOLBAR = "wizardToolBar";
	protected static final String STYLE_SAVE = "wizardToolBarButtonSave";
	protected static final String STYLE_CANCEL = "wizardToolBarButtonCancel";
	protected static final String STYLE_CLEAR = "wizardToolBarButtonClear";
	// url for file save web service
	protected static final String SAVEAS_URL = "/webapps/file/saveas"; 
    // form for saving the study design
	protected FormPanel form = new FormPanel("_blank");
	protected Hidden matrixXML = new Hidden("data");
	
	// navigation event listeners
    ArrayList<CancelListener> listeners = new ArrayList<CancelListener>();
	
	// main panel
	HorizontalPanel panel = new HorizontalPanel();
	// left navigation / "steps left" panel
    protected StepsLeftPanel stepsLeftPanel;
	// index of currently visible step
    protected int currentStep = 0;    
    // deck panel containing all steps in the input wizard
	DeckPanel wizardDeck = new DeckPanel();
	// top toolbar (save, clear, start over)
	// navigation buttons
	NavigationPanel navPanel = new NavigationPanel();
	
	/**
	 * Create an empty matrix panel
	 */
	public WizardPanel(WizardStepPanel[] stepPanels)
	{		
		VerticalPanel contentPanel = new VerticalPanel();
		
		// initialize the steps left panel
		stepsLeftPanel = new StepsLeftPanel(stepPanels);
		
		// add the content panels to the deck
		for(WizardStepPanel stepPanel: stepPanels)
		{
			wizardDeck.add(stepPanel);
			stepPanel.addStepStatusListener(this);
		}
		
		// add navigation callbacks
		navPanel.addNavigationListener(this);
		stepsLeftPanel.addNavigationListener(this);
		navPanel.addNavigationListener(stepsLeftPanel);
		enterStep();
		
		// layout the wizard panel
		contentPanel.add(createToolBar());
		contentPanel.add(wizardDeck);
		contentPanel.add(navPanel);
		panel.add(stepsLeftPanel);		
		panel.add(contentPanel);

		// set style
		// TODO: finish style
		//panel.setStyleName(LAYOUT_STYLE);		

		// initialize
		initWidget(panel);
	}
	
    /**
     * Call back when "next" navigation button is clicked
     * Does nothing if already at end of step list
     */
    public void onNext()
    {
    	if (currentStep < wizardDeck.getWidgetCount()-1)
    	{
    		exitStep();
    		currentStep++;
    		enterStep();
    	}
    }
	
    /**
     * Navigate to a specific step
     */
    public void onStep(int stepIndex)
    {
    	if (stepIndex >= 0 && stepIndex < wizardDeck.getWidgetCount())
    	{
    		exitStep();
    		currentStep = stepIndex;
    		enterStep();
    	}
    }
    
    /**
     * Allow forward navigation when user input to the current step is complete.
     */
    public void onStepComplete()
    {
    	navPanel.setNext(true);
    }
    
    /**
     * Disallow forward navigation when user input to the current step is not complete.
     */
    public void onStepInProgress()
    {
    	navPanel.setNext(false);
    }
    
    /**
     * Clear data from all panels in the wizard 
     */
    public void reset()
    {
    	for(int i = 0; i < wizardDeck.getWidgetCount(); i++)
    	{
    		WizardStepPanel step = (WizardStepPanel) wizardDeck.getWidget(i);
    		step.reset();
    	}
    	stepsLeftPanel.reset();
    }

    /**
     * Call any exit functions as we leave the current step
     */
    private void exitStep()
    {
    	WizardStepPanel w = (WizardStepPanel) wizardDeck.getWidget(currentStep);
    	w.onExit();
    }

    /**
     *  Enter the new step, calling any setup routines
     */
    private void enterStep()
    {
    	WizardStepPanel w = (WizardStepPanel) wizardDeck.getWidget(currentStep);
    	w.onEnter();
    	wizardDeck.showWidget(currentStep);
    	navPanel.setNext(w.isComplete());
    }
    
    private HorizontalPanel createToolBar()
    {
		HorizontalPanel panel = new HorizontalPanel();
		
		// add the save study link and associated form
		form.setAction(SAVEAS_URL);
		form.setMethod(FormPanel.METHOD_POST);
		VerticalPanel formContainer = new VerticalPanel();
		formContainer.add(matrixXML);
		formContainer.add(new Hidden("filename", "study.xml"));
		form.add(formContainer);
		// save button
		Button saveButton = new Button("Save", new ClickHandler() {
			public void onClick(ClickEvent e)
			{
				// TODO: set study text into matrixXML hidden field
		    	form.submit();    	
			}
		});
		// clear button
		Button clearButton = new Button("Clear", new ClickHandler() {
			public void onClick(ClickEvent e)
			{
				// TODO: Dialog box
		    	WizardStepPanel w = (WizardStepPanel) wizardDeck.getWidget(currentStep);
		    	w.reset();
			}
		});
		// cancel button
		Button cancelButton = new Button("New", new ClickHandler() {
			public void onClick(ClickEvent e)
			{
				notifyOnCancel();
			}
		});
		
		// layout the panel
		panel.add(saveButton);
		panel.add(clearButton);
		panel.add(cancelButton);
		// set style
		panel.setStyleName(STYLE_TOOLBAR);
		saveButton.setStyleName(STYLE_SAVE);
		clearButton.setStyleName(STYLE_CLEAR);
		cancelButton.setStyleName(STYLE_CANCEL);
		
		return panel;
    }

    protected void notifyOnCancel()
    {
        for(CancelListener listener: listeners)
            listener.onCancel();
    }
    
    public void addCancelListener(CancelListener listener)
    {
        listeners.add(listener);
    }
}
