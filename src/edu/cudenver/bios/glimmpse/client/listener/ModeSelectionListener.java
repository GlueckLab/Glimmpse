package edu.cudenver.bios.glimmpse.client.listener;

public interface ModeSelectionListener
{
	public void onGuidedMode();
	
	public void onMatrixMode();
	
    public void onStudyUpload(String uploadedStudy);
}
