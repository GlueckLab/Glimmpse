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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.panels.matrix.BetaPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.ContrastPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.CovariancePanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.DesignPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.ThetaPanel;

/**
 * Wizard panel for matrix input mode.  Contains separate steps
 * for each type of matrix required for power calculations on the GLMM
 */
public class MatrixWizardPanel extends Composite
implements NavigationListener
{
    // top steps left panel
    protected static final String[] stepNames = {
        Glimmpse.constants.stepsLeftAlpha(),
        Glimmpse.constants.stepsLeftDesign(),
        Glimmpse.constants.stepsLeftContrast(),
        Glimmpse.constants.stepsLeftBeta(),
        Glimmpse.constants.stepsLeftTheta(),
        Glimmpse.constants.stepsLeftSigma(),
        Glimmpse.constants.stepsLeftOptions(),
        Glimmpse.constants.stepsLeftResults() 	
    };
    protected StepsLeftPanel stepsLeftPanel = new StepsLeftPanel(stepNames);

    // deck panel containing all steps in the input wizard
    protected DeckPanel wizardDeck = new DeckPanel();
	
	// content panels 
    protected AlphaPanel alphaPanel = new AlphaPanel(Glimmpse.constants.stepsLeftAlpha());
    protected DesignPanel designPanel = new DesignPanel(Glimmpse.constants.stepsLeftDesign());
    protected ContrastPanel contrastPanel = new ContrastPanel(Glimmpse.constants.stepsLeftContrast());
    protected BetaPanel betaPanel = new BetaPanel(Glimmpse.constants.stepsLeftBeta());
    protected ThetaPanel thetaPanel = new ThetaPanel(Glimmpse.constants.stepsLeftTheta());
    protected CovariancePanel covariancePanel = new CovariancePanel(Glimmpse.constants.stepsLeftSigma());
    protected OptionsPanel optionsPanel = new OptionsPanel();
    protected ResultsPanel resultsPanel = new ResultsPanel();
	
	// index of currently visible step
    protected int currentStep = 0;
	
	/**
	 * Create an empty matrix panel
	 */
	public MatrixWizardPanel()
	{
		HorizontalPanel panel = new HorizontalPanel();
		VerticalPanel contentPanel = new VerticalPanel();
		
		// add the content panels to the deck
        wizardDeck.add(alphaPanel);
        wizardDeck.add(designPanel);
        wizardDeck.add(contrastPanel);
        wizardDeck.add(betaPanel);
        wizardDeck.add(thetaPanel);
        wizardDeck.add(covariancePanel);
        wizardDeck.add(optionsPanel);
        wizardDeck.add(resultsPanel);
		wizardDeck.showWidget(currentStep);
		
		// add content callbacks
		//outcomesPanel.addOutcomesListener(this);
		
		// add navigation callbacks
//		navPanel.addNavigationListener(this);
//		navPanel.addNavigationListener(stepsLeftPanel);
		
		// layout the wizard panel
		contentPanel.add(new ToolBar());
		contentPanel.add(wizardDeck);
		//contentPanel.add(navPanel);
		panel.add(stepsLeftPanel);		
		panel.add(contentPanel);
		
		// set style
		//panel.setStyleName(LAYOUT_STYLE);		
		
		// set up listener relationships
		stepsLeftPanel.addNavigationListener(this);

		// initialize
		initWidget(panel);
	}
	
	/**
	 * Fill in the wizard from an XML description of the study matrices
	 */
	public void loadFromXML(Document doc)
	{
		
	}
	
    /**
     * Call back when "next" navigation button is clicked
     * Does nothing if already at end of step list
     */
    public void onNext()
    {
    	if (currentStep < wizardDeck.getWidgetCount()-1)
    	{
    		currentStep++;
    		wizardDeck.showWidget(currentStep);
    	}
    }
    
    /**
     * Call back when "previous" navigation button is clicked
     * Does nothing if already at beginning of step list
     */
    public void onPrevious()
    {
    	if (currentStep > 0)
    	{
    		currentStep--;
    		wizardDeck.showWidget(currentStep);
    	}
    }
    
    /**
     * Return to first step
     */
    public void onCancel()
    {
    	// TODO: clear everything
    	stepsLeftPanel.onCancel();
    	currentStep = 0;
    	wizardDeck.showWidget(currentStep);
    }
	
    public void onStep(int stepIndex)
    {
    	if (stepIndex >= 0 && stepIndex < wizardDeck.getWidgetCount())
    	{
    		currentStep = stepIndex;
    		wizardDeck.showWidget(currentStep);
    	}
    }
    
    public void reset()
    {
    	
    }
}
