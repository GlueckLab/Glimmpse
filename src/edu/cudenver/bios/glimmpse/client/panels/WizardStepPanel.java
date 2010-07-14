package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;

import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public abstract class WizardStepPanel extends Composite
{
	protected String name;
	protected boolean complete = false;
    protected ArrayList<StepStatusListener> stepStatusListeners = new ArrayList<StepStatusListener>();

	public WizardStepPanel(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isComplete()
	{
		return complete;
	}
	
    public void addStepStatusListener(StepStatusListener listener)
    {
    	stepStatusListeners.add(listener);
    }
    
    public void notifyComplete()
    {
    	complete = true;
		for(StepStatusListener listener: stepStatusListeners) listener.onStepComplete();
    }
    
    public void notifyInProgress()
    {
    	complete = false;
		for(StepStatusListener listener: stepStatusListeners) listener.onStepInProgress();
    }
    
    public abstract void reset();
}
