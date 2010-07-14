package edu.cudenver.bios.glimmpse.client.listener;

/**
 * Interface for handling changes to the design matrix
 */
public interface DesignChangeListener
{
	public void onResize(int rows, int columns);
	
	public void onCovariate(boolean hasCovariate);
}
