package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public interface DynamicListManager
{
	public abstract Widget createListWidget(ChangeHandler handler, int column);
	
	public abstract String getValue(Widget w, int column);
	
	public abstract void clear(Widget w, int column);
}
