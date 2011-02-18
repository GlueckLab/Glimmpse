package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.BetaScaleListener;
import edu.cudenver.bios.glimmpse.client.listener.SigmaScaleListener;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class SigmaScalePanel extends WizardStepPanel
implements ListValidator
{
    // list of sigma scale factors
    protected ListEntryPanel sigmaScaleListPanel = 
    	new ListEntryPanel(Glimmpse.constants.sigmaScaleTableColumn(), this);
    
    protected ArrayList<SigmaScaleListener> listeners = new ArrayList<SigmaScaleListener>();
    
	public SigmaScalePanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(Glimmpse.constants.sigmaScaleTitle());
        HTML description = new HTML(Glimmpse.constants.sigmaScaleDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(sigmaScaleListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		sigmaScaleListPanel.reset();
	}

	@Override
	public void loadFromNode(Node node)
	{
		sigmaScaleListPanel.loadFromNode(node);
		onValidRowCount(sigmaScaleListPanel.getValidRowCount());
	}

	@Override
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseDouble(value, 0.0, Double.POSITIVE_INFINITY, false);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidNumber());
    	}
	}
	
	@Override
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
	}

	@Override
	public void onExit()
	{
    	List<String> values = sigmaScaleListPanel.getValues();
    	for(SigmaScaleListener listener: listeners) listener.onSigmaScaleList(values);
	}
	
    /**
     * Add a listener for the list of sigma scale values
     * @param listener sigma scale listener object
     */
    public void addSigmaScaleListener(SigmaScaleListener listener)
    {
    	listeners.add(listener);
    }
    
	public String toXML()
	{
		return sigmaScaleListPanel.toXML(GlimmpseConstants.TAG_SIGMA_SCALE_LIST);
	}
    
}
