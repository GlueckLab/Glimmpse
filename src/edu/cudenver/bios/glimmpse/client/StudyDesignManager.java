package edu.cudenver.bios.glimmpse.client;

public interface StudyDesignManager
{	
	public String getStudyDesignXML();
	public String getPowerRequestXML();
	
	public String getModeName();
	
	public void sendSaveRequest(String data, String filename);
}
