package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.HypothesisListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class MeanDifferencesIndependentMeasuresPanel extends WizardStepPanel
implements HypothesisListener, ChangeHandler, OutcomesListener
{
	private static final int COLUMN_LABEL = 0;
	private static final int COLUMN_TEXTBOX = 1;
    protected VerticalPanel mainEffectContainer = new VerticalPanel();
    protected HTML mainEffectHypothesisHTML = new HTML("");
    protected HTML mainEffectQuestionHTML = new HTML("");
    protected FlexTable mainEffectOutcomesTable = new FlexTable();
    protected HTML errorHTML = new HTML("");
    protected VerticalPanel interactionEffectContainer = new VerticalPanel();
    protected HTML interactionEffectPredictorHTML = new HTML("");
    protected HTML interactionEffectIntPredictorHTML = new HTML("");
    protected TextBox interactionEffectTextBox = new TextBox();
    
    protected VerticalPanel scaleContainer = new VerticalPanel();
    protected CheckBox scaleCheckBox = new CheckBox();
    protected String predictor = null;
    protected String interactionPredictor = null;
    
    private class OutcomeTextBox extends TextBox 
    {
    	public String outcome;
    	public OutcomeTextBox(String outcome, ChangeHandler handler)
    	{
    		super();
    		addChangeHandler(handler);
    	}
    }
    
	public MeanDifferencesIndependentMeasuresPanel()
	{
		VerticalPanel panel = new VerticalPanel();
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.meanDifferenceTitle());
        HTML description = new HTML(Glimmpse.constants.meanDifferenceDescription());
        // create the main effect entry form
        mainEffectContainer.add(mainEffectHypothesisHTML);
        mainEffectContainer.add(mainEffectQuestionHTML);
        mainEffectContainer.add(mainEffectOutcomesTable);
        
        // create the interaction effect entry form
        interactionEffectContainer.add(new HTML(Glimmpse.constants.meanDifferenceInteractionEffectQuestion()));
        interactionEffectContainer.add(interactionEffectPredictorHTML);
        interactionEffectContainer.add(new HTML(" " + Glimmpse.constants.and() + ""));
        interactionEffectContainer.add(interactionEffectIntPredictorHTML);
        interactionEffectContainer.add(interactionEffectTextBox);
        // create the beta scale checkbox - asks if the user wants to test 0.5,1,and 2 times the estimated
        // mean difference
        HorizontalPanel checkBoxContainer = new HorizontalPanel();
        checkBoxContainer.add(scaleCheckBox);
        checkBoxContainer.add(new HTML(Glimmpse.constants.meanDifferenceScaleAnswer()));
        scaleContainer.add(new HTML(Glimmpse.constants.meanDifferenceScaleQuestion()));
        scaleContainer.add(checkBoxContainer);
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(mainEffectContainer);
        panel.add(interactionEffectContainer);
        panel.add(errorHTML);
        panel.add(scaleContainer);
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        mainEffectOutcomesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        checkBoxContainer.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
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
	public void onMainEffectsHypothesis(String predictor)
	{
		this.predictor = predictor;
		this.interactionPredictor = null;
		interactionEffectContainer.setVisible(false);
		mainEffectContainer.setVisible(true);
		mainEffectHypothesisHTML.setHTML(Glimmpse.constants.meanDifferenceMainEffectHypothesis() + 
				" <u>" + predictor + "</u>");
		mainEffectQuestionHTML.setHTML(Glimmpse.constants.meanDifferenceMainEffectQuestion() + 
				" <u>" + predictor + "</u>?");
	}

	@Override
	public void onInteractionHypothesis(String predictor,
			String interactionPredictor)
	{
		this.predictor = predictor;
		this.interactionPredictor = interactionPredictor;
		interactionEffectContainer.setVisible(true);
		mainEffectContainer.setVisible(false);
		interactionEffectPredictorHTML.setHTML(predictor);
		interactionEffectPredictorHTML.setHTML(interactionPredictor);
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (interactionPredictor == null)
		{

		}
		else
		{
			
		}
		return buffer.toString();
	}

	@Override
	public void onChange(ChangeEvent event)
	{
		TextBox source = (TextBox) event.getSource();
		try
		{
			double value = TextValidation.parseDouble(source.getText(), Double.NEGATIVE_INFINITY, 
					Double.POSITIVE_INFINITY, false);
		}
		catch (NumberFormatException nfe)
		{
			TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidNumber());
			source.setText("");
		}
		checkComplete();
	}

	private void checkComplete()
	{
		if (interactionPredictor == null)
		{
			boolean noEmpty = true;
			for(int i = 0; i < mainEffectOutcomesTable.getRowCount(); i++)
			{
				String value = ((TextBox) mainEffectOutcomesTable.getWidget(i, COLUMN_TEXTBOX)).getText();
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
	}
	
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		mainEffectOutcomesTable.clear();
		int i = 0;
		for(String outcome: outcomes)
		{
			mainEffectOutcomesTable.setWidget(i, COLUMN_LABEL, new HTML(outcome));
			mainEffectOutcomesTable.setWidget(i, COLUMN_TEXTBOX, new OutcomeTextBox(outcome, this));
			i++;
		}
	}	

}
