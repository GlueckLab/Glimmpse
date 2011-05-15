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
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener;
import edu.cudenver.bios.glimmpse.client.panels.guided.CategoricalPredictorsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisDoublyRepeatedMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisIndependentMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisRepeatedMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.MeanDifferencesIndependentMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.MeanDifferencesPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.MeanDifferencesRepeatedMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.MeanDifferencesScalePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.OutcomesPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.PerGroupSampleSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.RelativeGroupSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.RepeatedMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityCovariateOutcomePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityCovariatePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityIndependentMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityRepeatedMeasuresPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityScalePanel;

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
	protected HypothesisDoublyRepeatedMeasuresPanel hypothesisDoublyRepeatedPanel = 
		new HypothesisDoublyRepeatedMeasuresPanel();
	// mean differences
	protected IntroPanel meanDifferencesIntroPanel = new IntroPanel(Glimmpse.constants.meanDifferenceIntroTitle(),
			Glimmpse.constants.meanDifferenceIntroDescription());
	protected MeanDifferencesPanel meanDifferencesPanel = new MeanDifferencesPanel();
	protected MeanDifferencesIndependentMeasuresPanel meanDifferencesIndependentPanel =
		new MeanDifferencesIndependentMeasuresPanel();
	protected MeanDifferencesRepeatedMeasuresPanel meanDifferencesRepeatedPanel =
		new MeanDifferencesRepeatedMeasuresPanel();
	protected MeanDifferencesScalePanel meanDifferencesScalePanel = 
		new MeanDifferencesScalePanel();
	// variability
	protected IntroPanel variabilityIntroPanel = new IntroPanel(Glimmpse.constants.variabilityIntroTitle(),
			Glimmpse.constants.variabilityIntroDescription());
	protected VariabilityIndependentMeasuresPanel variabilityIndependentPanel = 
		new VariabilityIndependentMeasuresPanel();
	protected VariabilityRepeatedMeasuresPanel variabilityRepeatedPanel = 
		new VariabilityRepeatedMeasuresPanel();
	protected VariabilityCovariatePanel variabilityCovariatePanel = new VariabilityCovariatePanel();
	protected VariabilityCovariateOutcomePanel variabilityCovariateOutcomePanel = 
		new VariabilityCovariateOutcomePanel();
	protected VariabilityScalePanel variabilityScalePanel = new VariabilityScalePanel();
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
			{alphaPanel}, 
			{predictorIntroPanel, catPredictorsPanel, covariatePanel, 
				relativeGroupSizePanel, perGroupSampleSizePanel}, 
			{outcomesIntroPanel, outcomesPanel, repeatedMeasuresPanel}, 
			{hypothesisIntroPanel, hypothesisIndependentPanel, hypothesisRepeatedPanel,
				hypothesisDoublyRepeatedPanel},
			{meanDifferencesIntroPanel, meanDifferencesPanel,
				meanDifferencesRepeatedPanel, meanDifferencesScalePanel},
			{variabilityIntroPanel, variabilityIndependentPanel, variabilityRepeatedPanel,
					variabilityCovariatePanel, variabilityCovariateOutcomePanel, variabilityScalePanel},
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
		outcomesPanel.addOutcomesListener(meanDifferencesIndependentPanel);
		outcomesPanel.addOutcomesListener(meanDifferencesPanel);
		outcomesPanel.addOutcomesListener(variabilityIndependentPanel);
		outcomesPanel.addOutcomesListener(variabilityRepeatedPanel);
		outcomesPanel.addOutcomesListener(variabilityCovariateOutcomePanel);
		// listeners for predictor information
		catPredictorsPanel.addPredictorsListener(relativeGroupSizePanel);
		catPredictorsPanel.addPredictorsListener(hypothesisIndependentPanel);
		catPredictorsPanel.addPredictorsListener(hypothesisRepeatedPanel);
		catPredictorsPanel.addPredictorsListener(meanDifferencesIndependentPanel);
		catPredictorsPanel.addPredictorsListener(meanDifferencesPanel);
		// listeners for relative group sizes
		relativeGroupSizePanel.addRelativeGroupSizeListener(hypothesisIndependentPanel);
		relativeGroupSizePanel.addRelativeGroupSizeListener(hypothesisRepeatedPanel);
		// listeners for baseline covariates
		covariatePanel.addCovariateListener(meanDifferencesIndependentPanel);
		covariatePanel.addCovariateListener(meanDifferencesPanel);
		// TODO: covariatePanel.addCovariateListener(meanDifferencesRepeatedPanel);
		covariatePanel.addCovariateListener(hypothesisIndependentPanel);
		covariatePanel.addCovariateListener(hypothesisRepeatedPanel);
		covariatePanel.addCovariateListener(variabilityIndependentPanel);
		// TODO: covariatePanel.addCovariateListener(variabilityRepeatedPanel);
		covariatePanel.addCovariateListener(variabilityCovariatePanel);
		covariatePanel.addCovariateListener(variabilityCovariateOutcomePanel);
		covariatePanel.addCovariateListener(optionsTestsPanel);
		covariatePanel.addCovariateListener(optionsPowerMethodsPanel);
		// listeners for repeated measures 
		repeatedMeasuresPanel.addRepeatedMeasuresListener(hypothesisIndependentPanel);
		repeatedMeasuresPanel.addRepeatedMeasuresListener(hypothesisRepeatedPanel);
		// listeners for hypotheses
		hypothesisIndependentPanel.addHypothesisListener(meanDifferencesIndependentPanel);
		hypothesisRepeatedPanel.addHypothesisListener(meanDifferencesRepeatedPanel);
		// group size listeners
		relativeGroupSizePanel.addRelativeGroupSizeListener(hypothesisIndependentPanel);
		// variability listeners
		variabilityIndependentPanel.addVariabilityListener(variabilityCovariateOutcomePanel);
		variabilityCovariatePanel.addVariabilityListener(variabilityCovariateOutcomePanel);
		optionsDisplayPanel.addChartOptionsListener(resultsPanel);
		
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
				if (GlimmpseConstants.TAG_SOLVING_FOR.equalsIgnoreCase(childName))
					solvingForPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_ALPHA_LIST.equalsIgnoreCase(childName))
					alphaPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_TEST_LIST.equalsIgnoreCase(childName))
					optionsTestsPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_QUANTILE_LIST.equalsIgnoreCase(childName))
					optionsPowerMethodsPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_POWER_LIST.equalsIgnoreCase(childName))
					powerPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_POWER_METHOD_LIST.equalsIgnoreCase(childName))
					optionsPowerMethodsPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_SAMPLE_SIZE_LIST.equalsIgnoreCase(childName))
					perGroupSampleSizePanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_CATEGORICAL_PREDICTORS.equalsIgnoreCase(childName))
					catPredictorsPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_RELATIVE_GROUP_SIZE_LIST.equalsIgnoreCase(childName))
					relativeGroupSizePanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_OUTCOMES_LIST.equalsIgnoreCase(childName))
					outcomesPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_HYPOTHESIS.equalsIgnoreCase(childName))
					hypothesisIndependentPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_REPEATED_MEASURES.equalsIgnoreCase(childName))
					repeatedMeasuresPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_BETA_SCALE_LIST.equalsIgnoreCase(childName))
					meanDifferencesScalePanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_SIGMA_SCALE_LIST.equalsIgnoreCase(childName))
					variabilityScalePanel.loadFromNode(child);

				/*
				TODO: finish upload for these panels
				{hypothesisRepeatedPanel, hypothesisDoublyRepeatedPanel},
				{meanDifferencesIndependentPanel,	meanDifferencesRepeatedPanel, },
				{variabilityIndependentPanel, variabilityRepeatedPanel,
						variabilityCovariatePanel, variabilityCovariateOutcomePanel, },
				
				*/
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
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_POWER_PARAMETERS);
		buffer.append(solvingForPanel.toRequestXML());
		buffer.append(alphaPanel.toXML());
		buffer.append(powerPanel.toXML());
		buffer.append(perGroupSampleSizePanel.toXML());
		buffer.append(meanDifferencesScalePanel.toRequestXML());
		buffer.append(variabilityScalePanel.toRequestXML());
		buffer.append(optionsTestsPanel.toRequestXML());
		buffer.append(optionsPowerMethodsPanel.toRequestXML());
		buffer.append(relativeGroupSizePanel.toRequestXML()); // outputs design matrix
		buffer.append(hypothesisIndependentPanel.toRequestXML());
		buffer.append(hypothesisRepeatedPanel.toRequestXML());
		buffer.append(meanDifferencesPanel.toXML());
//		buffer.append(meanDifferencesIndependentPanel.toRequestXML());
//		buffer.append(meanDifferencesRepeatedPanel.toRequestXML());
		buffer.append(variabilityIndependentPanel.toRequestXML());
		buffer.append(variabilityRepeatedPanel.toRequestXML());
		buffer.append(variabilityCovariatePanel.toRequestXML());
		buffer.append(variabilityCovariateOutcomePanel.toRequestXML());
		
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_POWER_PARAMETERS);
		
		return buffer.toString();
	}

	@Override
	public String getStudyDesignXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_STUDY, 
				GlimmpseConstants.ATTR_MODE + "='" + getModeName() + "'");
		
		buffer.append(solvingForPanel.toStudyXML());
		buffer.append(alphaPanel.toXML());
		buffer.append(powerPanel.toXML());
		buffer.append(optionsTestsPanel.toStudyXML());
		buffer.append(optionsPowerMethodsPanel.toStudyXML());
		buffer.append(optionsDisplayPanel.toStudyXML());
		// note: order matters between cat predictors and relative group size here
		// for upload to work properly
		buffer.append(catPredictorsPanel.toStudyXML());
		buffer.append(relativeGroupSizePanel.toStudyXML());
		buffer.append(perGroupSampleSizePanel.toXML());
		buffer.append(outcomesPanel.toStudyXML());
		buffer.append(hypothesisIndependentPanel.toStudyXML());
		buffer.append(repeatedMeasuresPanel.toStudyXML());
		buffer.append(meanDifferencesPanel.toXML());
		buffer.append(meanDifferencesScalePanel.toStudyXML());
		buffer.append(variabilityScalePanel.toStudyXML());
		// TODO	
/*

		{hypothesisRepeatedPanel, hypothesisDoublyRepeatedPanel},
		{meanDifferencesIndependentPanel,	meanDifferencesRepeatedPanel, },
		{variabilityIndependentPanel, variabilityRepeatedPanel,
				variabilityCovariatePanel, variabilityCovariateOutcomePanel, },
		
		*/



		
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_STUDY);
		
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
	 * Handle save events
	 */
	@Override
	public void onSave()
	{
		wizardPanel.sendSaveRequest(getStudyDesignXML(), DEFAULT_STUDY_FILENAME);
	}

	@Override
	public void sendSaveRequest(String data, String filename)
	{
		wizardPanel.sendSaveRequest(data, filename);
	}
}
