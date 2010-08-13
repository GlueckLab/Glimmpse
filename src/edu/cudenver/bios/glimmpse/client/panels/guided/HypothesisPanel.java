package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.ListUtilities;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.listener.RelativeGroupSizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class HypothesisPanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, 
RelativeGroupSizeListener, CovariateListener, ClickHandler
{   
	private static final int INDEPENDENT_GROUPS_INDEX = 0;
	private static final int REPEATED_MEASURES_INDEX = 1;
	
	private static final int CHECKBOX_COLUMN = 0;
	private static final int LABEL_COLUMN = 1;
	private static final int PREDICTOR_COLUMN = 2;
	private static final int INTERACTION_PREDICTOR_COLUMN = 3;

	
	private class ContrastCountCheckBox extends CheckBox
	{
		public int contrastCount = 0;
		public ContrastCountCheckBox(int contrastCount)
		{
			this.contrastCount = contrastCount;
		}
	}
	protected int contrastCount = 0;
	
	protected boolean hasRepeatedMeasures = false;
	protected List<String> outcomes = null;
	protected HashMap<String, ArrayList<String>> predictorMap = null;
	protected DataTable groups = null;
	protected HashMap<String, Integer> groupColumnLookup = new HashMap<String,Integer>();
	
	protected List<Integer> relativeGroupSizes = null;
	protected DeckPanel deckPanel = new DeckPanel();
	
	// independent groups widgets
	protected FlexTable independentMainEffectsTable = new FlexTable();
	protected FlexTable independentInteractionsTable = new FlexTable();
	protected FlexTable independentOutcomesTable = new FlexTable();

	// running counter of the number of 
	
	protected boolean hasCovariate = false;
	
    public HypothesisPanel()
    {
    	super(Glimmpse.constants.stepsLeftHypotheses());
    	complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        HTML header = new HTML(Glimmpse.constants.hypothesisTitle());
        HTML description = new HTML(Glimmpse.constants.hypothesisDescription());
        
        deckPanel.add(createIndependentGroupsPanel());
        deckPanel.add(createRepeatedMeasuresPanel());
        deckPanel.showWidget(INDEPENDENT_GROUPS_INDEX);
        
        panel.add(header);
        panel.add(description);
        panel.add(deckPanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        initWidget(panel);
    }

    private VerticalPanel createIndependentGroupsPanel()
    {
    	VerticalPanel panel = new VerticalPanel();

        panel.add(createIndependentMainEffectsPanel());
        panel.add(createIndependentInteractionsPanel());
        panel.add(createIndependentOutcomesPanel());
        
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);

        
        return panel;
    	
    }
    
    private VerticalPanel createIndependentOutcomesPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML header = new HTML("Test the Selected Hypotheses for");

    	panel.add(header);
    	panel.add(this.independentOutcomesTable);
    	
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
    }
    
    private VerticalPanel createIndependentMainEffectsPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML header = new HTML("Main Effects");

    	panel.add(header);
    	panel.add(this.independentMainEffectsTable);
    	
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
    }
    
    private VerticalPanel createIndependentInteractionsPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML header = new HTML("Interactions");

    	panel.add(header);
    	panel.add(this.independentInteractionsTable);
    	
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
    }
    
    private VerticalPanel createRepeatedMeasuresPanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
    	return panel;
    }
    
    public void reset() 
    {
    	independentInteractionsTable.removeAllRows();
    	independentMainEffectsTable.removeAllRows();
    	independentOutcomesTable.removeAllRows();
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
		{
			deckPanel.showWidget(INDEPENDENT_GROUPS_INDEX);
		}
		else
		{
			deckPanel.showWidget(REPEATED_MEASURES_INDEX);
		}
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
		contrastCount = 0;
		// TODO: check if repeated mesures
		Object[] predictorArray = (Object[]) predictorMap.keySet().toArray();
		reset();
		int i = 0;
		for(Object predictor: predictorArray)
		{
			List<String> categories = predictorMap.get(predictor);
			addMainEffect((String) predictor, categories.size());
			addInteractions((String) predictor, ++i, predictorArray);
		}
		
		for(String outcome: outcomes)
		{
			addOutcome(outcome);
		}
	}
	
	private void addOutcome(String outcome)
	{
		int startRow = independentOutcomesTable.getRowCount();
		CheckBox cb = new CheckBox();
		cb.addClickHandler(this);
		independentOutcomesTable.setWidget(startRow, 0, cb);
		independentOutcomesTable.setWidget(startRow, 1, new HTML(outcome));
	}
	
	private void addMainEffect(String predictor, int numCategories)
	{
		int startRow = independentMainEffectsTable.getRowCount();
		ContrastCountCheckBox cb = new ContrastCountCheckBox(numCategories - 1);
		cb.addClickHandler(this);
		cb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				ContrastCountCheckBox contrastCheckBox = (ContrastCountCheckBox) event.getSource();
				if (contrastCheckBox.getValue())
					contrastCount += contrastCheckBox.contrastCount;
				else
					contrastCount -= contrastCheckBox.contrastCount;
			}
		});
		independentMainEffectsTable.setWidget(startRow, CHECKBOX_COLUMN, cb);
		independentMainEffectsTable.setWidget(startRow, LABEL_COLUMN, 
				new HTML("The outcomes will differ by " + predictor));
		Hidden predictorHidden = new Hidden("predictor");
		predictorHidden.setValue(predictor);
		independentMainEffectsTable.setWidget(startRow, PREDICTOR_COLUMN, predictorHidden);
	}
	
	private void addInteractions(String predictor, int startIndex, Object[] predictorArray)
	{
		int startRow = independentInteractionsTable.getRowCount();
		for(int i = startIndex; i < predictorArray.length; i++)
		{
			String interaction = (String) predictorArray[i];
			List<String> predictorCategories = predictorMap.get(predictor);
			List<String> interactionCategories = predictorMap.get(interaction);

			int numContrasts = predictorCategories.size() * (interactionCategories.size()-1);
			ContrastCountCheckBox cb = new ContrastCountCheckBox(numContrasts);
			cb.addClickHandler(this);
			cb.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event)
				{
					ContrastCountCheckBox contrastCheckBox = (ContrastCountCheckBox) event.getSource();
					if (contrastCheckBox.getValue())
						contrastCount += contrastCheckBox.contrastCount;
					else
						contrastCount -= contrastCheckBox.contrastCount;
				}
			});
			independentInteractionsTable.setWidget(startRow, CHECKBOX_COLUMN, cb);
			independentInteractionsTable.setWidget(startRow, LABEL_COLUMN, 
					new HTML("The effect of " + predictor + 
							" on the outcomes will be different depending on the value of " + interaction));
			Hidden predictorHidden = new Hidden("predictor");
			predictorHidden.setValue(predictor);
			independentInteractionsTable.setWidget(startRow, PREDICTOR_COLUMN, predictorHidden);
			Hidden interactionHidden = new Hidden("interaction");
			interactionHidden.setValue(interaction);
			independentInteractionsTable.setWidget(startRow, INTERACTION_PREDICTOR_COLUMN, 
					interactionHidden);

			startRow++;
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
		boolean hypothesisSelected = false;
		
		// check if any hypotheses are selected
		if (deckPanel.getVisibleWidget() == INDEPENDENT_GROUPS_INDEX)
		{
			for(int r = 0; r < independentMainEffectsTable.getRowCount(); r++)
			{
				CheckBox cb = (CheckBox) independentMainEffectsTable.getWidget(r, 0);
				if (cb.getValue())
				{
					hypothesisSelected = true;
					break;
				}
			}
			if (!hypothesisSelected)
			{
				for(int r = 0; r < independentInteractionsTable.getRowCount(); r++)
				{
					CheckBox cb = (CheckBox) independentInteractionsTable.getWidget(r, 0);
					if (cb.getValue())
					{
						hypothesisSelected = true;
						break;
					}
				}
			}
		}
		else
		{
			
		}
		
		if (hypothesisSelected)
			notifyComplete();
		else
			notifyInProgress();
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
		return buffer.toString();
	}
	
	public void buildBetweenSubjectContrastXML(StringBuffer buffer)
	{
		buffer.append("<fixedRandomMatrix name='");
		buffer.append(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST);
		buffer.append("' combineHorizontal='true' >");
		int rows = contrastCount;
		if (deckPanel.getVisibleWidget() == INDEPENDENT_GROUPS_INDEX)
		{
			int columns = groups.getNumberOfRows();
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, columns);

			int startIndex = 0;
			// add the contrasts for main effects
			for(int i = 0; i < independentMainEffectsTable.getRowCount(); i++)
			{
				CheckBox cb = (CheckBox) independentMainEffectsTable.getWidget(i, CHECKBOX_COLUMN);
				if (cb.getValue())
				{
					String predictor = ((Hidden) independentMainEffectsTable.getWidget(i, PREDICTOR_COLUMN)).getDefaultValue();
					buildMainEffectsContrastXML(buffer, predictor);
				}
			}

			// add the contrasts for interactions
			for(int i = 0; i < independentInteractionsTable.getRowCount(); i++)
			{
				CheckBox cb = (CheckBox) independentInteractionsTable.getWidget(i, CHECKBOX_COLUMN);
				if (cb.getValue())
				{
					String predictor = ((Hidden) independentInteractionsTable.getWidget(i, PREDICTOR_COLUMN)).getDefaultValue();
					String interaction = ((Hidden) independentInteractionsTable.getWidget(i, INTERACTION_PREDICTOR_COLUMN)).getDefaultValue();
					buildInteractionContrastXML(buffer, predictor, interaction);
				}
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		else
		{
			
		}
		
		if (hasCovariate)
		{
			// build the gaussian portion of the C matrix
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, 1);
			for(int r = 0; r < rows; r++)
			{
				XMLUtilities.openTag(buffer, "r");
				XMLUtilities.openTag(buffer, "c");
				buffer.append(1);
				XMLUtilities.closeTag(buffer, "c");
				XMLUtilities.closeTag(buffer, "r");
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
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
		Window.alert(predictor);
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
		if (deckPanel.getVisibleWidget() == INDEPENDENT_GROUPS_INDEX)
		{
			// for non-repeated measures, the U matrix simply has one column per outcome
			// if all outcomes are selected, the U ,matrix will be an identity matrix
			int rows = independentOutcomesTable.getRowCount();
			int cols = independentOutcomesTable.getRowCount();

			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_WITHIN_CONTRAST, rows, cols);
			for(int r = 0; r < rows; r++)
			{
				XMLUtilities.openTag(buffer, "r");
				for(int c = 0; c < cols; c++)
				{
					XMLUtilities.openTag(buffer, "c");
					CheckBox cb = (CheckBox) independentOutcomesTable.getWidget(r, 0);
					if (r == c && cb.getValue())
						buffer.append(1);
					else
						buffer.append(0);
					XMLUtilities.closeTag(buffer, "c");
				}
				XMLUtilities.closeTag(buffer, "r");
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		else
		{
//			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_WITHIN_CONTRAST, rows, cols);
//			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

		}
	}
	
	private void buildThetaMatrixXML(StringBuffer buffer)
	{
		int rows = 0;
		int cols = 0;
		
		if (deckPanel.getVisibleWidget() == INDEPENDENT_GROUPS_INDEX)
		{
			// the dimension of theta is a x b, where a = #rows in C matrix, b = #cols in U matrix
			rows = contrastCount;
			cols = independentOutcomesTable.getRowCount();
		}
		else
		{
		}
		
		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_THETA, rows, cols);
		for(int r = 0; r < rows; r++)
		{
			XMLUtilities.openTag(buffer, "r");
			for(int c = 0; c < cols; c++)
			{
				// we assume null hypotheses of 0 for guided mode
				XMLUtilities.openTag(buffer, "c");
				buffer.append(0);
				XMLUtilities.closeTag(buffer, "c");
			}
			XMLUtilities.closeTag(buffer, "r");
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

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
}
