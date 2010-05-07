package edu.cudenver.bios.glimmpse.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import edu.cudenver.bios.glimmpse.client.panel.InputWizardPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Glimmpse implements EntryPoint
{
    /**
     * This is the entry point method.
     */
	
    // string constants for internationalization 
    public static final GlimmpseConstants constants =  
    	(GlimmpseConstants) GWT.create(GlimmpseConstants.class); 
    
    public void onModuleLoad()
    {        
        // add the gwt elements to the root panel
        RootPanel.get("glimmpseWizard").add(new InputWizardPanel());
        RootPanel.get("glimmpseWizard").setStyleName("glimmpsePanel");
        // set root style so it recognizes standard css elements like "body"
        RootPanel.get().setStyleName("body");
    }
}
