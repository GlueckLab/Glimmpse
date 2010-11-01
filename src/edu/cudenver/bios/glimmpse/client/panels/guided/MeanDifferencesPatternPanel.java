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
import edu.cudenver.bios.glimmpse.client.listener.HypothesisListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class MeanDifferencesPatternPanel extends WizardStepPanel
implements PredictorsListener, HypothesisListener, ClickHandler
{
	private static final String PATTERN_RADIO_GROUP = "patternGroup";
	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	private enum HypothesisType
	{
		MAIN_EFFECT,
		INTERACTION,
		TREND
	};
	
	protected FlexTable patternTable = new FlexTable();
	protected List<String> outcomes = null;
	protected HashMap<String, ArrayList<String>> predictorMap = null;
	protected DataTable groups = null;
	protected HashMap<String, Integer> groupColumnLookup = new HashMap<String,Integer>();
	protected HypothesisType hypothesisType = HypothesisType.MAIN_EFFECT ;
	
	protected int numColumns = -1;
	
	public MeanDifferencesPatternPanel()
	{
		super();
		VerticalPanel panel = new VerticalPanel();
		
        HTML header = new HTML(Glimmpse.constants.meanDifferencePatternTitle());
        HTML description = new HTML(Glimmpse.constants.meanDifferencePatternDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(patternTable);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		initWidget(panel);
		// TODO Auto-generated constructor stub
	}

	@Override 
	public void onEnter()
	{
		switch (hypothesisType)
		{
		case MAIN_EFFECT:
			buildMainEffectPatterns();
			break;
		case INTERACTION:
			buildInteractionPatterns();
			break;
		}
	}
	
	private void buildMainEffectPatterns()
	{
		if (numColumns > 0)
		{
			ArrayList<Integer> data = new ArrayList<Integer>();
			for(int i = 0; i < numColumns-1; i++) data.add(1);
			data.add(2);
			RadioButton rb = new RadioButton(PATTERN_RADIO_GROUP, "One group differs");
			rb.addClickHandler(this);
			patternTable.setWidget(0, 0, new MeanDifferenceMainEffectBarChart(data, WIDTH, HEIGHT));
			patternTable.setWidget(1, 0, rb);
			if (numColumns > 2)
			{
				// linear dose response data
				data.clear();
				for(int i = 0; i < numColumns; i++) data.add(i+1);
				rb = new RadioButton(PATTERN_RADIO_GROUP,  "Linear Dose-Response");
				rb.addClickHandler(this);
				patternTable.setWidget(0, 1, new MeanDifferenceMainEffectBarChart(data, WIDTH, HEIGHT));
				patternTable.setWidget(1, 1, rb);

				
				// exponential dose response
				data.clear();
				for(int i = 0; i < numColumns; i++) data.add((i+1)*(i+1));
				rb = new RadioButton(PATTERN_RADIO_GROUP, "Exponential Dose-Response");
				rb.addClickHandler(this); 
				patternTable.setWidget(0, 2, new MeanDifferenceMainEffectBarChart(data, WIDTH, HEIGHT));
				patternTable.setWidget(1, 2, rb);


			}
		}
	}
	
	private void buildInteractionPatterns()
	{
		
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
		hypothesisType = HypothesisType.MAIN_EFFECT;
		if (predictorMap != null)
		{
			List<String> categories = predictorMap.get(predictor);
			if (!categories.isEmpty()) numColumns = categories.size();
		}
	}

	@Override
	public void onInteractionHypothesis(String predictor,
			String interactionPredictor)
	{
		hypothesisType = HypothesisType.INTERACTION;
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap,
			DataTable groups)
	{
		this.predictorMap = predictorMap;
		this.groups = groups;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		notifyComplete();
	}

}
