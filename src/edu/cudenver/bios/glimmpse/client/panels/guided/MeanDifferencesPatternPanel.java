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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.HypothesisListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Panel for patterns of mean differences - unused
 */
public class MeanDifferencesPatternPanel extends WizardStepPanel
implements PredictorsListener, HypothesisListener, 
CovariateListener, ClickHandler
{
	private static final String PATTERN_RADIO_GROUP = "patternGroup";
	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	private enum HypothesisType
	{
		MAIN_EFFECT,
		INTERACTION,
		TREND
	};
	
	protected FlexTable patternTable = new FlexTable();
	protected List<String> outcomes = null;
	protected HashMap<String, ArrayList<String>> predictorMap = null;
	protected DataTable groups = null;
	protected HashMap<String, Integer> groupColumnLookup = new HashMap<String,Integer>();
	protected HypothesisType hypothesisType = HypothesisType.MAIN_EFFECT ;
	protected boolean hasCovariate = false;
	protected int numColumns = -1;
	
	public MeanDifferencesPatternPanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
		
        HTML header = new HTML(Glimmpse.constants.meanDifferencePatternTitle());
        HTML description = new HTML(Glimmpse.constants.meanDifferencePatternDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(patternTable);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		initWidget(panel);
		// TODO Auto-generated constructor stub
	}

	@Override 
	public void onEnter()
	{
		switch (hypothesisType)
		{
		case MAIN_EFFECT:
			buildMainEffectPatterns();
			break;
		case INTERACTION:
			buildInteractionPatterns();
			break;
		}
	}
	
	private void buildMainEffectPatterns()
	{
		if (numColumns > 0)
		{
			ArrayList<Integer> data = new ArrayList<Integer>();
			for(int i = 0; i < numColumns-1; i++) data.add(1);
			data.add(2);
			RadioButton rb = new RadioButton(PATTERN_RADIO_GROUP, "One group differs");
			rb.addClickHandler(this);
			patternTable.setWidget(0, 0, new MeanDifferenceMainEffectBarChart(data, WIDTH, HEIGHT));
			patternTable.setWidget(1, 0, rb);
			if (numColumns > 2)
			{
				// linear dose response data
				data.clear();
				for(int i = 0; i < numColumns; i++) data.add(i+1);
				rb = new RadioButton(PATTERN_RADIO_GROUP,  "Linear Dose-Response");
				rb.addClickHandler(this);
				patternTable.setWidget(0, 1, new MeanDifferenceMainEffectBarChart(data, WIDTH, HEIGHT));
				patternTable.setWidget(1, 1, rb);

				
				// exponential dose response
				data.clear();
				for(int i = 0; i < numColumns; i++) data.add((i+1)*(i+1));
				rb = new RadioButton(PATTERN_RADIO_GROUP, "Exponential Dose-Response");
				rb.addClickHandler(this); 
				patternTable.setWidget(0, 2, new MeanDifferenceMainEffectBarChart(data, WIDTH, HEIGHT));
				patternTable.setWidget(1, 2, rb);


			}
		}
	}
	
	private void buildInteractionPatterns()
	{
		
	}
	
	
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMainEffectsHypothesis(String predictor)
	{
		hypothesisType = HypothesisType.MAIN_EFFECT;
		if (predictorMap != null)
		{
			List<String> categories = predictorMap.get(predictor);
			if (!categories.isEmpty()) numColumns = categories.size();
		}
	}

	@Override
	public void onInteractionHypothesis(String predictor,
			String interactionPredictor)
	{
		hypothesisType = HypothesisType.INTERACTION;
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap,
			DataTable groups)
	{
		this.predictorMap = predictorMap;
		this.groups = groups;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		notifyComplete();
	}

	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		int rows = 0; //groupData.getNumberOfRows();
		int cols = 0; //outcomesList.size(); // TODO: repeated MEASURES!!!!
		
		// convert the estimated means into a beta matrix
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		buffer.append(" ");
		buffer.append(GlimmpseConstants.ATTR_NAME);
		buffer.append("='");
		buffer.append(GlimmpseConstants.MATRIX_BETA);
		buffer.append("' combineHorizontal='false'>");
		// fixed part of the beta matrix
		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, rows, cols);

//		for(int row = 0; row < rows; row++)
//		{
//			buffer.append("<r>");
//			for(int col = 0; col < cols; col++)
//			{
//				buffer.append("<c>");
//				
//				TextBox tb = (TextBox) effectSizeTable.getWidget(row+2, predictorMap.size() + col);
//				buffer.append(tb.getText());
//				
//				buffer.append("</c>");
//			}
//			buffer.append("</r>");
//		}
//		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

		// random part of the beta matrix
		if (hasCovariate)
		{
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, cols);
			
			buffer.append("<r>");
			for(int col = 0; col < cols; col++)
			{
				buffer.append("<c>1</c>");
			}
			buffer.append("<r>");
			
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		
		// close the fixed/rand beta matrix
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		
		return buffer.toString();
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}

}
