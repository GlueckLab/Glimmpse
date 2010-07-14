package edu.cudenver.bios.glimmpse.client.listener;

public interface CovariateListener
{
    public void onHasCovariate(boolean hasCovariate);
    
    public void onMean(double mean);
    
    public void onVariance(double variance);
    
}
