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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.StudyDesignManager;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener;
import edu.cudenver.bios.glimmpse.client.panels.guided.EffectSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.OutcomesPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.PredictorsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.StudyGroupsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityPanel;

/**
 * Wizard panel for "guided" input mode.  Contains panels to describe 
 * study subjects, outcomes, and estimate effect sizes and variability
 * 
 * @author Sarah Kreidler
 *
 */
public class GuidedWizardPanel extends Composite
implements StudyDesignManager, SaveListener
{
	private static final String MODE_NAME = "guided";
	// default filenames
	protected static final String DEFAULT_STUDY_FILENAME = "study.xml";
	protected static final String DEFAULT_RESULTS_FILENAME = "power.csv";
	protected static final String DEFAULT_CURVE_FILENAME = "powerCurve.jpg";
	
	// content panels 
	protected SolvingForPanel solvingForPanel = new SolvingForPanel(getModeName());
	protected AlphaPanel alphaPanel = new AlphaPanel();
	protected OutcomesPanel outcomesPanel = new OutcomesPanel();
	protected PredictorsPanel predictorsPanel = new PredictorsPanel();
	protected StudyGroupsPanel studyGroupsPanel = new StudyGroupsPanel();
	protected HypothesisPanel hypothesisPanel = new HypothesisPanel();
	protected EffectSizePanel effectSizePanel = new EffectSizePanel();
	protected VariabilityPanel variabilityPanel = new VariabilityPanel();
	protected OptionsPanel optionsPanel = new OptionsPanel(getModeName());
	protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(this);
	
    // list of panels for the wizard
	WizardStepPanel[] panelList = {
			solvingForPanel,
			alphaPanel, 
			predictorsPanel, 
			studyGroupsPanel, 
			outcomesPanel, 
			hypothesisPanel,
			effectSizePanel,
			variabilityPanel,
			optionsPanel,
			resultsPanel};
	
	// wizard navigation panel
	WizardPanel wizardPanel;
	
	/**
	 * Create an empty matrix panel
	 */
	public GuidedWizardPanel()
	{	
		VerticalPanel panel = new VerticalPanel();
		
		wizardPanel = new WizardPanel(panelList);
		wizardPanel.addSaveListener(this);
		panel.add(wizardPanel);

		// set up listener relationships
		solvingForPanel.addSolvingForListener(studyGroupsPanel);
		solvingForPanel.addSolvingForListener(resultsPanel);
		outcomesPanel.addOutcomesListener(studyGroupsPanel);
		outcomesPanel.addOutcomesListener(hypothesisPanel);
		outcomesPanel.addOutcomesListener(effectSizePanel);
		predictorsPanel.addPredictorsListener(studyGroupsPanel);
		predictorsPanel.addPredictorsListener(hypothesisPanel);
		predictorsPanel.addPredictorsListener(effectSizePanel);
		predictorsPanel.addCovariateListener(optionsPanel);
		// initialize
		initWidget(panel);
	}
	
	/**
	 * Fill in the wizard from an XML description of the study matrices
	 */
	public void loadFromXML(Document doc)
	{
		Node studyNode = doc.getElementsByTagName(GlimmpseConstants.TAG_STUDY).item(0);
		if (studyNode != null)
		{
			NodeList children = studyNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				String childName = child.getNodeName();
				if (GlimmpseConstants.TAG_SOLVING_FOR.equals(childName))
					solvingForPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_ALPHA_LIST.equals(childName))
					alphaPanel.loadFromNode(child);
				
				
				
				
				else if (GlimmpseConstants.TAG_OPTIONS.equals(childName))
					optionsPanel.loadFromNode(child);
					
			}
		}
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
		buffer.append(solvingForPanel.toRequestXML());
		buffer.append(alphaPanel.toXML());
//		buffer.append(designPanel.toXML());
//		buffer.append(betaPanel.toXML());
//		buffer.append(contrastPanel.toXML());
//		buffer.append(thetaPanel.toXML());
//		buffer.append(covariancePanel.toXML());
		buffer.append(optionsPanel.toRequestXML());
		buffer.append("</" + GlimmpseConstants.TAG_POWER_PARAMETERS + ">");
		
		return buffer.toString();
	}

	@Override
	public String getStudyDesignXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<" + GlimmpseConstants.TAG_POWER_PARAMETERS + ">");
		buffer.append(solvingForPanel.toStudyXML());
		buffer.append(alphaPanel.toXML());
//		buffer.append(designPanel.toXML());
//		buffer.append(betaPanel.toXML());
//		buffer.append(contrastPanel.toXML());
//		buffer.append(thetaPanel.toXML());
//		buffer.append(covariancePanel.toXML());
		buffer.append(optionsPanel.toStudyXML());
		buffer.append("</" + GlimmpseConstants.TAG_POWER_PARAMETERS + ">");
		
		return buffer.toString();
	}

	/**
	 * Returns the input mode name
	 */
	@Override
	public String getModeName()
	{
		return MODE_NAME;
	}

	/**
	 * Save the study design, results, or power curve
	 * @param type the type of save requested
	 */
	@Override
	public void onSave(SaveType type)
	{
		switch(type)
		{
		case STUDY:
			wizardPanel.sendSaveRequest(getStudyDesignXML(), DEFAULT_STUDY_FILENAME);
			break;
		case RESULTS:
			wizardPanel.sendSaveRequest(resultsPanel.dataTableToCSV(),DEFAULT_RESULTS_FILENAME);
			break;
		case CURVE:
			resultsPanel.saveCurveData();
			break;
		}	
	}
}
