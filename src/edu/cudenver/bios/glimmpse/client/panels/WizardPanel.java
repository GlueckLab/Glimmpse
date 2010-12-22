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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

/**
 * Abstract base class for "wizard" panels.  Manages navigation,
 * basic toolbars, etc.
 * 
 * @author Sarah Kreidler
 *
 */
public class WizardPanel extends Composite
implements NavigationListener, StepStatusListener
{
	// style for tools
	protected static final String STYLE_TOOL_PANEL = "wizardToolsPanel";
	protected static final String STYLE_TOOL_BUTTON = "wizardToolsPanelButton";
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
    // nav panel
    protected NavigationPanel navPanel = new NavigationPanel();
	// index of currently visible step
    protected int currentStep = 0;  
    // deck panel containing all steps in the input wizard
    protected DeckPanel wizardDeck = new DeckPanel();

    /* 
     * Panel that is not actually displayed, but indicates that we are 
     * at the end of the current panel group
     */
    private class GroupSeparatorPanel extends WizardStepPanel
    {
    	public GroupSeparatorPanel() 
    	{ 
    		super(); 
    		skip = true;
    		separator = true;
    		VerticalPanel panel = new VerticalPanel();
    		initWidget(panel);
    	}
		@Override
		public void reset() {}
		@Override
		public void loadFromNode(Node node) {}    	
    }
    
	/**
	 * Create an empty matrix panel
	 */
	public WizardPanel(WizardStepPanel[][] stepPanels, String[] stepGroupLabels)
	{		
		VerticalPanel contentPanel = new VerticalPanel();
		VerticalPanel leftPanel = new VerticalPanel();
		
		// initialize the steps left panel
		stepsLeftPanel = new StepsLeftPanel(stepGroupLabels);

		// add the content panels to the deck
		int count = 0;
		for(WizardStepPanel[] stepPanelGroup: stepPanels)
		{
			for(WizardStepPanel stepPanel: stepPanelGroup)
			{
				wizardDeck.add(stepPanel);
				stepPanel.addStepStatusListener(this);
			}
			count++;
			if (count < stepPanels.length) wizardDeck.add(new GroupSeparatorPanel());
		}
		
		// add navigation callbacks
		navPanel.addNavigationListener(this);
		enterStep();
		
		// layout the wizard panel
		//contentPanel.add(toolBar);
		leftPanel.add(stepsLeftPanel);
		leftPanel.add(createToolLinks());
		contentPanel.add(wizardDeck);
		contentPanel.add(navPanel);
		panel.add(leftPanel);		
		panel.add(contentPanel);

		// initialize
		initWidget(panel);
	}
	
	public VerticalPanel createToolLinks()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Button saveButton = new Button(Glimmpse.constants.toolsSaveStudy(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				notifyOnSave();
			}
		});
		
		Button cancelButton = new Button(Glimmpse.constants.toolsCancel(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				notifyOnCancel();
			}
		});
		Button helpButton = new Button(Glimmpse.constants.toolsHelp(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				openHelpManual();
			}
		});
		panel.add(saveButton);
		panel.add(helpButton);
		panel.add(cancelButton);
		
		// add style
		panel.setStyleName(STYLE_TOOL_PANEL);
		saveButton.setStyleName(STYLE_TOOL_BUTTON);
		cancelButton.setStyleName(STYLE_TOOL_BUTTON);
		helpButton.setStyleName(STYLE_TOOL_BUTTON);
		return panel;
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
			// find the next panel which is not skipped, and is not a separator panel
    		WizardStepPanel w;
    		do
    		{
    			currentStep++;
    			w = ((WizardStepPanel) wizardDeck.getWidget(currentStep));
    			if (w.isSeparator()) stepsLeftPanel.onNext();
    		} 
    		while ((w.isSkipped() || w.isSeparator()) && 
    				currentStep < wizardDeck.getWidgetCount()-1);
    		enterStep();
    	}
    }
    
    /**
     * Call back when "previous" navigation button is clicked
     * Does nothing if already at start of step list
     */
    public void onPrevious()
    {
    	if (currentStep > 0)
    	{
    		exitStep();
			// find the next panel which is not skipped, and is not a separator panel
    		WizardStepPanel w;
    		do
    		{
    			currentStep--;
    			w = ((WizardStepPanel) wizardDeck.getWidget(currentStep));
    			if (w.isSeparator()) stepsLeftPanel.onPrevious();
    		} 
    		while ((w.isSkipped() || w.isSeparator()) && 
    				currentStep > 0);
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
    	navPanel.setPrevious(currentStep != 0);
    }
        
    /**
     * Add a listener for cancel events
     */
    public void addCancelListener(CancelListener listener)
    {
    	cancelListeners.add(listener);
    }
    
    /**
     * Notify listeners of a cancel event
     */
	public void notifyOnCancel()
	{
		if (Window.confirm(Glimmpse.constants.confirmClearAll()))
		{
			for(CancelListener listener: cancelListeners) listener.onCancel();
		}
	}
	
    /**
     * Add a listener for save study events
     */
    public void addSaveListener(SaveListener listener)
    {
    	saveListeners.add(listener);
    }

    /**
     * Notify save listeners of a save study event
     */
    private void notifyOnSave()
    {
    	for(SaveListener listener: saveListeners) listener.onSave();
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

    /**
     * Open the help manual in a new tab/window
     */
    public void openHelpManual()
    {
		// open manual
		Window.open(HELP_URL, "_blank", null);
    }
}
