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

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.HypothesisListener;
import edu.cudenver.bios.glimmpse.client.listener.RepeatedMeasuresListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class MeanDifferencesRepeatedMeasuresPanel extends WizardStepPanel
implements HypothesisListener, RepeatedMeasuresListener
{
    protected HorizontalPanel mainEffectContainer = new HorizontalPanel();
    protected HorizontalPanel interactionEffectContainer = new HorizontalPanel();
    protected HorizontalPanel scaleContainer = new HorizontalPanel();
    protected TextBox mainEffectTextBox = new TextBox();
    protected TextBox interactionEffectTextBox = new TextBox();
    protected String predictor = null;
    protected String interactionPredictor = null;
    
	public MeanDifferencesRepeatedMeasuresPanel()
	{
		skip = true;
		VerticalPanel panel = new VerticalPanel();
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.meanDifferenceScaleTitle());
        HTML description = new HTML(Glimmpse.constants.meanDifferenceScaleDescription());
        // create the main effect and interaction entry forms
        HTML mainEffectQuestion = 
        	new HTML(Glimmpse.constants.meanDifferenceMainEffectQuestion() + predictor);
        mainEffectContainer.add(mainEffectQuestion);
        HTML interactionEffectQuestion = 
        	new HTML(Glimmpse.constants.meanDifferenceInteractionEffectQuestion() + " <u>" + predictor + 
        			"</u>" + Glimmpse.constants.and() + "<u>" + interactionPredictor + "</u>");
        // create the beta scale checkbox - asks if the user wants to test 0.5,1,and 2 times the estimated
        // mean difference
        scaleContainer.add(new HTML(Glimmpse.constants.meanDifferenceScaleQuestion()));
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(mainEffectContainer);
        panel.add(interactionEffectContainer);
        panel.add(scaleContainer);
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		initWidget(panel);
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
		this.predictor = predictor;
		this.interactionPredictor = null;
		interactionEffectContainer.setVisible(false);
		mainEffectContainer.setVisible(true);
	}

	@Override
	public void onInteractionHypothesis(String predictor,
			String interactionPredictor)
	{
		this.predictor = predictor;
		this.interactionPredictor = interactionPredictor;
		interactionEffectContainer.setVisible(true);
		mainEffectContainer.setVisible(false);
	}

	@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		if (repeatedMeasures == null || repeatedMeasures.size() <= 0)
			skip = true;
		else
			skip = false;
	}

	public String toRequestXML()
	{
		return "";
	}
}
