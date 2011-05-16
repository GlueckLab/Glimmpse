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
package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.HypothesisListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Mean differences panel
 */
public class MeanDifferencesIndependentMeasuresPanel extends WizardStepPanel
implements HypothesisListener, ChangeHandler, CovariateListener,
OutcomesListener, PredictorsListener
{
	private static final int COLUMN_LABEL = 0;
	private static final int COLUMN_TEXTBOX = 1;
    protected VerticalPanel mainEffectContainer = new VerticalPanel();
    protected HTML mainEffectHypothesisHTML = new HTML("");
    protected HTML mainEffectQuestionHTML = new HTML("");
    protected FlexTable outcomesTable = new FlexTable();
    protected HTML errorHTML = new HTML("");
    protected VerticalPanel interactionEffectContainer = new VerticalPanel();
    protected HTML interactionEffectHypothesisHTML = new HTML("");
    protected HTML interactionEffectQuestionHTML = new HTML("");

    protected FlexTable meansTable = new FlexTable();
    
    protected String predictor = null;
    protected String interactionPredictor = null;
    protected int numOutcomes = 0;
    protected int numGroups = 0;
    protected DataTable groups = null;
    protected boolean hasCovariate = false;
    
    private class OutcomeTextBox extends TextBox 
    {
    	public String outcome;
    	public OutcomeTextBox(String outcome, ChangeHandler handler)
    	{
    		super();
    		addChangeHandler(handler);
    	}
    }
    
	public MeanDifferencesIndependentMeasuresPanel()
	{
		VerticalPanel panel = new VerticalPanel();
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.meanDifferenceTitle());
        HTML description = new HTML(Glimmpse.constants.meanDifferenceDescription());
        // create the main effect entry form
        mainEffectContainer.add(mainEffectHypothesisHTML);
        mainEffectContainer.add(mainEffectQuestionHTML);
        
        // create the interaction effect entry form
        interactionEffectContainer.add(interactionEffectHypothesisHTML);
        interactionEffectContainer.add(interactionEffectQuestionHTML);

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(mainEffectContainer);
        panel.add(interactionEffectContainer);
        panel.add(outcomesTable);
        panel.add(errorHTML);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        outcomesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        initWidget(panel);
	}
	
	@Override
	public void reset()
	{
		outcomesTable.removeAllRows();
		// TODO
	}

	public void onEnter()
	{
		// clear the outcome text boxes
		for(int i = 0; i < outcomesTable.getRowCount(); i++)
		{
			((TextBox) outcomesTable.getWidget(i, COLUMN_TEXTBOX)).setText("");
		}
		TextValidation.displayOkay(errorHTML, "");
	}
	
	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMainEffectsHypothesis(String predictor)
	{
		this.predictor = predictor;
		this.interactionPredictor = null;
		interactionEffectContainer.setVisible(false);
		mainEffectContainer.setVisible(true);
		mainEffectHypothesisHTML.setHTML(Glimmpse.constants.meanDifferenceMainEffectHypothesis() + 
				" <u>" + predictor + "</u>");
		mainEffectQuestionHTML.setHTML(Glimmpse.constants.meanDifferenceMainEffectQuestion() + 
				" <u>" + predictor + "</u>?");
	}

	@Override
	public void onInteractionHypothesis(String predictor,
			String interactionPredictor)
	{
		this.predictor = predictor;
		this.interactionPredictor = interactionPredictor;
		interactionEffectContainer.setVisible(true);
		mainEffectContainer.setVisible(false);
		interactionEffectHypothesisHTML.setHTML(Glimmpse.constants.meanDifferenceInteractionEffectHypothesis() +
				" <u>" + predictor + "</u> " + Glimmpse.constants.and() + " <u>" + interactionPredictor + "</u>");
		interactionEffectQuestionHTML.setHTML(Glimmpse.constants.meanDifferenceInteractionEffectQuestion() +
				" <u>" + predictor + "</u> " + Glimmpse.constants.meanDifferenceInteractionEffectQuestionMiddle() + 
				" <u>" + interactionPredictor + "</u>");
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip && complete)
		{
			XMLUtilities.fixedRandomMatrixOpenTag(buffer, GlimmpseConstants.MATRIX_BETA, false);

			int columns = numOutcomes;
			int rows = numGroups;
			// main effects hypothesis
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, rows, columns);
			for(int row = 0; row < rows; row++)
			{
				buffer.append("<r>");
				for(int col = 0; col < columns; col++)
				{
					buffer.append("<c>");
					if (row == 0)
					{
						buffer.append(((TextBox) outcomesTable.getWidget(col, COLUMN_TEXTBOX)).getText());
					}
					else
					{
						buffer.append(0);
					}
					buffer.append("</c>");
				}
				buffer.append("</r>");
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

			if (hasCovariate)
			{
				XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, 1, columns);
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
				for(int col = 0; col < columns; col++)
				{
					XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
					buffer.append(1);
					XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
				}
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		}
		return buffer.toString();
	}

	@Override
	public void onChange(ChangeEvent event)
	{
		TextBox source = (TextBox) event.getSource();
		try
		{
			TextValidation.parseDouble(source.getText(), Double.NEGATIVE_INFINITY, 
					Double.POSITIVE_INFINITY, false);
		}
		catch (NumberFormatException nfe)
		{
			TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidNumber());
			source.setText("");
		}
		checkComplete();
	}

	private void checkComplete()
	{
		boolean noEmpty = true;
		for(int i = 0; i < outcomesTable.getRowCount(); i++)
		{
			String value = ((TextBox) outcomesTable.getWidget(i, COLUMN_TEXTBOX)).getText();
			if (value == null || value.isEmpty())
			{
				noEmpty = false;
				break;
			}		
		}
		if (noEmpty)
			notifyComplete();
		else
			notifyInProgress();
	}
	
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		numOutcomes = outcomes.size();
		outcomesTable.removeAllRows();
		int i = 0;
		for(String outcome: outcomes)
		{
			outcomesTable.setWidget(i, COLUMN_LABEL, new HTML(outcome));
			outcomesTable.setWidget(i, COLUMN_TEXTBOX, new OutcomeTextBox(outcome, this));
			i++;
		}
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap,
			DataTable groups)
	{
		numGroups = groups.getNumberOfRows();
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}	

}
