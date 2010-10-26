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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

/**
 * Abstract base class for all steps in a wizard
 * 
 * @author Sarah Kreidler
 *
 */
public abstract class WizardStepPanel extends Composite
{
	// if true, the panel is skipped in the navigation of the wizard
	protected boolean skip = false;
	// if true, the panel is complete and the user may navigate forward
	protected boolean complete = false;
	// if true, this panel is a non-visible separator.  Used to indicate the
	// start and end of a panel group
	protected boolean separator = false;
	// listeners for changes to step complete / in-progress status
    protected ArrayList<StepStatusListener> stepStatusListeners = new ArrayList<StepStatusListener>();
    
    /**
     * Constructor
     */
	public WizardStepPanel()
	{
	}
	
	/**
	 * Indicates if this panel is a separator between panel groups
	 * @return true if this is a separator panel
	 */
	public boolean isSeparator()
	{
		return separator;
	}
	
	/**
	 * Indicates if this panel should be skipped in current navigation
	 * @return true if panel should be skipped over
	 */
	public boolean isSkipped()
	{
		return skip;
	}

	/**
	 * Indicates if the user has completed this panel and forward
	 * navigation is allowed
	 * @return true if forward navigation is allowed
	 */
	public boolean isComplete()
	{
		return complete;
	}
	
	/**
	 * Add a listener for changes to this step's complete/in-progress status
	 * @param listener listener for step complete/in-progress events
	 */
    public void addStepStatusListener(StepStatusListener listener)
    {
    	stepStatusListeners.add(listener);
    }
    
    /**
     * Notify listeners that this step is complete and forward navigation
     * is allowed.
     */
    public void notifyComplete()
    {
    	complete = true;
		for(StepStatusListener listener: stepStatusListeners) listener.onStepComplete();
    }
    
    /**
     * Notify listeners that this step is in-progress and forward navigation
     * is not allowed.
     */
    public void notifyInProgress()
    {
    	complete = false;
		for(StepStatusListener listener: stepStatusListeners) listener.onStepInProgress();
    }
    
    /**
     * Clears any information and resets the panel to its default values
     */
    public abstract void reset();
    
    /**
     * Fill in the panel with information from a previously saved, XML
     * formatted study design.
     * @param node DOM node containing information about this panel
     */
    public abstract void loadFromNode(Node node);
    
    /**
     * Perform any setup when first entering this step in the wizard
     */
    public void onEnter() {}
    
    /**
     * Perform any cleanup when first exiting this step in the wizard
     */
    public void onExit() {}
}
