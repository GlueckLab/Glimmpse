package edu.cudenver.bios.glimmpse.client.listener;

public interface NavigationListener
{
    public void onNext();
    
    public void onPrevious();
    
    public void onCancel();
    
    public void onStep(int stepIndex);
}
