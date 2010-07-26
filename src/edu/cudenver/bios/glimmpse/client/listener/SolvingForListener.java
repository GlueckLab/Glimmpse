package edu.cudenver.bios.glimmpse.client.listener;

public interface SolvingForListener
{
	public enum SolutionType
	{
		POWER,
		TOTAL_N,
		EFFECT_SIZE
	};
	
	public void onSolvingFor(SolutionType solutionType);
}
