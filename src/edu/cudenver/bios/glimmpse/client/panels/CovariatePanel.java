package edu.cudenver.bios.glimmpse.client.panels;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;

public class CovariatePanel extends Composite
{
    protected CheckBox covariateCheckBox = new CheckBox();
    protected TextBox meanTextBox = new TextBox();
    protected TextBox stddevTextBox = new TextBox();
    protected HTML errorHTML = new HTML();

    protected ArrayList<CovariateListener> listeners = new ArrayList<CovariateListener>();
    
    public CovariatePanel(String descriptionString)
    {
    	// build covariate panel
        VerticalPanel panel = new VerticalPanel();
        
        // create header, description HTML
        HTML header = new HTML(Glimmpse.constants.covariateTitle());
        HTML description = new HTML(descriptionString);
        
        // build the checkbox / label to contorl for a covariate
        HorizontalPanel includeCovariatePanel = new HorizontalPanel();
        includeCovariatePanel.add(covariateCheckBox);
        includeCovariatePanel.add(new HTML(Glimmpse.constants.covariateCheckBoxLabel()));
        
        // build the grid containing the mean, stddev inputs
        // subpanel for mean / stddev
        Grid meanVarPanel = new Grid(2,2);
        meanVarPanel.setWidget(0, 0, new HTML(Glimmpse.constants.covariateMeanLabel()));
        meanVarPanel.setWidget(0, 1, meanTextBox);
        meanVarPanel.setWidget(1, 0, new HTML(Glimmpse.constants.covariateStandardDeviationLabel()));
        meanVarPanel.setWidget(1, 1, stddevTextBox);
        
        // add handlers 
        covariateCheckBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e)
            {
            	meanTextBox.setEnabled(covariateCheckBox.getValue());
            	stddevTextBox.setEnabled(covariateCheckBox.getValue());
                meanTextBox.setText("");
                stddevTextBox.setText("");
                // notify listeners
                for(CovariateListener listener : listeners) listener.onHasCovariate(covariateCheckBox.getValue());
            }
        });
        // listeners on the mean / stddev
        meanTextBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                try
                {
                    double mean = Double.parseDouble(meanTextBox.getText());
                    for(CovariateListener listener : listeners) listener.onMean(mean);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (NumberFormatException nfe)
                {
                    TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidMean());
                    meanTextBox.setText("");
                }
            }
        });
        stddevTextBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                try
                {
                    double stddev = TextValidation.parseDouble(stddevTextBox.getText(), 0, true);
                    for(CovariateListener listener : listeners) listener.onVariance(stddev * stddev);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (NumberFormatException nfe)
                {
                    TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidStandardDeviation());
                    stddevTextBox.setText("");
                }
            }
        });       
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(includeCovariatePanel);
        panel.add(meanVarPanel);
        panel.add(errorHTML);
        
        // add style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        errorHTML.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
  
        initWidget(panel);
    }
    
    public void addCovariateListener(CovariateListener listener)
    {
        listeners.add(listener);
    }
    
    public String getMean()
    {
        return meanTextBox.getText();
    }
    
    public String getVariance()
    {
        return stddevTextBox.getText();
    }
    
    public boolean hasCovariate()
    {
        return covariateCheckBox.getValue();
    }
    
    public boolean isComplete()
    {
    	if (covariateCheckBox.getValue())
    	{
    		return (!meanTextBox.getValue().isEmpty() && !stddevTextBox.getValue().isEmpty());
    	}
    	else
    	{
    		return true;
    	}
    }
}
