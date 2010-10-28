package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.listener.RelativeGroupSizeListener;
import edu.cudenver.bios.glimmpse.client.listener.RepeatedMeasuresListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class HypothesisIndependentMeasuresPanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, RepeatedMeasuresListener,
RelativeGroupSizeListener, CovariateListener
{
	private static final String HYPOTHESIS_RADIO_GROUP = "hypothesisIndependentGroup";

	private class HypothesisRadioButton extends RadioButton
	{
		public int contrastDF;
		public String predictor;
		public String interactionPredictor;
		
		public HypothesisRadioButton(String group, String label,
				int contrastDF, String predictor, String interactionPredictor)
		{
			super(group, label);
			this.contrastDF = contrastDF;
			this.predictor = predictor;
			this.interactionPredictor = interactionPredictor;
		}
		
		public HypothesisRadioButton(String group, String label,
				int contrastDF, String predictor)
		{
			this(group, label, contrastDF, predictor, null);
		}
	}
	
	protected List<String> outcomes = null;
	protected HashMap<String, ArrayList<String>> predictorMap = null;
	protected DataTable groups = null;
	protected HashMap<String, Integer> groupColumnLookup = new HashMap<String,Integer>();
	
	protected List<Integer> relativeGroupSizes = null;
	
	// independent groups widgets
	protected FlexTable mainEffectsTable = new FlexTable();
	protected FlexTable interactionsTable = new FlexTable();
	
	// indicates if the study design has a covariate
	boolean hasCovariate = false;
	
    public HypothesisIndependentMeasuresPanel()
    {
    	super();
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.hypothesisTitle());
        HTML description = new HTML(Glimmpse.constants.hypothesisDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(mainEffectsTable);
        panel.add(interactionsTable);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        initWidget(panel);
    }
	
	@Override
	public void reset()
	{
    	interactionsTable.removeAllRows();
    	mainEffectsTable.removeAllRows();
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

	@Override
	public void onMean(double mean)
	{
		// no action needed for this event
	}

	@Override
	public void onVariance(double variance)
	{
		// no action needed for this event		
	}

	@Override
	public void onRelativeGroupSize(List<Integer> relativeSizes)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap, DataTable groups)
	{
		this.predictorMap = predictorMap;
		this.groups = groups;
		// build a lookup table of column id's (predictors) to column numbers
		for(int c = 0; c < groups.getNumberOfColumns(); c++)
		{
			this.groupColumnLookup.put(groups.getColumnLabel(c), c);
		}
	}

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		if (repeatedMeasures == null)
			skip = false;
		else
			skip = (repeatedMeasures.size() > 0);		
	}
	
    /**
     * Perform any setup when first entering this step in the wizard
     */
    public void onEnter() 
    {
		// TODO: check if repeated measures
		Object[] predictorArray = (Object[]) predictorMap.keySet().toArray();
		reset();
		int i = 0;
		for(Object predictor: predictorArray)
		{
			List<String> categories = predictorMap.get(predictor);
			addMainEffect((String) predictor, categories.size());
			addInteractions((String) predictor, ++i, predictorArray);
		}
    }
	
	
	private void addMainEffect(String predictor, int numCategories)
	{
		int startRow = mainEffectsTable.getRowCount();
		HypothesisRadioButton rb = 
			new HypothesisRadioButton(HYPOTHESIS_RADIO_GROUP, 
					"The outcomes will differ by " + predictor,
					numCategories - 1, predictor);

		mainEffectsTable.setWidget(startRow, 0, rb);
	}
	
	private void addInteractions(String predictor, int startIndex, Object[] predictorArray)
	{
		int startRow = interactionsTable.getRowCount();
		for(int i = startIndex; i < predictorArray.length; i++)
		{
			String interaction = (String) predictorArray[i];
			List<String> predictorCategories = predictorMap.get(predictor);
			List<String> interactionCategories = predictorMap.get(interaction);

			int df = predictorCategories.size() * (interactionCategories.size()-1);
			HypothesisRadioButton cb = new HypothesisRadioButton(HYPOTHESIS_RADIO_GROUP, 
					"The effect of " + predictor + 
					" on the outcomes will be different depending on the value of " + interaction,
					df, predictor, interaction);

			interactionsTable.setWidget(startRow, 0, cb);
			startRow++;
		}
	}
    
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();

		return buffer.toString();
	}

}
