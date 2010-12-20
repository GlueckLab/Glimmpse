package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.ToolbarActionListener;

public class ToolBarPanel extends Composite
{
	// css names
	protected static final String STYLE_TOOLBAR = "wizardToolBarPanel";

	private class ToolbarCommand implements Command
	{
		private String menu;
		private String menuItem;
		
		ToolbarCommand(String menu, String menuItem)
		{
			this.menu = menu;
			this.menuItem = menuItem;
		}
		@Override
		public void execute()
		{
			notifyOnMenuAction(menu, menuItem);
		}
	}
	
	private class ToolbarMenuItem extends MenuItem
	{
		public ToolbarCommand originalCmd;
		public ToolbarMenuItem(String text, ToolbarCommand cmd)
		{
			super(text, cmd);
			this.originalCmd = cmd;
		}
	}

	// toolbar action listeners
	protected ArrayList<ToolbarActionListener> actionListeners = new ArrayList<ToolbarActionListener>();
    
	// main menu
	protected MenuBar toolBarMenu = new MenuBar();

	// index to each menu item by menu_item
	protected HashMap<String,ToolbarMenuItem> itemIndex = new HashMap<String,ToolbarMenuItem>();
	
    public ToolBarPanel(String[][] menuItems)
    {
        HorizontalPanel panel = new HorizontalPanel();

		// set options on the menu bar
        toolBarMenu.setAutoOpen(true);
        toolBarMenu.setAnimationEnabled(true);
	    
	    // build the submenus
	    for(int menuCount = 0; menuCount < menuItems.length; menuCount++)
	    {
	    	String[] subMenu = menuItems[menuCount];
	    	// build the submenu - the first list item is the menu title
	    	MenuBar subMenuBar = new MenuBar(true);
	    	for(int i = 1; i < subMenu.length; i++)
	    	{
	    		if (GlimmpseConstants.TOOLBAR_SEPARATOR.equals(subMenu[i]))
	    		{
	    			subMenuBar.addSeparator();
	    		}
	    		else
	    		{
	    			ToolbarMenuItem item = new ToolbarMenuItem(subMenu[i], new ToolbarCommand(subMenu[0], subMenu[i]));
	    			itemIndex.put(subMenu[0] + "_" + subMenu[i], item);
	    			subMenuBar.addItem(item);
	    		}
	    	}
	    	toolBarMenu.addItem(subMenu[0], true, subMenuBar);
	    	if (menuCount < menuItems.length - 1) toolBarMenu.addSeparator();
	    }
		
		// layout the panel
		panel.add(toolBarMenu);
		// set style 
		panel.setStyleName(STYLE_TOOLBAR);
                
        initWidget(panel);
    }
    
    public void notifyOnMenuAction(String menu, String item)
    {
    	for(ToolbarActionListener listener: actionListeners) listener.onMenuAction(menu, item);
    }
    
    public void addToolbarActionListener(ToolbarActionListener listener)
    {
    	actionListeners.add(listener);
    }
	
    public void setMenuItemEnabled(String menuName, String itemName, boolean enabled)
    {
    	String key = menuName + "_" + itemName;
    	ToolbarMenuItem item = itemIndex.get(key);
    	
    	if (item != null) 
    	{
    		item.removeStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
    		if (!enabled)
    		{
    			item.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
    			item.setCommand(null);
    		}
    		else
    		{
    			item.setCommand(item.originalCmd);
    		}
    	}
    }
	
}
