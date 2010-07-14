package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;

public class CovariatePanel extends com.google.gwt.user.client.ui.Composite
{
    protected CheckBox covariateCheckBox = new CheckBox();
    protected Grid meanVarPanel = new Grid(2,3);
    protected TextBox meanTextBox = new TextBox();
    protected TextBox varianceTextBox = new TextBox();
    protected HTML meanErrorHTML = new HTML();
    protected HTML varianceErrorHTML = new HTML();
    protected ArrayList<CovariateListener> listeners = new ArrayList<CovariateListener>();
    
    public CovariatePanel()
    {
     // build covariate panel
        VerticalPanel covariatePanel = new VerticalPanel();
        HorizontalPanel includeCovariatePanel = new HorizontalPanel();
        includeCovariatePanel.add(covariateCheckBox);
        includeCovariatePanel.add(new HTML("Include a baseline covariate"));
        covariateCheckBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e)
            {
                meanVarPanel.setVisible(covariateCheckBox.getValue());
                meanTextBox.setText("");
                varianceTextBox.setText("");
                // notify listeners
                for(CovariateListener listener : listeners) listener.onHasCovariate(covariateCheckBox.getValue());
            }
        });
        covariatePanel.add(includeCovariatePanel);
        
        // listeners on the mean / variance
        meanTextBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                try
                {
                    double mean = Double.parseDouble(meanTextBox.getText());
                    for(CovariateListener listener : listeners) listener.onMean(mean);
                    TextValidation.displayOkay(meanErrorHTML, "");
                }
                catch (NumberFormatException nfe)
                {
                    TextValidation.displayError(meanErrorHTML, Glimmpse.constants.errorInvalidMean());
                    meanTextBox.setText("");
                }
            }
        });
        varianceTextBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e)
            {
                try
                {
                    double mean = TextValidation.parseDouble(varianceTextBox.getText(), 0, true);
                    for(CovariateListener listener : listeners) listener.onMean(mean);
                    TextValidation.displayOkay(varianceErrorHTML, "");
                }
                catch (NumberFormatException nfe)
                {
                    TextValidation.displayError(varianceErrorHTML, Glimmpse.constants.errorInvalidVariance());
                    varianceTextBox.setText("");
                }
            }
        });
        
        // subpanel for mean / variance
        meanVarPanel.setWidget(0, 0, new HTML("Mean: "));
        meanVarPanel.setWidget(0, 1, meanTextBox);
        meanVarPanel.setWidget(0, 2, meanErrorHTML);
        meanVarPanel.setWidget(1, 0, new HTML("Variance: "));
        meanVarPanel.setWidget(1, 1, varianceTextBox);
        meanVarPanel.setWidget(1, 2, varianceErrorHTML);
        meanVarPanel.setVisible(false);
        covariatePanel.add(meanVarPanel);
        
        // add style
        meanErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        meanErrorHTML.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        varianceErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        varianceErrorHTML.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
  
        initWidget(covariatePanel);
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
        return varianceTextBox.getText();
    }
    
    public boolean hasCovariate()
    {
        return covariateCheckBox.getValue();
    }
}
