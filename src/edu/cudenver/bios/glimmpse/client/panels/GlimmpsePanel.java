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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.StartListener;

/**
 * Main application panel for Glimmpse. 
 */
public class GlimmpsePanel extends Composite
implements StartListener, CancelListener
{
	private static final int START_INDEX = 0;
	private static final int GUIDED_INDEX = 1;
	private static final int MATRIX_INDEX = 2;
	
	protected DeckPanel deckPanel = new DeckPanel();
	protected StartPanel startPanel = new StartPanel();
	protected MatrixWizardPanel matrixWizardPanel = new MatrixWizardPanel();
	protected GuidedWizardPanel guidedWizardPanel = new GuidedWizardPanel();
	
	public GlimmpsePanel()
	{
		// add the start panel and wizard panels to the deck
		deckPanel.add(startPanel);
		deckPanel.add(guidedWizardPanel);
		deckPanel.add(matrixWizardPanel);
		// show start screen first
		deckPanel.showWidget(START_INDEX);
		// add style
		deckPanel.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
		
		// set up listener relationships
		startPanel.addStartListener(this);
		guidedWizardPanel.addCancelListener(this);
		matrixWizardPanel.addCancelListener(this);
		// initialize
		initWidget(deckPanel);
		
	}
	
    
	public void onGuidedMode()
	{
		deckPanel.showWidget(GUIDED_INDEX);
	}
	
	public void onMatrixMode()
	{
		deckPanel.showWidget(MATRIX_INDEX);
	}
	
    public void onStudyUpload(String uploadedStudy)
    {
        if (uploadedStudy != null)
        {
        	try
        	{
           		Document doc = XMLParser.parse(uploadedStudy);
        		Node studyNode = doc.getElementsByTagName("study").item(0);
        		if (studyNode == null) throw new DOMException(DOMException.SYNTAX_ERR, "no study tag specified");
        		Node mode = studyNode.getAttributes().getNamedItem("mode");
        		if (mode != null && mode.equals(GlimmpseConstants.MODE_MATRIX))
        		{
        			matrixWizardPanel.reset();
        			matrixWizardPanel.loadFromXML(doc);
        			onMatrixMode();
        		}
        		else
        		{
        			guidedWizardPanel.reset();
        			guidedWizardPanel.loadFromXML(doc);
        			onGuidedMode();
        		}
        	}
        	catch (DOMException e)
        	{
        		Window.alert(Glimmpse.constants.errorUploadInvalidStudyFile() + " [" + e.getMessage() + "]");
        	}
        }
        else
        {
        	Window.alert(Glimmpse.constants.errorUploadFailed());
        }
    }


	@Override
	public void onCancel()
	{
		matrixWizardPanel.reset();
		guidedWizardPanel.reset();
		deckPanel.showWidget(START_INDEX);
	}
}
