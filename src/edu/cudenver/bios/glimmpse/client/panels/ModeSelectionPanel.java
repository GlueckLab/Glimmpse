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
package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.ModeSelectionListener;

/**
 * Welcome / start panel which allows the user to select the method for 
 * study design input (guided or matrix), or upload an existing design
 * 
 * @author Sarah Kreidler
 *
 */
public class ModeSelectionPanel extends Composite implements SubmitCompleteHandler
{
	private static final String STYLE_HEADER = "modeSelectionPanelHeader";
	private static final String STYLE_DESCRIPTION = "modeSelectionPanelDescription";
	private static final String STYLE_GO_BUTTON = "modeSelectionPanelGoButton";
	private static final String STYLE_CONTAINER = "modeSelectionPanelContainer";
	private static final String STYLE_CONTAINER_TITLE = "modeSelectionPanelContainerTitle";
	
    // uri of file upload service
    private static final String UPLOAD_URI = "/webapps/file/upload";
    // form tag for file
    private static final String FORM_TAG_FILE = "file";
    
    protected FormPanel formPanel = new FormPanel();
    protected FileUpload uploader = new FileUpload();
    
    // listeners for start events
    protected ArrayList<ModeSelectionListener> startListeners = new ArrayList<ModeSelectionListener>();
    
    /**
     * Constructor
     */
    public ModeSelectionPanel()
    {
        VerticalPanel panel = new VerticalPanel();

        // layout the widgets        
        // add introductory text
        HTML header = new HTML(Glimmpse.constants.modeSelectionTitle());
        HTML description = new HTML(Glimmpse.constants.modeSelectionDescription());
        panel.add(header);
        panel.add(description);
        
        // create user input container
        VerticalPanel inputContainer = new VerticalPanel();

        // guided study design mode
        Grid guidedModeContainer = new Grid(1,2);
        VerticalPanel guidedTextContainer = new VerticalPanel();
        HTML guidedTitle = new HTML(Glimmpse.constants.modeSelectionGuidedTitle());
        guidedTextContainer.add(guidedTitle);
        guidedTextContainer.add(new HTML(Glimmpse.constants.modeSelectionGuidedDescription()));
        guidedModeContainer.setWidget(0, 0, guidedTextContainer);
        Button guidedGoButton = new Button(Glimmpse.constants.modeSelectionGoButton(), new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                for(ModeSelectionListener listener: startListeners) listener.onGuidedMode();
            }
        });
        guidedModeContainer.setWidget(0, 1, guidedGoButton);
        
        // matrix entry mode
        Grid matrixModeContainer = new Grid(1,2);
        VerticalPanel matrixTextContainer = new VerticalPanel();
        HTML matrixTitle = new HTML(Glimmpse.constants.modeSelectionMatrixTitle());
        matrixTextContainer.add(matrixTitle);
        matrixTextContainer.add(new HTML(Glimmpse.constants.modeSelectionMatrixDescription()));
        matrixModeContainer.setWidget(0, 0, matrixTextContainer);
        Button matrixGoButton = new Button(Glimmpse.constants.modeSelectionGoButton(), new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                for(ModeSelectionListener listener: startListeners) listener.onMatrixMode();
            }
        });
        matrixModeContainer.setWidget(0, 1, matrixGoButton);
        
        // upload an existing study        
        VerticalPanel uploadContainer = new VerticalPanel();
        HTML uploadTitle = new HTML(Glimmpse.constants.modeSelectionUploadTitle());
        uploadContainer.add(uploadTitle);
        uploadContainer.add(new HTML(Glimmpse.constants.modeSelectionUploadDescription()));
        // build the upload form
        // for file upload, we need to use the POST method, and multipart MIME encoding.
        formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        formPanel.setMethod(FormPanel.METHOD_POST);
        formPanel.setAction(UPLOAD_URI);
        // panel to contain the contents of the submission form
        HorizontalPanel formContents = new HorizontalPanel();
        // create an upload widget
        uploader.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                String filename = uploader.getFilename();
                if (filename == null || filename.isEmpty())
                {
                    Window.alert("No filename specified.  Please click the 'Browse' button and select a file for upload.");
                }
                else
                {
                    formPanel.submit();
                }
            }
        });
        uploader.setName(FORM_TAG_FILE);
        formContents.add(uploader);
        formPanel.add(formContents);
        formPanel.addSubmitCompleteHandler(this);
        uploadContainer.add(formPanel);
        
        // build overall panel        
        inputContainer.add(guidedModeContainer);
        inputContainer.add(matrixModeContainer);
        inputContainer.add(uploadContainer);       
        panel.add(inputContainer);
        
        // add style
        header.setStyleName(STYLE_HEADER);
        description.setStyleName(STYLE_DESCRIPTION);
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        inputContainer.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_INPUT_CONTAINER);
        // guided subpanel style
        guidedModeContainer.setStyleName(STYLE_CONTAINER);
        guidedTitle.setStyleName(STYLE_CONTAINER_TITLE);
        guidedGoButton.setStyleName(STYLE_GO_BUTTON);
        // matrix subpanel style
        matrixModeContainer.setStyleName(STYLE_CONTAINER);
        matrixTitle.setStyleName(STYLE_CONTAINER_TITLE);
        matrixGoButton.setStyleName(STYLE_GO_BUTTON);
        // upload subpanel style
        uploadContainer.setStyleName(STYLE_CONTAINER);
        uploadTitle.setStyleName(STYLE_CONTAINER_TITLE);

        // initialize the panel
        initWidget(panel);
    }
    
    /**
     * Notify any listeners when a study design file has been uploaded
     */
    public void onSubmitComplete(SubmitCompleteEvent event) 
    {
        String results = event.getResults();
        for(ModeSelectionListener listener: startListeners) listener.onStudyUpload(results);
    }
    
    /**
     * Reset the upload form panel
     */
    public void reset()
    {
        formPanel.reset();
    }

    /**
     * Add a listener for "start events" which indicate if the user
     * wants to create a new study, or upload an existing design
     * 
     * @param listener 
     */
    public void addStartListener(ModeSelectionListener listener)
    {
    	startListeners.add(listener);
    }  
}