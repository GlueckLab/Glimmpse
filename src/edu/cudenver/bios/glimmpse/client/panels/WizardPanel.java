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
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener.SaveType;
import edu.cudenver.bios.glimmpse.client.listener.ToolbarActionListener;

/**
 * Abstract base class for "wizard" input panels.  Manages navigation,
 * basic toolbars, etc.
 * 
 * @author Sarah Kreidler
 *
 */
public class WizardPanel extends Composite
implements ToolbarActionListener, NavigationListener, StepStatusListener
{
	// separator name 
	protected static final String SEPARATOR = "__SEPARATOR__";
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
    // toolbar 
    protected ToolBarPanel toolBar = new ToolBarPanel();
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
    		super(SEPARATOR); 
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
		
		// add navigation and  toolbar action callbacks
		toolBar.addToolbarActionListener(this);
		navPanel.addNavigationListener(this);
		enterStep();
		
		// layout the wizard panel
		contentPanel.add(toolBar);
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
    		if (SEPARATOR.equals(((WizardStepPanel) wizardDeck.getWidget(currentStep)).getName()))
    		{
    			currentStep++;
    			stepsLeftPanel.onNext();
    		}
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
    		currentStep--;
    		if (SEPARATOR.equals(((WizardStepPanel) wizardDeck.getWidget(currentStep)).getName()))
    		{
    			currentStep--;
    			stepsLeftPanel.onPrevious();
    		}
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
    	navPanel.setPrevious(currentStep != 0);
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

	@Override
	public void onSave()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClear()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHelp()
	{
		// TODO Auto-generated method stub
		
	}
    
}
