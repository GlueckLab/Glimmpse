package edu.cudenver.bios.glimmpse.client.panels.guided;

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
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.VariabilityListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

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
