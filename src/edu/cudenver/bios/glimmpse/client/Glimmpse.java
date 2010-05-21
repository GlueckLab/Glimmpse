package edu.cudenver.bios.glimmpse.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ColumnChart;
import com.google.gwt.visualization.client.visualizations.LineChart;

import edu.cudenver.bios.glimmpse.client.panels.InputWizardPanel;

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
    

    Runnable onLoadCallback = new Runnable() {
    	public void run() {
            // add the gwt elements to the root panel
            RootPanel.get("glimmpseWizard").add(new InputWizardPanel());
            RootPanel.get("glimmpseWizard").setStyleName("glimmpseWizardPanel");
            // set root style so it recognizes standard css elements like "body"
            RootPanel.get().setStyleName("body");
    	}
    };
    
    public void onModuleLoad()
    {        
    	
        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
       VisualizationUtils.loadVisualizationApi(onLoadCallback, ColumnChart.PACKAGE, LineChart.PACKAGE);
    	
//        // add the gwt elements to the root panel
//        RootPanel.get("glimmpseWizard").add(new InputWizardPanel());
//        RootPanel.get("glimmpseWizard").setStyleName("glimmpseWizardPanel");
//        // set root style so it recognizes standard css elements like "body"
//        RootPanel.get().setStyleName("body");
	}
}
