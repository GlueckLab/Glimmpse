/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener.SaveType;

/**
 * Abstract base class for "wizard" input panels.  Manages navigation,
 * basic toolbars, etc.
 * 
 * @author Sarah Kreidler
 *
 */
public class WizardPanel extends Composite
implements NavigationListener, StepStatusListener
{
	// style
	protected static final String STYLE_TOOLBAR = "wizardToolBar";
	// uri for help manual
	protected static final String HELP_URL = "/help/manual.pdf";
	// url for file save web service
	protected static final String SAVEAS_URL = "/webapps/file/saveas"; 
    // form for saving the study design
	protected FormPanel saveForm = new FormPanel("_blank");
	protected Hidden dataHidden = new Hidden("data");
	protected Hidden filenameHidden = new Hidden("filename");
	
	// cancel event listeners
	protected ArrayList<CancelListener> cancelListeners = new ArrayList<CancelListener>();
	// save event listeners
	protected ArrayList<SaveListener> saveListeners = new ArrayList<SaveListener>();
	
	// main panel
	protected HorizontalPanel panel = new HorizontalPanel();
	// left navigation / "steps left" panel
    protected StepsLeftPanel stepsLeftPanel;
	// index of currently visible step
    protected int currentStep = 0;    
    // deck panel containing all steps in the input wizard
    protected DeckPanel wizardDeck = new DeckPanel();
	// save results, curve menu items - these are enabled at the end
    protected MenuItem saveResultsMenuItem = 
    	new MenuItem(Glimmpse.constants.toolBarSaveResultsMenuItem(), true, (Command) null);
    protected Command saveResultsCommand = new Command() {
		@Override
		public void execute()
		{
			notifyOnSave(SaveType.RESULTS);
		}
    };
    protected MenuItem saveCurveMenuItem = 
    	new MenuItem(Glimmpse.constants.toolBarSaveCurveMenuItem(), true, (Command) null);
    protected Command saveCurveCommand = new Command() {
		@Override
		public void execute()
		{
			notifyOnSave(SaveType.CURVE);
		}
    };
	// navigation buttons
    protected NavigationPanel navPanel = new NavigationPanel();
	
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
    	onStep(0);
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
    	
    	boolean lastPanel = (currentStep == wizardDeck.getWidgetCount() - 1);
    	navPanel.setVisible(!lastPanel);
    	
    	// enable the save menu items if we're on the last step.  Note, I'm not crazy about this
    	// since it assumes that results are only shown on the last step, but I didn't have time
    	// to make it all robust and stuff.  Bad programmer, no biscuit for you.
    	if (lastPanel)
    	{    		
    	    saveResultsMenuItem.removeStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
    	    saveCurveMenuItem.removeStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
    	    // add save command
    	    saveResultsMenuItem.setCommand(saveResultsCommand);
    	    saveCurveMenuItem.setCommand(saveCurveCommand);
    	}
    	else
    	{
    	    saveResultsMenuItem.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
    	    saveCurveMenuItem.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
    	    // clear the commands
    	    saveResultsMenuItem.setCommand(null);
    	    saveCurveMenuItem.setCommand(null);
    	}
    }
    
    /**
     * Create the save, clear, help toolbar. 
     * (there's probably a way to make this more generic, but this works for now)
     * 
     * @return HorizontalPanel widget
     */
    private HorizontalPanel createToolBar()
    {
		HorizontalPanel panel = new HorizontalPanel();
		
	    MenuBar menu = new MenuBar();
		// set options on the menu bar
	    menu.setAutoOpen(true);
	    menu.setAnimationEnabled(true);
	    
	    // build the submenus
	    menu.addItem(Glimmpse.constants.toolBarSaveMenu(), true, createSaveMenu());
	    menu.addSeparator();
	    menu.addItem(Glimmpse.constants.toolBarClearMenu(), true, createClearMenu());
	    menu.addSeparator();
	    menu.addItem(Glimmpse.constants.toolBarHelpMenu(), true, createHelpMenu());
	    
		// add the save study link and associated form
		saveForm.setAction(SAVEAS_URL);
		saveForm.setMethod(FormPanel.METHOD_POST);
		VerticalPanel formContainer = new VerticalPanel();
		formContainer.add(dataHidden);
		formContainer.add(filenameHidden);
		saveForm.add(formContainer);
		
		// layout the panel
		panel.add(menu);
		panel.add(saveForm);

		// set style 
		panel.setStyleName(STYLE_TOOLBAR);
		
		return panel;
    }

    /**
     * Create a menubar to save either the study design or results
     * @return "save" menubar
     */
	private MenuBar createSaveMenu()
	{
	    MenuBar saveMenu = new MenuBar(true);
	    MenuItem saveDesignMenuItem = 
	    	new MenuItem(Glimmpse.constants.toolBarSaveStudyMenuItem(), true, 
	    			new Command() {
			@Override
			public void execute()
			{
				notifyOnSave(SaveType.STUDY);
			}
	    });
	    
	    saveMenu.addItem(saveDesignMenuItem);
	    saveMenu.addSeparator();
	    saveMenu.addItem(saveResultsMenuItem);
	    saveMenu.addItem(saveCurveMenuItem);
	    
	    // add disabled style to the results, curve
	    // GWT doesn't provide enable/disable of menu items, so css is the workaround
	    saveResultsMenuItem.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
	    saveCurveMenuItem.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);

	    return saveMenu;
	}
	
	/**
	 * Create a menubar for clearing the all or the current screen
	 * @return "Clear" menubar
	 */
	private MenuBar createClearMenu()
	{
		MenuBar clearMenu = new MenuBar(true);

		clearMenu.addItem(Glimmpse.constants.toolBarClearScreenMenuItem(), new Command() {
			public void execute()
			{
				if (Window.confirm(Glimmpse.constants.confirmClearScreen()))
				{
					WizardStepPanel wsp = (WizardStepPanel) wizardDeck.getWidget(currentStep);
					wsp.reset();
				}
			}
		});
		clearMenu.addItem(Glimmpse.constants.toolBarClearAllMenuItem(), new Command() {
			public void execute()
			{
				if (Window.confirm(Glimmpse.constants.confirmClearAll()))
				{
					notifyOnCancel();
				}
			}
		});
		
		return clearMenu;
	}
    
	/**
	 * Create a menu bar for accessing the help manual
	 * @return "Help" menubar
	 */
	private MenuBar createHelpMenu()
	{
		MenuBar helpMenu = new MenuBar(true);
		helpMenu.addItem(Glimmpse.constants.toolBarHelpManualMenuItem(), new Command() {
			public void execute()
			{
				Window.open(HELP_URL, "", "");
			}
		});

		return helpMenu;
	}
    
	/**
	 * Notify cancel listeners of cancel event
	 */
    protected void notifyOnCancel()
    {  	
    	for(CancelListener listener: cancelListeners) listener.onCancel();
    }
    
    /**
     * Add a listener for cancel events
     * @param listener
     */
    public void addCancelListener(CancelListener listener)
    {
    	cancelListeners.add(listener);
    }
    
    /**
     * Notify listeners that a save action is requested
     * @param type type of save requested
     */
    protected void notifyOnSave(SaveType type)
    {  	
    	for(SaveListener listener: saveListeners) listener.onSave(type);
    }
    
    /**
     * Add a listener for save events
     * @param listener
     */
    public void addSaveListener(SaveListener listener)
    {
    	saveListeners.add(listener);
    }

    /**
     * Send a save request to the File web service.  A service is used to force
     * the browser to pop-up the save as dialog box.
     * 
     * @param data the data to be saved
     * @param filename default filename to use
     */
    public void sendSaveRequest(String data, String filename)
    {
    	dataHidden.setValue(data);
    	filenameHidden.setValue(filename);
    	Window.alert(dataHidden.getValue());
    	saveForm.submit();
    }
    
}
