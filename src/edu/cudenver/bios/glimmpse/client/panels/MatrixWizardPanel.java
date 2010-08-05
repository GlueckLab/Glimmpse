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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.StudyDesignManager;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
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
implements StudyDesignManager
{
	// content panels 
	protected SolvingForPanel solvingForPanel = new SolvingForPanel(getModeName());
    protected AlphaPanel alphaPanel = new AlphaPanel();
    protected DesignPanel designPanel = new DesignPanel();
    protected ContrastPanel contrastPanel = new ContrastPanel();
    protected BetaPanel betaPanel = new BetaPanel();
    protected ThetaPanel thetaPanel = new ThetaPanel();
    protected CovariancePanel covariancePanel = new CovariancePanel();
    protected OptionsPanel optionsPanel = new OptionsPanel();
    //protected ResultsPanel resultsPanel = new ResultsPanel(this);
	protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(this);
    // list of panels for the wizard
	WizardStepPanel[] panelList = {
			solvingForPanel,
			alphaPanel, 
			designPanel, 
			betaPanel, 
			contrastPanel, 
			thetaPanel,
			covariancePanel,
			optionsPanel,
			resultsPanel
	};
	
	// wizard navigation panel
	WizardPanel wizardPanel;
	
	/**
	 * Create an empty matrix panel
	 */
	public MatrixWizardPanel()
	{	
		VerticalPanel panel = new VerticalPanel();
		
		wizardPanel = new WizardPanel(panelList);
		panel.add(wizardPanel);

		// set up listener relationships between the matrix panels
		// this maintains matrix conformance
		solvingForPanel.addSolvingForListener(designPanel);
		solvingForPanel.addSolvingForListener(betaPanel);
		solvingForPanel.addSolvingForListener(resultsPanel);
		designPanel.addMatrixResizeListener(betaPanel);
		designPanel.addMatrixResizeListener(contrastPanel);
		designPanel.addCovariateListener(betaPanel);
		designPanel.addCovariateListener(contrastPanel);
		designPanel.addCovariateListener(covariancePanel);
		contrastPanel.addBetweenSubjectMatrixResizeListener(thetaPanel);
		contrastPanel.addWithinSubjectMatrixResizeListener(thetaPanel);
		optionsPanel.addOptionsListener(resultsPanel);

		betaPanel.addMatrixResizeListener(contrastPanel);
		betaPanel.addMatrixResizeListener(covariancePanel);
		// initialize
		initWidget(panel);
	}
	
	/**
	 * Fill in the wizard from an XML description of the study matrices
	 */
	public void loadFromXML(Document doc)
	{
		
	}
    
    public void reset()
    {
    	wizardPanel.reset();
    }
    
    public void addCancelListener(CancelListener listener)
    {
    	wizardPanel.addCancelListener(listener);
    }

	@Override
	public String getPowerRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<" + GlimmpseConstants.TAG_POWER_PARAMETERS + ">");
		buffer.append(solvingForPanel.toXML());
		buffer.append(alphaPanel.toXML());
		buffer.append(designPanel.toXML());
		buffer.append(betaPanel.toXML());
		buffer.append(contrastPanel.toXML());
		buffer.append(thetaPanel.toXML());
		buffer.append(covariancePanel.toXML());
		buffer.append(optionsPanel.toXML());
		buffer.append("</" + GlimmpseConstants.TAG_POWER_PARAMETERS + ">");
		
		return buffer.toString();
	}

	@Override
	public String getStudyDesignXML()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getModeName()
	{
		return "matrix";
	}
}
