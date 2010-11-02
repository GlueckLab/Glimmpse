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

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.StudyDesignManager;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener;
import edu.cudenver.bios.glimmpse.client.panels.guided.CategoricalPredictorsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisIndependentMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisRepeatedMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.MeanDifferencesPatternPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.MeanDifferencesScalePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.OutcomesPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.PerGroupSampleSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.RelativeGroupSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.RepeatedMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityCovariatePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityErrorPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityOutcomeCovariatePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityOutcomesPanel;

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
	protected IntroPanel startIntroPanel = new IntroPanel(Glimmpse.constants.startTitle(),
			Glimmpse.constants.startDescription());
	protected SolvingForPanel solvingForPanel = new SolvingForPanel(getModeName());
	protected PowerPanel powerPanel = new PowerPanel();
	// type I error
	protected IntroPanel alphaIntroPanel = new IntroPanel(Glimmpse.constants.alphaIntroTitle(),
			Glimmpse.constants.alphaIntroDescription());
	protected AlphaPanel alphaPanel = new AlphaPanel();
	// predictors
	protected IntroPanel predictorIntroPanel = new IntroPanel(Glimmpse.constants.predictorsIntroTitle(),
			Glimmpse.constants.predictorsIntroDescription());
	protected CategoricalPredictorsPanel catPredictorsPanel = new CategoricalPredictorsPanel();
	protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel();
	protected RelativeGroupSizePanel relativeGroupSizePanel = new RelativeGroupSizePanel();
	protected PerGroupSampleSizePanel perGroupSampleSizePanel = new PerGroupSampleSizePanel();
	// outcomes
	protected IntroPanel outcomesIntroPanel = new IntroPanel(Glimmpse.constants.outcomesIntroTitle(),
			Glimmpse.constants.outcomesIntroDescription());
	protected OutcomesPanel outcomesPanel = new OutcomesPanel();
	protected RepeatedMeasuresPanel repeatedMeasuresPanel = new RepeatedMeasuresPanel();
	// hypotheses
	protected IntroPanel hypothesisIntroPanel = new IntroPanel(Glimmpse.constants.hypothesisIntroTitle(),
			Glimmpse.constants.hypothesisIntroDescription());
	protected HypothesisIndependentMeasuresPanel hypothesisIndependentPanel = 
		new HypothesisIndependentMeasuresPanel();
	protected HypothesisRepeatedMeasuresPanel hypothesisRepeatedPanel = 
		new HypothesisRepeatedMeasuresPanel();
	// mean differences
	protected IntroPanel meanDifferencesIntroPanel = new IntroPanel(Glimmpse.constants.meanDifferenceIntroTitle(),
			Glimmpse.constants.meanDifferenceIntroDescription());
	protected MeanDifferencesPatternPanel meanDifferencesPatternPanel =
		new MeanDifferencesPatternPanel();
	protected MeanDifferencesScalePanel meanDifferencesScalePanel =
		new MeanDifferencesScalePanel();
	// variability
	protected IntroPanel variabilityIntroPanel = new IntroPanel(Glimmpse.constants.variabilityIntroTitle(),
			Glimmpse.constants.variabilityIntroDescription());
	protected VariabilityErrorPanel variabilityErrorPanel = new VariabilityErrorPanel();
	protected VariabilityCovariatePanel variabilityCovariatePanel = new VariabilityCovariatePanel();
	protected VariabilityOutcomesPanel variabilityOutcomePanel = new VariabilityOutcomesPanel();
	protected VariabilityOutcomeCovariatePanel variabilityOutcomeCovariatePanel = 
		new VariabilityOutcomeCovariatePanel();
	// options
	protected OptionsTestsPanel optionsTestsPanel = new OptionsTestsPanel(getModeName());
	protected OptionsPowerMethodsPanel optionsPowerMethodsPanel = 
		new OptionsPowerMethodsPanel(getModeName());
	protected OptionsDisplayPanel optionsDisplayPanel = new OptionsDisplayPanel(getModeName());
	// results
	protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(this);
	
    // list of panels for the wizard
	WizardStepPanel[][] panelList = {
			{startIntroPanel, solvingForPanel, powerPanel},
			{alphaIntroPanel, alphaPanel}, 
			{predictorIntroPanel, catPredictorsPanel, covariatePanel, 
				relativeGroupSizePanel, perGroupSampleSizePanel}, 
			{outcomesIntroPanel, outcomesPanel, repeatedMeasuresPanel}, 
			{hypothesisIntroPanel, hypothesisIndependentPanel, hypothesisRepeatedPanel},
			{meanDifferencesIntroPanel, meanDifferencesPatternPanel,
				meanDifferencesScalePanel},
			{variabilityIntroPanel, variabilityErrorPanel, variabilityCovariatePanel, 
					variabilityOutcomePanel, variabilityOutcomeCovariatePanel},
			{optionsTestsPanel, optionsPowerMethodsPanel, optionsDisplayPanel},
			{resultsPanel}
	};
	// labels for each group of panels
	String[] groupLabels = {
		Glimmpse.constants.stepsLeftStart(),
		Glimmpse.constants.stepsLeftAlpha(),
		Glimmpse.constants.stepsLeftPredictors(),
		Glimmpse.constants.stepsLeftResponses(),
		Glimmpse.constants.stepsLeftHypotheses(),
		Glimmpse.constants.stepsLeftMeanDifferences(),
		Glimmpse.constants.stepsLeftVariability(),
		Glimmpse.constants.stepsLeftOptions(),
		Glimmpse.constants.stepsLeftResults()
	};
	// wizard navigation panel
	WizardPanel wizardPanel;
	
	/**
	 * Create an empty matrix panel
	 */
	public GuidedWizardPanel()
	{	
		VerticalPanel panel = new VerticalPanel();
		
		wizardPanel = new WizardPanel(panelList, groupLabels);
		wizardPanel.addSaveListener(this);
		panel.add(wizardPanel);

		// set up listener relationships
		solvingForPanel.addSolvingForListener(powerPanel);
		solvingForPanel.addSolvingForListener(perGroupSampleSizePanel);
		solvingForPanel.addSolvingForListener(resultsPanel);
		// listeners for outcome measures
		outcomesPanel.addOutcomesListener(hypothesisIndependentPanel);
		outcomesPanel.addOutcomesListener(hypothesisRepeatedPanel);
		outcomesPanel.addOutcomesListener(variabilityErrorPanel);
		outcomesPanel.addOutcomesListener(variabilityOutcomePanel);
		outcomesPanel.addOutcomesListener(variabilityOutcomeCovariatePanel);
		// listeners for predictor information
		catPredictorsPanel.addPredictorsListener(relativeGroupSizePanel);
		catPredictorsPanel.addPredictorsListener(hypothesisIndependentPanel);
		catPredictorsPanel.addPredictorsListener(hypothesisRepeatedPanel);
		catPredictorsPanel.addPredictorsListener(meanDifferencesPatternPanel);
		// listeners for baseline covariates
		covariatePanel.addCovariateListener(hypothesisIndependentPanel);
		covariatePanel.addCovariateListener(hypothesisRepeatedPanel);
		covariatePanel.addCovariateListener(meanDifferencesPatternPanel);
		covariatePanel.addCovariateListener(relativeGroupSizePanel);
		covariatePanel.addCovariateListener(variabilityErrorPanel);
		covariatePanel.addCovariateListener(variabilityOutcomePanel);
		covariatePanel.addCovariateListener(variabilityCovariatePanel);
		covariatePanel.addCovariateListener(variabilityOutcomeCovariatePanel);
		covariatePanel.addCovariateListener(optionsTestsPanel);
		covariatePanel.addCovariateListener(optionsPowerMethodsPanel);
		// listeners for hypotheses
		hypothesisIndependentPanel.addHypothesisListener(meanDifferencesPatternPanel);
		hypothesisRepeatedPanel.addHypothesisListener(meanDifferencesPatternPanel);
		// TODO: covariatePanel.addCovariateListener(effectSizePanel);
		relativeGroupSizePanel.addRelativeGroupSizeListener(hypothesisIndependentPanel);
		optionsDisplayPanel.addOptionsListener(resultsPanel);

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
				else if (GlimmpseConstants.TAG_TEST_LIST.equals(childName))
					optionsTestsPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_TEST_LIST.equals(childName))
					optionsTestsPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_QUANTILE_LIST.equals(childName))
					optionsPowerMethodsPanel.loadFromNode(child);				
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
		buffer.append(powerPanel.toRequestXML());
		buffer.append(alphaPanel.toXML());
		buffer.append(relativeGroupSizePanel.toRequestXML());
		buffer.append(hypothesisIndependentPanel.toRequestXML());
		buffer.append(hypothesisRepeatedPanel.toRequestXML());
		buffer.append(meanDifferencesPatternPanel.toRequestXML());
		buffer.append(meanDifferencesScalePanel.toRequestXML());
		buffer.append(variabilityErrorPanel.toRequestXML());
		buffer.append(variabilityOutcomePanel.toRequestXML());
		buffer.append(variabilityCovariatePanel.toRequestXML());
		buffer.append(variabilityOutcomeCovariatePanel.toRequestXML());
		buffer.append(optionsTestsPanel.toRequestXML());
		buffer.append(optionsPowerMethodsPanel.toRequestXML());
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
		buffer.append(optionsTestsPanel.toStudyXML());
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
