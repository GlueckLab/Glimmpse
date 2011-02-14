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
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.StudyDesignManager;
import edu.cudenver.bios.glimmpse.client.listener.CancelListener;
import edu.cudenver.bios.glimmpse.client.listener.SaveListener;
import edu.cudenver.bios.glimmpse.client.panels.guided.PerGroupSampleSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.BetaPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.BetaScalePanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.BetweenSubjectContrastPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.DesignPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.SigmaCovariateMatrixPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.SigmaErrorMatrixPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.SigmaOutcomeCovariateMatrixPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.SigmaOutcomesMatrixPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.SigmaScalePanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.ThetaPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.WithinSubjectContrastPanel;

/**
 * Wizard panel for matrix input mode.  Contains separate steps
 * for each type of matrix required for power calculations on the GLMM
 */
public class MatrixWizardPanel extends Composite
implements StudyDesignManager, SaveListener
{
	// default filenames
	protected static final String DEFAULT_STUDY_FILENAME = "study.xml";
	protected static final String DEFAULT_RESULTS_FILENAME = "power.csv";
	protected static final String DEFAULT_CURVE_FILENAME = "powerCurve.jpg";
	// content panels 
	protected IntroPanel introPanel = new IntroPanel(Glimmpse.constants.matrixIntroTitle(),
			Glimmpse.constants.matrixIntroDescription());
	protected SolvingForPanel solvingForPanel = new SolvingForPanel(getModeName());
	protected PowerPanel powerPanel = new PowerPanel();
    protected AlphaPanel alphaPanel = new AlphaPanel();
    protected DesignPanel designPanel = new DesignPanel();
	protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel();
	protected PerGroupSampleSizePanel perGroupSampleSizePanel = new PerGroupSampleSizePanel();
	
    protected BetweenSubjectContrastPanel betweenContrastPanel = new BetweenSubjectContrastPanel();
    protected WithinSubjectContrastPanel withinContrastPanel = new WithinSubjectContrastPanel();

    protected BetaPanel betaPanel = new BetaPanel();
    protected BetaScalePanel betaScalePanel = new BetaScalePanel();
    protected ThetaPanel thetaPanel = new ThetaPanel();
    
    protected SigmaErrorMatrixPanel sigmaErrorPanel = new SigmaErrorMatrixPanel();
    protected SigmaOutcomesMatrixPanel sigmaOutcomesPanel = new SigmaOutcomesMatrixPanel();
    protected SigmaOutcomeCovariateMatrixPanel sigmaOutcomeCovariatePanel = 
    	new SigmaOutcomeCovariateMatrixPanel();
    protected SigmaCovariateMatrixPanel sigmaCovariatePanel = new SigmaCovariateMatrixPanel();
    protected SigmaScalePanel sigmaScalePanel = new SigmaScalePanel();
	// options
	protected OptionsTestsPanel optionsTestsPanel = new OptionsTestsPanel(getModeName());
	protected OptionsPowerMethodsPanel optionsPowerMethodsPanel = 
		new OptionsPowerMethodsPanel(getModeName());
	protected OptionsDisplayPanel optionsDisplayPanel = new OptionsDisplayPanel(getModeName());
	// results
	protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(this);
    // list of panels for the wizard
	WizardStepPanel[][] panelList = {
			{introPanel, solvingForPanel, powerPanel},
			{alphaPanel},
			{designPanel, covariatePanel, perGroupSampleSizePanel},
			{betaPanel, betaScalePanel},
			{betweenContrastPanel, withinContrastPanel},
			{thetaPanel},
			{sigmaErrorPanel, sigmaOutcomesPanel, sigmaCovariatePanel, 
				sigmaOutcomeCovariatePanel, sigmaScalePanel},
			{optionsTestsPanel, optionsPowerMethodsPanel, optionsDisplayPanel},
			{resultsPanel}
	};
	
	String[] groupLabels = {
		Glimmpse.constants.stepsLeftStart(),
		Glimmpse.constants.stepsLeftAlpha(),
		Glimmpse.constants.stepsLeftDesign(),
		Glimmpse.constants.stepsLeftBeta(),
		Glimmpse.constants.stepsLeftContrast(),
		Glimmpse.constants.stepsLeftTheta(),
		Glimmpse.constants.stepsLeftVariability(),
		Glimmpse.constants.stepsLeftOptions(),
		Glimmpse.constants.stepsLeftResults()
	};
	
	// wizard navigation panel
	WizardPanel wizardPanel;
	
	/**
	 * Create an empty matrix panel
	 */
	public MatrixWizardPanel()
	{	
		VerticalPanel panel = new VerticalPanel();
		
		wizardPanel = new WizardPanel(panelList, groupLabels);
		wizardPanel.addSaveListener(this);
		panel.add(wizardPanel);

		// set up listener relationships between the matrix panels
		// this maintains matrix conformance
		// set up listener relationships
		solvingForPanel.addSolvingForListener(powerPanel);
		solvingForPanel.addSolvingForListener(betaScalePanel);
		solvingForPanel.addSolvingForListener(resultsPanel);
		solvingForPanel.addSolvingForListener(perGroupSampleSizePanel);
		designPanel.addMatrixResizeListener(betaPanel);
		designPanel.addMatrixResizeListener(betweenContrastPanel);
		covariatePanel.addCovariateListener(designPanel);
		covariatePanel.addCovariateListener(betaPanel);
		covariatePanel.addCovariateListener(betweenContrastPanel);
		covariatePanel.addCovariateListener(sigmaErrorPanel);
		covariatePanel.addCovariateListener(sigmaOutcomesPanel);
		covariatePanel.addCovariateListener(sigmaOutcomeCovariatePanel);
		covariatePanel.addCovariateListener(sigmaCovariatePanel);
		covariatePanel.addCovariateListener(optionsTestsPanel);
		covariatePanel.addCovariateListener(optionsPowerMethodsPanel);
		sigmaCovariatePanel.addVariabilityListener(designPanel);
		betweenContrastPanel.addMatrixResizeListener(thetaPanel);
		withinContrastPanel.addMatrixResizeListener(thetaPanel);
		optionsDisplayPanel.addOptionsListener(resultsPanel);
		betaPanel.addMatrixResizeListener(betweenContrastPanel);
		betaPanel.addMatrixResizeListener(withinContrastPanel);
		betaPanel.addMatrixResizeListener(sigmaErrorPanel);
		betaPanel.addMatrixResizeListener(sigmaOutcomesPanel);
		betaPanel.addMatrixResizeListener(sigmaOutcomeCovariatePanel);
		betaPanel.addMatrixResizeListener(sigmaCovariatePanel);
		
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
				else if (GlimmpseConstants.TAG_BETA_SCALE_LIST.equalsIgnoreCase(childName))
					betaScalePanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_SIGMA_SCALE_LIST.equalsIgnoreCase(childName))
					sigmaScalePanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_ESSENCE_MATRIX.equalsIgnoreCase(childName))
					designPanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_COVARIATE.equalsIgnoreCase(childName))
					covariatePanel.loadFromNode(child);
				else if (GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX.equalsIgnoreCase(childName))
				{
					NamedNodeMap attrs = child.getAttributes();
					Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
					if (nameNode != null)
					{
						String name = nameNode.getNodeValue();
						if (GlimmpseConstants.MATRIX_BETA.equalsIgnoreCase(name))
							betaPanel.loadFromNode(child);
						else if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST.equalsIgnoreCase(name))
							betweenContrastPanel.loadFromNode(child);
					}
				}
				else if (GlimmpseConstants.TAG_MATRIX.equalsIgnoreCase(childName))
				{
					NamedNodeMap attrs = child.getAttributes();
					Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
					if (nameNode != null)
					{
						String name = nameNode.getNodeValue();
						if (GlimmpseConstants.MATRIX_WITHIN_CONTRAST.equalsIgnoreCase(name))
							withinContrastPanel.loadFromNode(child);
						else if (GlimmpseConstants.MATRIX_SIGMA_ERROR.equalsIgnoreCase(name))
							sigmaErrorPanel.loadFromNode(child);
						else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME.equalsIgnoreCase(name))
							sigmaOutcomesPanel.loadFromNode(child);
						else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE.equalsIgnoreCase(name))
							sigmaOutcomeCovariatePanel.loadFromNode(child);
						else if (GlimmpseConstants.MATRIX_SIGMA_COVARIATE.equalsIgnoreCase(name))
							sigmaCovariatePanel.loadFromNode(child);
						else if (GlimmpseConstants.MATRIX_THETA.equalsIgnoreCase(name))
							thetaPanel.loadFromNode(child);
					}
				}
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
		buffer.append(alphaPanel.toXML());
		buffer.append(betaScalePanel.toXML());
		buffer.append(powerPanel.toXML());
		buffer.append(optionsTestsPanel.toRequestXML());
		buffer.append(optionsPowerMethodsPanel.toRequestXML());
		buffer.append(perGroupSampleSizePanel.toXML());
		buffer.append(sigmaScalePanel.toXML());
		buffer.append(designPanel.toXML());
		buffer.append(betaPanel.toXML());
		buffer.append(betweenContrastPanel.toXML());
		buffer.append(withinContrastPanel.toXML());
		buffer.append(thetaPanel.toXML());		
		buffer.append(sigmaErrorPanel.toXML());
		buffer.append(sigmaOutcomesPanel.toXML());
		buffer.append(sigmaOutcomeCovariatePanel.toXML());
		buffer.append(sigmaCovariatePanel.toXML());

		buffer.append("</" + GlimmpseConstants.TAG_POWER_PARAMETERS + ">");

		return buffer.toString();
	}

	@Override
	public String getStudyDesignXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_STUDY);
		buffer.append(" ");
		buffer.append(GlimmpseConstants.ATTR_MODE);
		buffer.append("='");
		buffer.append(getModeName());
		buffer.append("'>");

		buffer.append(solvingForPanel.toStudyXML());
		buffer.append(alphaPanel.toXML());
		buffer.append(betaScalePanel.toXML());
		buffer.append(powerPanel.toXML());
		buffer.append(optionsTestsPanel.toStudyXML());
		buffer.append(optionsPowerMethodsPanel.toStudyXML());
		buffer.append(optionsDisplayPanel.toStudyXML());
		buffer.append(perGroupSampleSizePanel.toXML());
		buffer.append(sigmaScalePanel.toXML());
		buffer.append(designPanel.toXML());
		buffer.append(covariatePanel.toStudyXML());
		buffer.append(betaPanel.toXML());
		buffer.append(betweenContrastPanel.toXML());
		buffer.append(withinContrastPanel.toXML());
		buffer.append(thetaPanel.toXML());		
		buffer.append(sigmaErrorPanel.toXML());
		buffer.append(sigmaOutcomesPanel.toXML());
		buffer.append(sigmaOutcomeCovariatePanel.toXML());
		buffer.append(sigmaCovariatePanel.toXML());
		
		buffer.append("</");
		buffer.append(GlimmpseConstants.TAG_STUDY);
		buffer.append(">");
		
		return buffer.toString();
	}
	
	@Override
	public String getModeName()
	{
		return "matrix";
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
