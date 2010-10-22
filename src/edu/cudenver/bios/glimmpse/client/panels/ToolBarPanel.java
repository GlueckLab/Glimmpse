package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.ToolbarActionListener;


public class ToolBarPanel extends Composite
{
	// css names
	protected static final String STYLE_TOOLBAR = "wizardToolBar";
	protected static final String STYLE_SAVE = "wizardToolBarSave";
	protected static final String STYLE_CLEAR = "wizardToolBarClear";
	protected static final String STYLE_HELP = "wizardToolBarHelp";

	// toolbar action listeners
	ArrayList<ToolbarActionListener> actionListeners = new ArrayList<ToolbarActionListener>();
    protected Button saveButton;
    protected Button clearButton;
    protected Button helpButton;

    
    public ToolBarPanel()
    {
        HorizontalPanel panel = new HorizontalPanel();
       
        saveButton = new Button(Glimmpse.constants.buttonSave(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                notifyOnSave();
            }
        });
        clearButton = new Button(Glimmpse.constants.buttonClear(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                notifyOnClear();
            }
        });
        helpButton = new Button(Glimmpse.constants.buttonHelp(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                notifyOnHelp();
            }
        });
        
        panel.add(saveButton);
        panel.add(clearButton);
        panel.add(helpButton);
        
        // add style
        saveButton.setStyleName(STYLE_SAVE);
        clearButton.setStyleName(STYLE_CLEAR);
        helpButton.setStyleName(STYLE_HELP);
        panel.setStyleName(STYLE_TOOLBAR);
                
        initWidget(panel);
    }
    
    protected void notifyOnSave()
    {
    	for(ToolbarActionListener listener: actionListeners)
    		listener.onSave();
    }
    
    protected void notifyOnClear()
    {
    	for(ToolbarActionListener listener: actionListeners)
    		listener.onClear();
    }
    
    protected void notifyOnHelp()
    {
    	for(ToolbarActionListener listener: actionListeners)
    		listener.onHelp();
    }
    
    public void addToolbarActionListener(ToolbarActionListener listener)
    {
    	actionListeners.add(listener);
    }

}
