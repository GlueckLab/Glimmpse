package edu.cudenver.bios.glimmpse.client;

public interface StudyDesignManager
{
	public enum Solution {
		POWER,
		SAMPLE_SIZE,
		EFFECT_SIZE
	};
	
	public String getStudyDesignXML();
	public String getPowerRequestXML();
	public Solution getSolvingFor();

}
