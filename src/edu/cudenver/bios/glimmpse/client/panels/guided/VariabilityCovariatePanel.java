package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

public class VariabilityCovariatePanel extends WizardStepPanel
implements CovariateListener, ChangeHandler
{
	protected TextBox standardDeviationTextBox = new TextBox();
	protected HTML errorHTML = new HTML();
	protected ArrayList<VariabilityListener> listeners = new ArrayList<VariabilityListener>();
	
	public VariabilityCovariatePanel()
	{
		super();
		skip = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.variabilityCovariateTitle());
        HTML description = new HTML(Glimmpse.constants.variabilityCovariateDescription());
		
        HorizontalPanel standardDeviationContainer = new HorizontalPanel();
        standardDeviationContainer.add(new HTML("What is the estimated standard deviation for your covariate?"));
        standardDeviationContainer.add(standardDeviationTextBox);
        
        // callback for text box
        standardDeviationTextBox.addChangeHandler(this);
        
        // build the panel
        panel.add(header);
        panel.add(description);
        panel.add(standardDeviationContainer);
        panel.add(errorHTML);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        standardDeviationContainer.setStyleName(GlimmpseConstants.STYLE_WIZARD_CONTENT);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        errorHTML.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        
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
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = !hasCovariate;
	}

	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip && complete)
		{
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_SIGMA_COVARIATE, 1, 1);
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
			double stddev = Double.parseDouble(standardDeviationTextBox.getText());
			buffer.append(stddev * stddev);
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		return buffer.toString();
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
		String value = standardDeviationTextBox.getText();
		if (value == null || value.isEmpty())
			notifyInProgress();
		else
			notifyComplete();
	}
	
	public void addVariabilityListener(VariabilityListener listener)
	{
		listeners.add(listener);
	}
	
	public void onExit()
	{
		if (complete)
		{
			double stddev = Double.parseDouble(standardDeviationTextBox.getText());
			double variance = stddev * stddev;
			// notify listeners
			for(VariabilityListener listener: listeners) listener.onCovariateVariance(variance);
		}
	}
}
