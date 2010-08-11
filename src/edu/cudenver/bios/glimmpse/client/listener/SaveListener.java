package edu.cudenver.bios.glimmpse.client.listener;

public interface SaveListener
{
	public static enum SaveType 
	{
		STUDY,
		RESULTS,
		CURVE
	};
	
	public void onSave(SaveType type);
}
