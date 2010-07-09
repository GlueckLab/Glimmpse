package edu.cudenver.bios.glimmpse.client.listener;

public interface StepStatusListener
{
	public void onStepComplete(String stepName);
	
	public void onStepInProgress(String stepName);
}
