package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.ListUtilities;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.HypothesisListener;
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
	protected ArrayList<HypothesisListener> listeners = new ArrayList<HypothesisListener>();
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
	
	private class HypothesisRadioButton extends RadioButton
	{
		public int contrastDF;
		public String predictor;
		public String interactionPredictor;
		
		public HypothesisRadioButton(String group, String label,
				int contrastDF, String predictor, String interactionPredictor)
		{
			super(group, label, true);
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
	

	
    public HypothesisIndependentMeasuresPanel()
    {
    	super();
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.hypothesisTitle());
        HTML description = new HTML(Glimmpse.constants.hypothesisDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(interactionsTable);
        panel.add(mainEffectsTable);

        
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
	public void onRelativeGroupSize(List<Integer> relativeSizes)
	{
		this.relativeGroupSizes = relativeSizes;
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

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		this.outcomes = outcomes;
	}

	@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		if (repeatedMeasures == null || repeatedMeasures.size() <= 0)
			skip = false;
		else
			skip = true;		
	}

	private void addMainEffect(String predictor, int numCategories)
	{
		int startRow = mainEffectsTable.getRowCount();
		HypothesisRadioButton rb = 
			new HypothesisRadioButton(HYPOTHESIS_RADIO_GROUP, 
					"The outcomes will differ by <u>" + predictor + "</u>",
					numCategories - 1, predictor);
		rb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				HypothesisRadioButton source = (HypothesisRadioButton) event.getSource();
				for(HypothesisListener listener: listeners)
					listener.onMainEffectsHypothesis(source.predictor);
			}
		});
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

			int df = (predictorCategories.size()-1) * (interactionCategories.size()-1);
			HypothesisRadioButton rb = new HypothesisRadioButton(HYPOTHESIS_RADIO_GROUP, 
					"The effect of <u>" + predictor + 
					"</u> on the outcomes will be different depending on the value of <u>" + interaction + "</u>",
					df, predictor, interaction);
			rb.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event)
				{
					HypothesisRadioButton source = (HypothesisRadioButton) event.getSource();
					for(HypothesisListener listener: listeners)
						listener.onInteractionHypothesis(source.predictor, source.interactionPredictor);
				}
			});
			interactionsTable.setWidget(startRow, 0, rb);
			startRow++;
		}
	}
    
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		HypothesisRadioButton selectedRb = null;
		if (!skip)
		{
			// find the selected hypothesis
			for(int i = 0; i < interactionsTable.getRowCount(); i++)
			{
				HypothesisRadioButton currentRb = (HypothesisRadioButton) interactionsTable.getWidget(i, 0);
				if (currentRb.getValue())
				{
					selectedRb = currentRb;
					break;
				}
			}
			if (selectedRb == null)
			{
				for(int i = 0; i < mainEffectsTable.getRowCount(); i++)
				{
					HypothesisRadioButton currentRb = (HypothesisRadioButton) mainEffectsTable.getWidget(i, 0);
					if (currentRb.getValue())
					{
						selectedRb = currentRb;
						break;
					}
				}
			}
			if (selectedRb != null)
			{
				buildBetweenSubjectContrastXML(buffer, selectedRb);
				buildWithinSubjectContrastXML(buffer);
				buildThetaNullMatrixXML(buffer, selectedRb);
			}
		}
		return buffer.toString();
	}
	
	public void addHypothesisListener(HypothesisListener listener)
	{
		listeners.add(listener);
	}

	public void buildBetweenSubjectContrastXML(StringBuffer buffer, HypothesisRadioButton rb)
	{
		XMLUtilities.fixedRandomMatrixOpenTag(buffer, GlimmpseConstants.MATRIX_BETWEEN_CONTRAST, true);
		if (rb.interactionPredictor == null)
		{
			// main effect hypothesis
			buildMainEffectsContrastXML(buffer, rb);
		}
		else
		{
			// interaction hypothesis
			buildInteractionContrastXML(buffer, rb);
		}
		if (hasCovariate)
		{
			// build the gaussian portion of the C matrix
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rb.contrastDF, 1);
			for(int r = 0; r < rb.contrastDF; r++)
			{
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
				buffer.append(1);
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
	}
	
	private void buildInteractionContrastXML(StringBuffer buffer, HypothesisRadioButton rb)
	{
		List<String> predictorCategories = predictorMap.get(rb.predictor);
		int predictorColumn =groupColumnLookup.get(rb.predictor);
		List<String> interactionCategories = predictorMap.get(rb.interactionPredictor);
		int interactionColumn =groupColumnLookup.get(rb.interactionPredictor);

		List<String[]> predictorPairs = ListUtilities.getPairs(predictorCategories);
		List<String[]> interactionPairs = ListUtilities.getPairs(interactionCategories);
		int predictorPairCount = 0;
		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, 
				rb.contrastDF, groups.getNumberOfRows());
		for(String[] predictorPair: predictorPairs)
		{
			if (predictorPairs.size() > 1 && predictorPairCount >= predictorPairs.size() - 1)
				break;
			else
				predictorPairCount++;
			
			HashMap<String,Double> predictorCoefficients = new HashMap<String,Double>();
			predictorCoefficients.put(predictorPair[0], 1.0);
			predictorCoefficients.put(predictorPair[1], -1.0);

			int pairCount = 0;
			for(String[] pair: interactionPairs)
			{
				if (interactionPairs.size() > 1 && pairCount >= interactionPairs.size() - 1)
					break;
				else
					pairCount++;
				
				HashMap<String,Double> interactionCoefficients = new HashMap<String,Double>();
				interactionCoefficients.put(pair[0], 1.0);
				interactionCoefficients.put(pair[1], -1.0);
				
				// okay, now we can finally bust out some XML
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
				for(int c = 0; c < groups.getNumberOfRows(); c++)
				{
					XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
					Double interactionCoefficient = 
						interactionCoefficients.get(groups.getValueString(c, interactionColumn));
					Double predictorCoefficient = 
						predictorCoefficients.get(groups.getValueString(c, predictorColumn));
					if (interactionCoefficient != null && predictorCoefficient != null)
					{
						buffer.append(interactionCoefficient * predictorCoefficient);
					}
					else
					{
						buffer.append(0);
					}
					XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
				}			
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
			}			
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
	}
	
	private void buildMainEffectsContrastXML(StringBuffer buffer, HypothesisRadioButton rb)
	{
		String predictor = rb.predictor;
		List<String> categories = predictorMap.get(predictor);
		int groupColumn = groupColumnLookup.get(predictor);
		// calculate the standardizing denominator values
		HashMap<String,Double> denominators = new HashMap<String,Double>();
		for(int i = 0; i < groups.getNumberOfRows(); i++)
		{
			String categoryValue = groups.getValueString(i, groupColumn);
			Double denominator = denominators.get(categoryValue);
			if (denominator != null)
				denominator += relativeGroupSizes.get(i);
			else
				denominator = new Double(relativeGroupSizes.get(i));
			denominators.put(categoryValue, denominator);
		}
		
		// get all possible pairs
		List<String[]> categoryPairs = ListUtilities.getPairs(categories);
		int pairCount = 0;
		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, 
				rb.contrastDF, groups.getNumberOfRows());
		for(String[] pair: categoryPairs)
		{
			if (categoryPairs.size() > 1 && pairCount >= categoryPairs.size() - 1)
				break;
			else
				pairCount++;
			
			HashMap<String,Double> coefficients = new HashMap<String,Double>();
			coefficients.put(pair[0], 1.0);
			coefficients.put(pair[1], -1.0);
			
			// okay, now we can finally bust out some XML
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
			for(int c = 0; c < groups.getNumberOfRows(); c++)
			{
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
				String currentCategory = groups.getValueString(c, groupColumn);
				Double coefficient = coefficients.get(currentCategory);
				if (coefficient != null)
				{
					buffer.append(coefficients.get(currentCategory) * 
							relativeGroupSizes.get(c) / denominators.get(currentCategory));
				}
				else
				{
					buffer.append(0);
				}
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
			}			
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
	}
	
	public void buildWithinSubjectContrastXML(StringBuffer buffer)
	{
		
//		if (deckPanel.getVisibleWidget() == INDEPENDENT_GROUPS_INDEX)
//		{
//			// for non-repeated measures, the U matrix simply has one column per outcome
//			// if all outcomes are selected, the U ,matrix will be an identity matrix
//			int rows = independentOutcomesTable.getRowCount();
//			int cols = independentOutcomesTable.getRowCount();
//
//			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_WITHIN_CONTRAST, rows, cols);
//			for(int r = 0; r < rows; r++)
//			{
//				XMLUtilities.openTag(buffer, "r");
//				for(int c = 0; c < cols; c++)
//				{
//					XMLUtilities.openTag(buffer, "c");
//					CheckBox cb = (CheckBox) independentOutcomesTable.getWidget(r, 0);
//					if (r == c && cb.getValue())
//						buffer.append(1);
//					else
//						buffer.append(0);
//					XMLUtilities.closeTag(buffer, "c");
//				}
//				XMLUtilities.closeTag(buffer, "r");
//			}
//			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
//		}
//		else
//		{
////			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_WITHIN_CONTRAST, rows, cols);
////			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
//
//		}
	}
	
	private void buildThetaNullMatrixXML(StringBuffer buffer, HypothesisRadioButton rb)
	{
		int rows = rb.contrastDF;
		int cols = outcomes.size();

		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_THETA, rows, cols);
		for(int r = 0; r < rows; r++)
		{
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
			for(int c = 0; c < cols; c++)
			{
				// we assume null hypotheses of 0 for guided mode
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
				buffer.append(0);
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

	}

}
