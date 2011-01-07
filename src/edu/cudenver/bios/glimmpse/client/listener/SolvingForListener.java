package edu.cudenver.bios.glimmpse.client.listener;

public interface SolvingForListener
{
	public enum SolutionType
	{
		POWER,
		TOTAL_N,
		DETECTABLE_DIFFERENCE
	};
	
	public void onSolvingFor(SolutionType solutionType);
}
