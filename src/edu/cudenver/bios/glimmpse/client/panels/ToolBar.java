package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;

public class ToolBar extends Composite
{
	// style
	protected static final String STYLE_TOOLBAR = "wizardToolBar";
	protected static final String STYLE_SAVE = "wizardToolBarButtonSave";
	protected static final String STYLE_CANCEL = "wizardToolBarButtonCancel";
	protected static final String STYLE_CLEAR = "wizardToolBarButtonClear";
	
	// url for file save web service
	protected static final String SAVEAS_URL = "/webapps/file/saveas"; 
    // form for saving the study design
	protected FormPanel form = new FormPanel("_blank");
	protected Hidden matrixXML = new Hidden("data");
	
	public ToolBar()
	{
		
		HorizontalPanel panel = new HorizontalPanel();
		
		// add the save study link and associated form
		form.setAction(SAVEAS_URL);
		form.setMethod(FormPanel.METHOD_POST);
		VerticalPanel formContainer = new VerticalPanel();
		formContainer.add(matrixXML);
		formContainer.add(new Hidden("filename", "study.xml"));
		form.add(formContainer);
		// save button
		Button saveButton = new Button("Save", new ClickHandler() {
			public void onClick(ClickEvent e)
			{
				// TODO: set study text into matrixXML hidden field
		    	form.submit();    	
			}
		});
		// clear button
		Button clearButton = new Button("Clear", new ClickHandler() {
			public void onClick(ClickEvent e)
			{
				
			}
		});
		// cancel button
		Button cancelButton = new Button("New", new ClickHandler() {
			public void onClick(ClickEvent e)
			{
				
			}
		});
		
		// layout the panel
		panel.add(saveButton);
		panel.add(clearButton);
		panel.add(cancelButton);
		// set style
		panel.setStyleName(STYLE_TOOLBAR);
		saveButton.setStyleName(STYLE_SAVE);
		clearButton.setStyleName(STYLE_CLEAR);
		cancelButton.setStyleName(STYLE_CANCEL);
		
		initWidget(panel);
	}
}
