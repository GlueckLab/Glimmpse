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
package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class OptionsConfidenceIntervalsPanel extends WizardStepPanel
implements CovariateListener
{
	protected String ciTypeRadioGroup= "confidenceIntervalTypeGroup";
	
	protected CheckBox noCICheckbox = new CheckBox();
	
	protected RadioButton sigmaCIRadioButton;
	protected RadioButton betaSigmaCIRadioButton;
	
	protected TextBox alphaLowerTextBox = new TextBox();
	protected TextBox alphaUpperTextBox = new TextBox();
	protected TextBox sampleSizeTextBox = new TextBox();
	protected TextBox rankTextBox = new TextBox();
	
	protected HTML alphaErrorHTML = new HTML();
	protected HTML estimatesErrorHTML = new HTML();
	
	public OptionsConfidenceIntervalsPanel(String mode)
	{
		super();
	
		ciTypeRadioGroup += mode;
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(Glimmpse.constants.confidenceIntervalOptionsTitle());
		HTML description = new HTML(Glimmpse.constants.confidenceIntervalOptionsDescription());        
		
		panel.add(header);
		panel.add(description);
		
		panel.add(createDisablePanel());
		panel.add(createTypePanel());
		panel.add(createTailProbabilityPanel());
		panel.add(createEstimatesPanel());
		
		// add style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		reset();
		initWidget(panel);
	}
	
	private HorizontalPanel createDisablePanel()
	{
		HorizontalPanel panel = new HorizontalPanel();
		
		// add callbacks
		noCICheckbox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				CheckBox cb = (CheckBox) event.getSource();
				enableConfidenceIntervalOptions(!cb.getValue());
				checkComplete();
			}
		});
		
		panel.add(noCICheckbox);
		panel.add(new HTML(Glimmpse.constants.confidenceIntervalOptionsNone()));
		
		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
	
	private VerticalPanel createTypePanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		// create the radio buttons
		sigmaCIRadioButton = new RadioButton(ciTypeRadioGroup, 
				Glimmpse.constants.confidenceIntervalOptionsTypeSigma(), true);
		sigmaCIRadioButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				checkComplete();
			}	
		});
		betaSigmaCIRadioButton = new RadioButton(ciTypeRadioGroup, 
				Glimmpse.constants.confidenceIntervalOptionsTypeBetaSigma(), true);
		betaSigmaCIRadioButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				checkComplete();
			}	
		});
		// layout the panel
		panel.add(new HTML(Glimmpse.constants.confidenceIntervalOptionsTypeQuestion()));
		panel.add(sigmaCIRadioButton);
		panel.add(betaSigmaCIRadioButton);
		
		// set style
		sigmaCIRadioButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		betaSigmaCIRadioButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		return panel;
	}
	
	
	private VerticalPanel createTailProbabilityPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML(Glimmpse.constants.confidenceIntervalOptionsAlphaLower()));
		grid.setWidget(0, 1, alphaLowerTextBox);
		grid.setWidget(1, 0, new HTML(Glimmpse.constants.confidenceIntervalOptionsAlphaUpper()));
		grid.setWidget(1, 1, alphaUpperTextBox);
		
		// layout the panel
		panel.add(new HTML(Glimmpse.constants.confidenceIntervalOptionsAlphaQuestion()));
		panel.add(grid);
		panel.add(alphaErrorHTML);
		
		// callbacks for error checking
		alphaLowerTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					TextValidation.parseDouble(alphaLowerTextBox.getText(), 0, 0.5, false);
					TextValidation.displayOkay(alphaErrorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(alphaErrorHTML, Glimmpse.constants.errorInvalidTailProbability());
					alphaLowerTextBox.setText("");
				}
				checkComplete();
			}
		});
		alphaUpperTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					TextValidation.parseDouble(alphaUpperTextBox.getText(), 0, 0.5, false);
					TextValidation.displayOkay(alphaErrorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(alphaErrorHTML, Glimmpse.constants.errorInvalidTailProbability());
					alphaUpperTextBox.setText("");
				}
				checkComplete();
			}
		});
		
		// add style
		grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		alphaErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
	
	private VerticalPanel createEstimatesPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML(Glimmpse.constants.confidenceIntervalOptionsSampleSize()));
		grid.setWidget(0, 1, sampleSizeTextBox);
		grid.setWidget(1, 0, new HTML(Glimmpse.constants.confidenceIntervalOptionsRank()));
		grid.setWidget(1, 1, rankTextBox);
		
		// call backs for error checking
		sampleSizeTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					// first make sure it's an integer
					int sampleSize = TextValidation.parseInteger(sampleSizeTextBox.getText(),
							0, true);
					if (!rankTextBox.getText().isEmpty())
					{
						int rank = Integer.parseInt(rankTextBox.getText());
						if (sampleSize > rank)
						{
							TextValidation.displayOkay(estimatesErrorHTML, "");
						}
						else
						{
							TextValidation.displayError(estimatesErrorHTML, Glimmpse.constants.errorSampleSizeLessThanRank());
							sampleSizeTextBox.setText("");
						}
					}
					else
					{
						TextValidation.displayOkay(estimatesErrorHTML, "");
					}
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(estimatesErrorHTML, Glimmpse.constants.errorInvalidSampleSize());
					sampleSizeTextBox.setText("");
				}
				checkComplete();
			}
		});
		rankTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					// first make sure it's an integer
					int rank = TextValidation.parseInteger(rankTextBox.getText(),
							0, true);
					if (!sampleSizeTextBox.getText().isEmpty())
					{
						int sampleSize = Integer.parseInt(sampleSizeTextBox.getText());
						if (sampleSize > rank)
						{
							TextValidation.displayOkay(estimatesErrorHTML, "");
						}
						else
						{
							TextValidation.displayError(estimatesErrorHTML, Glimmpse.constants.errorSampleSizeLessThanRank());
							rankTextBox.setText("");
						}
					}
					else
					{
						TextValidation.displayOkay(estimatesErrorHTML, "");
					}
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(estimatesErrorHTML, Glimmpse.constants.errorInvalidPositiveNumber());
					rankTextBox.setText("");
				}
				checkComplete();
			}
		});
		// layout the panel
		panel.add(new HTML(Glimmpse.constants.confidenceIntervalOptionsEstimatedDataQuestion()));
		panel.add(grid);
		panel.add(estimatesErrorHTML);
		
		// add style
		grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		estimatesErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
	
	private void enableConfidenceIntervalOptions(boolean enabled)
	{
		sigmaCIRadioButton.setEnabled(enabled);
		betaSigmaCIRadioButton.setEnabled(enabled);
		alphaLowerTextBox.setEnabled(enabled);
		alphaUpperTextBox.setEnabled(enabled);
		sampleSizeTextBox.setEnabled(enabled);
		rankTextBox.setEnabled(enabled);
		TextValidation.displayOkay(alphaErrorHTML, "");
		TextValidation.displayOkay(estimatesErrorHTML, "");
	}
	
	@Override
	public void reset()
	{
		noCICheckbox.setValue(true);
		enableConfidenceIntervalOptions(false);
		alphaLowerTextBox.setText("");
		alphaUpperTextBox.setText("");
		sampleSizeTextBox.setText("");
		rankTextBox.setText("");
		TextValidation.displayOkay(alphaErrorHTML, "");
		TextValidation.displayOkay(estimatesErrorHTML, "");
		checkComplete();
	}

	@Override
	public void loadFromNode(Node node)
	{
		
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (complete && !noCICheckbox.getValue())
		{
			StringBuffer attrs = new StringBuffer();
			attrs.append(GlimmpseConstants.ATTR_TYPE);
			attrs.append("='");
			if (sigmaCIRadioButton.getValue())
				attrs.append(GlimmpseConstants.CONFIDENCE_INTERVAL_BETA_KNOWN_EST_SIGMA);
			else if (betaSigmaCIRadioButton.getValue())
				attrs.append(GlimmpseConstants.CONFIDENCE_INTERVAL_EST_BETA_SIGMA);
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ALPHA_LOWER);
			attrs.append("='");
			attrs.append(alphaLowerTextBox.getText());
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ALPHA_UPPER);
			attrs.append("='");
			attrs.append(alphaUpperTextBox.getText());
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ESTIMATES_SAMPLE_SIZE);
			attrs.append("='");
			attrs.append(sampleSizeTextBox.getText());
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ESTIMATES_RANK);
			attrs.append("='");
			attrs.append(rankTextBox.getText());
			attrs.append("' ");
						
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_CONFIDENCE_INTERVAL,
					attrs.toString());
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_CONFIDENCE_INTERVAL);
		}
		return buffer.toString();
	}
	
	public String toStudyXML()
	{
		if (noCICheckbox.getValue())
		{
			StringBuffer buffer = new StringBuffer();
						
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_CONFIDENCE_INTERVAL,
					"type='none'");
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_CONFIDENCE_INTERVAL);
			
			return buffer.toString();
		}
		else
		{
			return toRequestXML();
		}
	}
	
	private void checkComplete()
	{
		if (noCICheckbox.getValue() || 
				((sigmaCIRadioButton.getValue() || betaSigmaCIRadioButton.getValue()) 
						&& (!alphaLowerTextBox.getText().isEmpty() && 
								!alphaUpperTextBox.getText().isEmpty() &&
								!sampleSizeTextBox.getText().isEmpty() &&
								!rankTextBox.getText().isEmpty())))
		{
			notifyComplete();
		}
		else
		{
			notifyInProgress();
		}
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = hasCovariate;
		noCICheckbox.setValue(hasCovariate);
		enableConfidenceIntervalOptions(!hasCovariate);
	}

}
