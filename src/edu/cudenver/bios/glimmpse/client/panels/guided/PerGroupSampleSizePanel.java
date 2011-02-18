package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.AlphaListener;
import edu.cudenver.bios.glimmpse.client.listener.PerGroupSampleSizeListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener.SolutionType;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class PerGroupSampleSizePanel extends WizardStepPanel
implements ListValidator, SolvingForListener
{
   	// list of per group sample sizes
    protected ListEntryPanel perGroupNListPanel =
    	new ListEntryPanel(Glimmpse.constants.perGroupSampleSizeTableColumn(), this);
    
    protected ArrayList<PerGroupSampleSizeListener> listeners = 
    	new ArrayList<PerGroupSampleSizeListener>();
    
	public PerGroupSampleSizePanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(Glimmpse.constants.perGroupSampleSizeTitle());
        HTML description = new HTML(Glimmpse.constants.perGroupSampleSizeDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(perGroupNListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
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
	public void validate(String value)
			throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseInteger(value, 0, true);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidSampleSize());
    	}
	}
	
	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		skip = (solutionType == SolutionType.TOTAL_N);
	}
	
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		perGroupNListPanel.reset();
	}

	@Override
	public void loadFromNode(Node node)
	{
		perGroupNListPanel.loadFromNode(node);
		onValidRowCount(perGroupNListPanel.getValidRowCount());
	}
	
	public String toXML()
	{
		return perGroupNListPanel.toXML(GlimmpseConstants.TAG_SAMPLE_SIZE_LIST);
	}

    /**
     * Add a listener for the list of per group sample size values
     * @param listener per group sample size listener object
     */
    public void addPerGroupSampleSizeListener(PerGroupSampleSizeListener listener)
    {
    	listeners.add(listener);
    }
	
    /**
     * Notify sample size listeners as we exit the screen
     */
    @Override
    public void onExit()
    {
    	List<String> values = perGroupNListPanel.getValues();
    	for(PerGroupSampleSizeListener listener: listeners) listener.onPerGroupSampleSizeList(values);
    }
	
}
