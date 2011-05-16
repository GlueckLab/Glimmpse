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
import edu.cudenver.bios.glimmpse.client.ListUtilities;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.VariabilityListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

/**
 * Guided mode panel for entering s.d. and correlations for outcomes
 */
public class VariabilityIndependentMeasuresPanel extends WizardStepPanel
implements OutcomesListener, CovariateListener
{
	protected static final int COLUMN_LABEL = 0;
	protected static final int COLUMN_TEXTBOX = 1;
	protected VerticalPanel standardDeviationContainer = new VerticalPanel();
	protected FlexTable standardDeviationTable = new FlexTable();
	protected HTML standardDeviationErrorHTML = new HTML();
	protected VerticalPanel correlationContainer = new VerticalPanel();
	protected FlexTable correlationTable = new FlexTable();
	protected HTML correlationErrorHTML = new HTML();
	protected boolean hasCovariate = false;
	protected ArrayList<VariabilityListener> listeners = new ArrayList<VariabilityListener>();
	
	public VariabilityIndependentMeasuresPanel()
	{
		VerticalPanel panel = new VerticalPanel();
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.variabilityOutcomeTitle());
        HTML description = new HTML(Glimmpse.constants.variabilityOutcomeDescription());
        // create the standard deviation entry form
        // TODO: constants!!
        standardDeviationContainer.add(new HTML(Glimmpse.constants.variabilityOutcomeQuestion()));
        standardDeviationContainer.add(standardDeviationTable);
        standardDeviationContainer.add(standardDeviationErrorHTML);
        // create the correlation entry form
        correlationContainer.add(new HTML(Glimmpse.constants.correlationOutcomeQuestion()));
        correlationContainer.add(correlationTable);
        correlationContainer.add(correlationErrorHTML);
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(standardDeviationContainer);
        panel.add(correlationContainer);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        standardDeviationTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        standardDeviationErrorHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        correlationTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        correlationErrorHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        initWidget(panel);
	}

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		standardDeviationTable.removeAllRows();
		correlationTable.removeAllRows();
		int i = 0;
		for(String outcome: outcomes)
		{
			standardDeviationTable.setWidget(i, COLUMN_LABEL, new HTML(outcome));
			TextBox tb = new TextBox();
			standardDeviationTable.setWidget(i, COLUMN_TEXTBOX, tb);
			tb.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event)
				{
					TextBox tb = (TextBox) event.getSource();
					try
					{
						TextValidation.parseDouble(tb.getText(), 0, true);
						TextValidation.displayOkay(standardDeviationErrorHTML, "");
					}
					catch (NumberFormatException e)
					{
						TextValidation.displayError(standardDeviationErrorHTML, Glimmpse.constants.errorInvalidPositiveNumber()); 
						tb.setText("");
					}
					checkComplete();
				}
			});
			i++;
		}
		if (outcomes.size() > 1)
		{
			List<String[]> pairs = ListUtilities.getPairs(outcomes);
			i = 0;
			for(String[] pair: pairs)
			{
				correlationTable.setWidget(i, COLUMN_LABEL, new HTML(pair[0] + " " + 
						Glimmpse.constants.and() + " " + pair[1]));
				TextBox tb = new TextBox();
				tb.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event)
					{
						TextBox tb = (TextBox) event.getSource();
						try
						{
							TextValidation.parseDouble(tb.getText(), 0, 1, true);
							TextValidation.displayOkay(correlationErrorHTML, "");
						}
						catch (NumberFormatException e)
						{
							TextValidation.displayError(correlationErrorHTML, Glimmpse.constants.errorInvalidAlpha()); // TODO
							tb.setText("");
						}
						checkComplete();
					}
				});
				correlationTable.setWidget(i, COLUMN_TEXTBOX, tb);
				i++;
			}
			correlationContainer.setVisible(true);
		}
		else
		{
			correlationContainer.setVisible(false);
		}
	}
	
	private void checkComplete()
	{
		boolean noEmpty = true;
		for(int i = 0; i < standardDeviationTable.getRowCount(); i++)
		{
			String value = ((TextBox) standardDeviationTable.getWidget(i, COLUMN_TEXTBOX)).getText();
			if (value == null || value.isEmpty())
			{
				noEmpty = false;
				break;
			}		
		}
		if (noEmpty)
		{
			for(int i = 0; i < correlationTable.getRowCount(); i++)
			{
				String value = ((TextBox) correlationTable.getWidget(i, COLUMN_TEXTBOX)).getText();
				if (value == null || value.isEmpty())
				{
					noEmpty = false;
					break;
				}		
			}
		}
		if (noEmpty)
			notifyComplete();
		else
			notifyInProgress();
	}
	
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (complete)
		{
			// calculate the variances from the entered standard deviations
			int numOutcomes = standardDeviationTable.getRowCount();
			Double[] variances = new Double[numOutcomes];
			for(int i = 0; i < numOutcomes; i++)
			{
				double value =  Double.parseDouble(((TextBox) standardDeviationTable.getWidget(i,COLUMN_TEXTBOX)).getText());
				variances[i] = value*value;
			}

			if (!hasCovariate)
			{
				XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_SIGMA_ERROR, 
						numOutcomes, numOutcomes);
			}
			else
			{
				XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_SIGMA_OUTCOME, 
						numOutcomes, numOutcomes);
			}
			for(int row = 0; row < numOutcomes; row++)
			{
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
				for(int col = 0; col < numOutcomes; col++)
				{
					XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
					if (row == col)
					{
						buffer.append(variances[row]);
					}
					else
					{
						// convert the correlation to a covariance and append to the buffer
						double correlation = 
							Double.parseDouble(((TextBox) correlationTable.getWidget(row+col-1,COLUMN_TEXTBOX)).getText());
						double covariance = correlation * Math.sqrt(variances[row]*variances[col]);
						buffer.append(covariance);
					}
					XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
				}
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
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_VARIABILITY_Y);
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_SD_LIST);
			for(int i = 0; i < standardDeviationTable.getRowCount(); i++)
			{
				StringBuffer attrBuffer = new StringBuffer();
				attrBuffer.append(GlimmpseConstants.ATTR_NAME);
				attrBuffer.append("='");
				attrBuffer.append(((HTML) standardDeviationTable.getWidget(i, COLUMN_LABEL)).getText());
				attrBuffer.append("' ");
				attrBuffer.append(GlimmpseConstants.ATTR_VALUE);
				attrBuffer.append("='");
				attrBuffer.append(((TextBox) standardDeviationTable.getWidget(i, COLUMN_TEXTBOX)).getText());
				attrBuffer.append("' ");
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_SD, attrBuffer.toString());
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_SD);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_SD_LIST);
			
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_CORRELATION_LIST);
			for(int i = 0; i < correlationTable.getRowCount(); i++)
			{
				StringBuffer attrBuffer = new StringBuffer();
				attrBuffer.append(GlimmpseConstants.ATTR_NAME);
				attrBuffer.append("='");
				attrBuffer.append(((HTML) correlationTable.getWidget(i, COLUMN_LABEL)).getText());
				attrBuffer.append("' ");
				attrBuffer.append(GlimmpseConstants.ATTR_VALUE);
				attrBuffer.append("='");
				attrBuffer.append(((TextBox) correlationTable.getWidget(i, COLUMN_TEXTBOX)).getText());
				attrBuffer.append("' ");
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_CORRELATION, attrBuffer.toString());
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_CORRELATION);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_CORRELATION_LIST);
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_VARIABILITY_Y);
		}
		return buffer.toString();
	}
	@Override
	public void reset()
	{
		standardDeviationTable.removeAllRows();
		correlationTable.removeAllRows();
	}

	@Override
	public void loadFromNode(Node node)
	{
		if (GlimmpseConstants.TAG_VARIABILITY_Y.equalsIgnoreCase(node.getNodeName()))
		{
			NodeList childList = node.getChildNodes();
			for(int i = 0; i < childList.getLength(); i++)
			{
				Node child = childList.item(i);
				if (GlimmpseConstants.TAG_CORRELATION_LIST.equalsIgnoreCase(child.getNodeName()))
				{
					parseCorrelationList(child);
				}
				else if (GlimmpseConstants.TAG_SD_LIST.equalsIgnoreCase(child.getNodeName()))
				{
					parseStandardDeviationList(child);
				}

			}
		}
	}
	
	private void parseStandardDeviationList(Node node)
	{
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); i++)
		{
			Node child = childList.item(i);
			NamedNodeMap attrs = child.getAttributes();
			Node labelNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
			Node valueNode = attrs.getNamedItem(GlimmpseConstants.ATTR_VALUE);
			if (labelNode != null && valueNode != null)
			{
				standardDeviationTable.setWidget(i, COLUMN_LABEL, new HTML(labelNode.getNodeValue()));
				TextBox tb = new TextBox();
				tb.setText(valueNode.getNodeValue());
				standardDeviationTable.setWidget(i, COLUMN_TEXTBOX, tb);
				tb.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event)
					{
						TextBox tb = (TextBox) event.getSource();
						try
						{
							TextValidation.parseDouble(tb.getText(), 0, true);
							TextValidation.displayOkay(standardDeviationErrorHTML, "");
						}
						catch (NumberFormatException e)
						{
							TextValidation.displayError(standardDeviationErrorHTML, Glimmpse.constants.errorInvalidPositiveNumber()); 
							tb.setText("");
						}
						checkComplete();
					}
				});
			}
		}
	}
	
	private void parseCorrelationList(Node node)
	{
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); i++)
		{
			Node child = childList.item(i);
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
							TextValidation.displayOkay(correlationErrorHTML, "");
						}
						catch (NumberFormatException e)
						{
							TextValidation.displayError(correlationErrorHTML, Glimmpse.constants.errorInvalidPositiveNumber()); 
							tb.setText("");
						}
						checkComplete();
					}
				});
			}
		}
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}

	public void addVariabilityListener(VariabilityListener listener)
	{
		listeners.add(listener);
	}
	
	public void onExit()
	{
		if (complete)
		{
			ArrayList<Double> variances = new ArrayList<Double>();
			// build list of outcome variances
			for(int row = 0; row < standardDeviationTable.getRowCount(); row++)
			{
				double stddev = 
					Double.parseDouble(((TextBox) standardDeviationTable.getWidget(row,COLUMN_TEXTBOX)).getText());
				variances.add(stddev * stddev);
			}
			// notify listeners
			for(VariabilityListener listener: listeners) listener.onOutcomeVariance(variances);
		}
	}
}
