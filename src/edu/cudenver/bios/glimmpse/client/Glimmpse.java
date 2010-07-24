/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.glimmpse.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ColumnChart;
import com.google.gwt.visualization.client.visualizations.ScatterChart;
import com.google.gwt.visualization.client.visualizations.Table;

import edu.cudenver.bios.glimmpse.client.panels.GlimmpsePanel;

/**
 * Entry point class for the glimmpse user interface.
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
    		RootPanel glimmpsePanel = RootPanel.get("glimmpsePanel");
    		if (glimmpsePanel != null)
    		{
    			glimmpsePanel.add(new GlimmpsePanel());
    			glimmpsePanel.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
    		}
            // set root style so it recognizes standard css elements like "body"
            RootPanel.get().setStyleName("body");
    	}
    };
    
    public void onModuleLoad()
    {        
        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
       VisualizationUtils.loadVisualizationApi(onLoadCallback, 
    		   ColumnChart.PACKAGE, 
    		   ScatterChart.PACKAGE, 
    		   Table.PACKAGE);
	}
}
