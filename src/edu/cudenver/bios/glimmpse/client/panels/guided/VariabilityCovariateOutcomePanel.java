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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.VariabilityListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Guided model panel for entry of correlations between the covariate and the outcome
 * @author Sarah Kreidler
 *
 */
public class VariabilityCovariateOutcomePanel extends WizardStepPanel
implements CovariateListener, VariabilityListener, OutcomesListener, ChangeHandler
{
	private static final int COLUMN_LABEL = 0;
	private static final int COLUMN_TEXTBOX = 1;
	protected FlexTable correlationTable = new FlexTable();
	protected HTML errorHTML = new HTML();

	protected List<Double> variancesOfOutcomes = null;
	protected double varianceOfCovariate = 1;
	
	public VariabilityCovariateOutcomePanel()
	{
		super();
		skip = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.variabilityCovariateOutcomeTitle());
        HTML description = new HTML(Glimmpse.constants.variabilityCovariateOutcomeDescription());
        
        // build the panel
        panel.add(header);
        panel.add(description);
        panel.add(new HTML(Glimmpse.constants.variabilityCovariateOutcomeQuestion()));
        panel.add(correlationTable);
        panel.add(errorHTML);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        errorHTML.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        
		initWidget(panel);
	}
	
	@Override
	public void reset()
	{
		correlationTable.removeAllRows();
		skip = true;
	}

	@Override
	public void loadFromNode(Node node)
	{
		if (GlimmpseConstants.TAG_VARIABILITY_YG.equalsIgnoreCase(node.getNodeName()))
		{
			NodeList childList = node.getChildNodes();
			for(int i = 0; i < childList.getLength(); i++)
			{
				Node child = childList.item(i);
				if (GlimmpseConstants.TAG_CORRELATION.equalsIgnoreCase(child.getNodeName()))
				{
					NamedNodeMap attrs = child.getAttributes();
					Node labelNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
					Node valueNode = attrs.getNamedItem(GlimmpseConstants.ATTR_VALUE);
					if (labelNode != null && valueNode != null)
					{
						correlationTable.setWidget(i, COLUMN_LABEL, new HTML(labelNode.getNodeValue()));
						TextBox tb = new TextBox();
						tb.setText(valueNode.getNodeValue());
						correlationTable.setWidget(i, COLUMN_TEXTBOX, tb);
						tb.addChangeHandler(new ChangeHandler() {
							@Override
							public void onChange(ChangeEvent event)
							{
								TextBox tb = (TextBox) event.getSource();
								try
								{
									TextValidation.parseDouble(tb.getText(), 0, 1, false);
									TextValidation.displayOkay(errorHTML, "");
								}
								catch (NumberFormatException e)
								{
									TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidPositiveNumber()); 
									tb.setText("");
								}
								checkComplete();
							}
						});
					}
				}
			}
		}
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = !hasCovariate;
	}

	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip && complete)
		{
			XMLUtilities.matrixOpenTag(buffer, 
					GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE, 
					correlationTable.getRowCount(), 1);
			for(int row = 0; row < correlationTable.getRowCount(); row++)
			{
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
				
				double correlation = 
					Double.parseDouble(((TextBox) correlationTable.getWidget(row, COLUMN_TEXTBOX)).getText());
				buffer.append((correlation * Math.sqrt(varianceOfCovariate * variancesOfOutcomes.get(row))));
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		return buffer.toString();
	}

	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip)
		{
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_VARIABILITY_YG);
			for(int row = 0; row < correlationTable.getRowCount(); row++)
			{
				StringBuffer attrBuffer = new StringBuffer();
				attrBuffer.append(GlimmpseConstants.ATTR_NAME);
				attrBuffer.append("='");
				attrBuffer.append(((HTML) correlationTable.getWidget(row, COLUMN_LABEL)).getText());
				attrBuffer.append("' ");
				attrBuffer.append(GlimmpseConstants.ATTR_VALUE);
				attrBuffer.append("='");
				attrBuffer.append(((TextBox) correlationTable.getWidget(row, COLUMN_TEXTBOX)).getText());
				attrBuffer.append("' ");
				
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_CORRELATION, attrBuffer.toString());
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_CORRELATION);
			}
		}
		
		return buffer.toString();
	}
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		int i = 0;
		for(String outcome: outcomes)
		{
			correlationTable.setWidget(i, COLUMN_LABEL, new HTML(outcome + " " + Glimmpse.constants.and() + " covariate"));
			TextBox tb = new TextBox();
			correlationTable.setWidget(i, COLUMN_TEXTBOX, tb);
			tb.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event)
				{
					TextBox tb = (TextBox) event.getSource();
					try
					{
						TextValidation.parseDouble(tb.getText(), 0, 1, false);
						TextValidation.displayOkay(errorHTML, "");
					}
					catch (NumberFormatException e)
					{
						TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidPositiveNumber()); 
						tb.setText("");
					}
					checkComplete();
				}
			});
			i++;
		}
	}

	@Override
	public void onChange(ChangeEvent event)
	{
		TextBox source = (TextBox) event.getSource();
		try
		{
			TextValidation.parseDouble(source.getText(), 0, true);
			TextValidation.displayOkay(errorHTML, "");
		}
		catch (NumberFormatException nfe)
		{
			TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidPositiveNumber());
			source.setText("");
		}
		checkComplete();
	}
	
	private void checkComplete()
	{
		boolean noEmpty = true;
		for(int row = 0; row < correlationTable.getRowCount(); row++)
		{
			String value = ((TextBox) correlationTable.getWidget(row, COLUMN_TEXTBOX)).getText();
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
	public void onOutcomeVariance(List<Double> variancesOfOutcomes)
	{
		this.variancesOfOutcomes = variancesOfOutcomes;
	}

	@Override
	public void onCovariateVariance(double varianceOfCovariate)
	{
		this.varianceOfCovariate = varianceOfCovariate;
	}

}
