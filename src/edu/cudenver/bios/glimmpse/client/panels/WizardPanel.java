package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
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
    	wizardDeck.showWidget(currentStep);
    	navPanel.setNext(w.isComplete());
    	w.onEnter();
    }
    
    private HorizontalPanel createToolBar()
    {
		HorizontalPanel panel = new HorizontalPanel();
		
	    MenuBar menu = new MenuBar();
		// set options on the menu bar
	    menu.setAutoOpen(true);
	    menu.setAnimationEnabled(true);
	    
	    // build the submenus
	    menu.addItem("Save", true, createSaveMenu());
	    menu.addSeparator();
	    menu.addItem("Clear", true, createClearMenu());
	    menu.addSeparator();
	    menu.addItem("Help", true, createHelpMenu());
		// add the save study link and associated form
		form.setAction(SAVEAS_URL);
		form.setMethod(FormPanel.METHOD_POST);
		VerticalPanel formContainer = new VerticalPanel();
		formContainer.add(matrixXML);
		formContainer.add(new Hidden("filename", "study.xml"));
		form.add(formContainer);

		
		// layout the panel
		panel.add(menu);

		// set style TODO:
		panel.setStyleName(STYLE_TOOLBAR);
		
		return panel;
    }

    
	private MenuBar createSaveMenu()
	{
	    MenuBar saveMenu = new MenuBar(true);
	    saveMenu.addItem(new MenuItem("Study Design", true, new Command() {
			@Override
			public void execute()
			{
				
			}
	    }));
	    saveMenu.addSeparator();
	    saveMenu.addItem(new MenuItem("Results", true, new Command() {
			@Override
			public void execute()
			{

			}
	    }));
	    saveMenu.addItem(new MenuItem("Curve", true, new Command() {
			@Override
			public void execute()
			{

			}
	    }));
	    
	    return saveMenu;
	}
	
	private MenuBar createClearMenu()
	{
		MenuBar clearMenu = new MenuBar(true);
		
		
		clearMenu.addItem("Current Screen", new Command() {
			public void execute()
			{
				// TODO: dialog confirmation
				WizardStepPanel wsp = (WizardStepPanel) wizardDeck.getWidget(currentStep);
				wsp.reset();
			}
		});
		
		clearMenu.addItem("All", new Command() {
			public void execute()
			{
				notifyOnCancel();
			}
		});
		
		return clearMenu;
	}
    
	private MenuBar createHelpMenu()
	{
		MenuBar helpMenu = new MenuBar(true);
		helpMenu.addItem("Manual", new Command() {
			public void execute()
			{
				Window.open("/help/manual.pdf", "", "");
			}
		});

		return helpMenu;
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
