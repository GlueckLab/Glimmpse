package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DeckPanel;
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

public class HypothesisRepeatedMeasuresPanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, RepeatedMeasuresListener,
RelativeGroupSizeListener, CovariateListener
{   
	private static final String HYPOTHESIS_RADIO_GROUP = "hypothesisRepeatedGroup";
	
	protected boolean hasRepeatedMeasures = false;
	protected List<String> outcomes = null;
	protected HashMap<String, ArrayList<String>> predictorMap = null;
	protected DataTable groups = null;
	protected HashMap<String, Integer> groupColumnLookup = new HashMap<String,Integer>();
	protected List<RepeatedMeasure> repeatedMeasures = null;
	protected List<Integer> relativeGroupSizes = null;

	protected ArrayList<HypothesisListener> listeners = new ArrayList<HypothesisListener>();
	// running counter of the number of 
	
	protected boolean hasCovariate = false;
	
    public HypothesisRepeatedMeasuresPanel()
    {
    	super();
    	complete = true;
    	skip = true;
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.hypothesisTitle());
        HTML description = new HTML(Glimmpse.constants.hypothesisDescription());
        
        
        panel.add(header);
        panel.add(description);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        initWidget(panel);
    }


    

    


    
    private VerticalPanel createRepeatedMeasuresPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	return panel;
    }
    
    @Override
    public void reset() 
    {

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
			skip = true;
		else
			skip = false;		
		
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
	public void onEnter()
	{
		// TODO: check if repeated measures

	}
	


	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		buildBetweenSubjectContrastXML(buffer);
		buildWithinSubjectContrastXML(buffer);
		buildThetaNullMatrixXML(buffer);
		return buffer.toString();
	}
	
	public void buildBetweenSubjectContrastXML(StringBuffer buffer)
	{
		buffer.append("<fixedRandomMatrix name='");
		buffer.append(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST);
		buffer.append("' combineHorizontal='true' >");
		int rows = 0;
//			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, rows, columns);
//
//			// add the contrasts for main effects
//			for(int i = 0; i < independentMainEffectsTable.getRowCount(); i++)
//			{
//				CheckBox cb = (CheckBox) independentMainEffectsTable.getWidget(i, CHECKBOX_COLUMN);
//				if (cb.getValue())
//				{
//					String predictor = ((Hidden) independentMainEffectsTable.getWidget(i, PREDICTOR_COLUMN)).getDefaultValue();
//					buildMainEffectsContrastXML(buffer, predictor);
//				}
//			}
//
//			// add the contrasts for interactions
//			for(int i = 0; i < independentInteractionsTable.getRowCount(); i++)
//			{
//				CheckBox cb = (CheckBox) independentInteractionsTable.getWidget(i, CHECKBOX_COLUMN);
//				if (cb.getValue())
//				{
//					String predictor = ((Hidden) independentInteractionsTable.getWidget(i, PREDICTOR_COLUMN)).getDefaultValue();
//					String interaction = ((Hidden) independentInteractionsTable.getWidget(i, INTERACTION_PREDICTOR_COLUMN)).getDefaultValue();
//					buildInteractionContrastXML(buffer, predictor, interaction);
//				}
//			}
//			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
//		}
//		else
//		{
//			
//		}
//		
//		if (hasCovariate)
//		{
//			// build the gaussian portion of the C matrix
//			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, 1);
//			for(int r = 0; r < rows; r++)
//			{
//				XMLUtilities.openTag(buffer, "r");
//				XMLUtilities.openTag(buffer, "c");
//				buffer.append(1);
//				XMLUtilities.closeTag(buffer, "c");
//				XMLUtilities.closeTag(buffer, "r");
//			}
//			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
//		}
//		
//		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
	}
	
	private void buildInteractionContrastXML(StringBuffer buffer, String predictor, String interaction)
	{
		List<String> predictorCategories = predictorMap.get(predictor);
		int predictorColumn =groupColumnLookup.get(predictor);
		List<String> interactionCategories = predictorMap.get(interaction);
		int interactionColumn =groupColumnLookup.get(interaction);

		List<String[]> predictorPairs = ListUtilities.getPairs(predictorCategories);
		List<String[]> interactionPairs = ListUtilities.getPairs(interactionCategories);
		int predictorPairCount = 0;
		
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
				XMLUtilities.openTag(buffer, "r");
				for(int c = 0; c < groups.getNumberOfRows(); c++)
				{
					XMLUtilities.openTag(buffer, "c");
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
					XMLUtilities.closeTag(buffer, "c");
				}			
				XMLUtilities.closeTag(buffer, "r");
			}			
		}
	}
	
	private void buildMainEffectsContrastXML(StringBuffer buffer, String predictor)
	{
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
			XMLUtilities.openTag(buffer, "r");
			for(int c = 0; c < groups.getNumberOfRows(); c++)
			{
				XMLUtilities.openTag(buffer, "c");
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
				XMLUtilities.closeTag(buffer, "c");
			}			
			XMLUtilities.closeTag(buffer, "r");
		}
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
	
	private void buildThetaNullMatrixXML(StringBuffer buffer)
	{
		int rows = 0;
		int cols = 0;
		
//		if (deckPanel.getVisibleWidget() == INDEPENDENT_GROUPS_INDEX)
//		{
//			// the dimension of theta is a x b, where a = #rows in C matrix, b = #cols in U matrix
//			rows = 0; //TODO: contrastCount;
//			cols = independentOutcomesTable.getRowCount();
//		}
//		else
//		{
//		}
//		
//		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_THETA, rows, cols);
//		for(int r = 0; r < rows; r++)
//		{
//			XMLUtilities.openTag(buffer, "r");
//			for(int c = 0; c < cols; c++)
//			{
//				// we assume null hypotheses of 0 for guided mode
//				XMLUtilities.openTag(buffer, "c");
//				buffer.append(0);
//				XMLUtilities.closeTag(buffer, "c");
//			}
//			XMLUtilities.closeTag(buffer, "r");
//		}
//		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

	}
	
	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		
		return buffer.toString();
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

	/**
	 * Needed for generating main effects contrasts
	 */
	@Override
	public void onRelativeGroupSize(List<Integer> relativeSizes)
	{
		relativeGroupSizes = relativeSizes;
	}
	
	public void addHypothesisListener(HypothesisListener listener)
	{
		listeners.add(listener);
	}
}
