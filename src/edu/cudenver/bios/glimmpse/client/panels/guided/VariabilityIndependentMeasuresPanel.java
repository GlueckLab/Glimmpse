package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.ListUtilities;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.VariabilityListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

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
		standardDeviationTable.clear();
		correlationTable.clear();
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
	
	@Override
	public void reset()
	{
		standardDeviationTable.clear();
		correlationTable.clear();
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

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
