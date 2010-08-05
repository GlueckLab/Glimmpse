package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.CheckBox;

public class RowCheckBox extends CheckBox
{
	public int row;
	
	public RowCheckBox(int row)
	{
		super();
		this.row = row;
	}
}
