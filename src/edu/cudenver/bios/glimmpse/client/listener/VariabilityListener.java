package edu.cudenver.bios.glimmpse.client.listener;

import java.util.List;

public interface VariabilityListener
{
	public void onOutcomeVariance(List<Double> variancesOfOutcomes);
	
	public void onCovariateVariance(double varianceOfCovariate);

}
