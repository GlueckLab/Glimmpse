package edu.cudenver.bios.glimmpse.client.listener;

import com.google.gwt.xml.client.Document;

public interface StartListener
{
	public void onGuidedMode();
	
	public void onMatrixMode();
	
    public void onStudyUpload(String uploadedStudy);
}
