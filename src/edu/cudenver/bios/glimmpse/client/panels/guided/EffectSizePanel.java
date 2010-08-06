package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.BarChart.Options;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class EffectSizePanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, DynamicListValidator
{
	protected static final int MAX_RELATIVE_EFFECT_SIZE = 10;
	
	// data table holding the relative sizes of the groups
	protected DataTable barChartData = DataTable.create();
	protected Options options = createOptions();
	protected BarChart chart = new BarChart();
	
	protected FlexTable outcomesTable = new FlexTable();
	protected List<String> outcomesList = null;
	protected List<RepeatedMeasure> repeatedMeasuresList = null;
	
	protected HashMap<String, ArrayList<String>> predictorMap;
	protected DataTable groupData;
	
   	// list of per group sample sizes
	String[] columnNames = { Glimmpse.constants.betaScaleTableColumn() };
    protected DynamicListPanel betaScaleListPanel =
    	new DynamicListPanel(columnNames, this);
    
    public EffectSizePanel()
    {
    	super(Glimmpse.constants.stepsLeftEffectSize());
        VerticalPanel panel = new VerticalPanel();

        // create header/instruction text
        HTML header = new HTML("Effect Sizes");
        HTML description = new HTML("Here you will estimate the differences you expect in your outcomes.  You will first specify the relative differences, and then a list");
        
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(createRelativeEffectSizePanel());
        panel.add(createBetaScalePanel());

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
        initWidget(panel);
    }
    
    private VerticalPanel createRelativeEffectSizePanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
        HTML header = new HTML("Relative Effect Sizes");
        HTML description = new HTML("Use the drop down list to adjust the relative differences in each outcome");
        
        panel.add(header);
        panel.add(description);
        panel.add(outcomesTable);
        panel.add(chart);

    	// add style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    	    	
    	return panel;
    }
    
    private VerticalPanel createBetaScalePanel()
    {
    	VerticalPanel panel = new VerticalPanel();
    	
        HTML header = new HTML("Actual Effect Sizes");
        HTML description = new HTML("In the list below, enter the true effect sizes you expect.  Note that this is a multiplier...");
        
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);

    	// add style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    	    	
    	return panel;
    }
    
    private Options createOptions() 
    {
    	options = Options.create();
    	options.setWidth(500);
    	options.setHeight(500);
    	//options.set3D(true);
    	options.setTitle("Estimated Relative Clinical Differences");
    	options.setShowCategories(true);
    	return options;
    }
	
    public void reset()
    {
    	barChartData.removeRows(0, barChartData.getNumberOfRows());
    	outcomesTable.removeAllRows();
    }

    @Override
    public void onEnter()
    {
    	reset();
    	loadOutcomesList();
    	loadBarChart();
    }
    

    
    private void loadOutcomesList()
    {
    	outcomesTable.removeAllRows();
    	if (outcomesList != null)
    	{
    		ListBox lb = new ListBox();
    		for(String outcome: outcomesList)
    		{
    			lb.addItem(outcome);
    		}
    		outcomesTable.setWidget(0, 0, lb);
    	}
		
    	int col = 1;
		for (RepeatedMeasure rm: repeatedMeasuresList)
		{
    		ListBox lb = new ListBox();
    		for(int i = 1; i <= rm.repeats; i++)
    		{
    			lb.addItem(rm.units + " " + i);
    		}
    		outcomesTable.setWidget(0, col++, lb);
		}
    }
    
    private String effectSizeRowToLabel(int row)
    {
    	StringBuffer buffer = new StringBuffer();
    	for(int col = 0; col < groupData.getNumberOfColumns(); col++)
    	{
    		if (col > 0) buffer.append(", ");
    		buffer.append(groupData.getValueString(row, col));
    	}
    	return buffer.toString();
    }
    
    private void loadBarChart()
    {
    	barChartData.removeRows(0, barChartData.getNumberOfRows());
    	barChartData.removeColumns(0, barChartData.getNumberOfColumns());
    	barChartData.addColumn(ColumnType.STRING, "labels");
    	barChartData.addRows(groupData.getNumberOfRows());

		for(int r = 0; r < groupData.getNumberOfRows(); r++)
		{
			String label = effectSizeRowToLabel(r);
			barChartData.setCell(r, 0, label, label, null);
		}
    	int col = 1; 
    	for(String outcome: outcomesList)
    	{
    		barChartData.addColumn(ColumnType.NUMBER, outcome);
    		for(int r = 0; r < groupData.getNumberOfRows(); r++)
    		{
    			barChartData.setCell(r, col, r, "0", null);
    		}
    		col++;
    	}
    	chart.draw(barChartData, options);
    }
    
	@Override
	public void onOutcomes(List<String> outcomes)
	{
		outcomesList = outcomes;
	}

	@Override
	public void onRepeatedMeasures(List<RepeatedMeasure> repeatedMeasures)
	{
		this.repeatedMeasuresList = repeatedMeasures;
	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap, DataTable groupData)
	{
		this.predictorMap = predictorMap;	
		this.groupData = groupData;
	}

	
	private ListBox createEffectSizeListBox()
	{
		ListBox lb = new ListBox();
		for(int i = 0; i <= MAX_RELATIVE_EFFECT_SIZE; i++) lb.addItem(Integer.toString(i));
		return lb;
	}
	
	@Override
	public void onValidRowCount(int validRowCount)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validate(String value, int column)
			throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		
	}
}
