package edu.cudenver.bios.glimmpse.client.panels;

public interface DynamicListValidator
{
	public void validate(String value, int column) throws IllegalArgumentException;
	
	public void onValidRowCount(int validRowCount);
}
