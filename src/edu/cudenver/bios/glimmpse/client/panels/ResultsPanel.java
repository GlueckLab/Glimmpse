package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.ColumnChart.Options;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.StudyDesignManager;
import edu.cudenver.bios.glimmpse.client.listener.OptionsListener;

public class ResultsPanel extends WizardStepPanel
implements OptionsListener
{
	protected StudyDesignManager manager;
	
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_CREATED = 201;
	private static final String POWER_URL = "/webapps/power/power";
    private static final String SAMPLE_SIZE_URL = "/webapps/power/samplesize";
	private static final String EFFECT_SIZE_URL = "/webapps/power/difference";
	
	private NumberFormat doubleFormatter = NumberFormat.getFormat("0.0000");
	
	// google visualization api data table to hold results
	protected DataTable resultsData; 
	
	// tabular display of results
	protected VerticalPanel resultsTablePanel = new VerticalPanel();
	protected Table resultsTable; 
	
	// curve display
	protected VerticalPanel resultsCurvePanel = new VerticalPanel();
	protected Grid curveGrid = new Grid(1,1);
	
	// options for display of data
	protected boolean showTable = true;
	protected boolean showCurve = true;
	protected XAxisType xaxisType = null;
	protected CurveSubset[] curveSubsets = null;
	
    public ResultsPanel(StudyDesignManager manager)
    {
    	super(Glimmpse.constants.stepsLeftResults());
    	this.manager = manager;
    	
    	// create the data table 
    	buildDataTable();
    	resultsTable = new Table(resultsData, null);
    	
        VerticalPanel panel = new VerticalPanel();
        
        buildTablePanel();
        buildCurvePanel();
        
        panel.add(resultsTablePanel);
        panel.add(resultsCurvePanel);

        
        initWidget(panel);
    }
    
    private void buildDataTable()
    {
    	// set up the columns in the data table
    	resultsData = DataTable.create();
    	resultsData.addColumn(ColumnType.NUMBER, "Alpha", "alpha");
    	resultsData.addColumn(ColumnType.STRING, "Test", "test");
    	resultsData.addColumn(ColumnType.STRING, "Power Method", "powerMethod");
    	resultsData.addColumn(ColumnType.NUMBER, "Quantile", "quantile");
    	resultsData.addColumn(ColumnType.NUMBER, "&Sigma; Scale", "sigmaScale");
    	resultsData.addColumn(ColumnType.NUMBER, "&beta; Scale", "betaScale");
    	resultsData.addColumn(ColumnType.NUMBER, "Total Sample Size", "sampleSize");
    	resultsData.addColumn(ColumnType.NUMBER, "Nominal Power", "nominalPower");
    	resultsData.addColumn(ColumnType.NUMBER, "Actual Power", "actualPower");
    }
    
    private void buildTablePanel()
    {
    	resultsTablePanel.add(new HTML("Results"));
    	resultsTablePanel.add(resultsTable);
    }
    
    private void buildCurvePanel()
    {
    	resultsCurvePanel.add(new HTML("Curves"));
    	resultsCurvePanel.add(curveGrid);

    }
    
    public void reset()
    {
    	
    }

    @Override
    public void onEnter()
    {
    	reset();
    	sendPowerRequest();
    }
    
    private void showWorkingDialog()
    {
    	
    }
    
    private void hideWorkingDialog()
    {
    	
    }
    
    private void showError(String message)
    {
    	
    }
    
    private void showResults(String resultXML)
    {
        try
        {
        	// parse the returned XML
            Document doc = XMLParser.parse(resultXML);
            Node powerList = doc.getFirstChild();
            if (powerList == null)
            	throw new IllegalArgumentException("No results returned");
            NamedNodeMap attrList = powerList.getAttributes();
            if (attrList == null)
            	throw new IllegalArgumentException("Invalid response from power server");
            Node countAttr = attrList.getNamedItem("count");
            if (countAttr == null)
            	throw new IllegalArgumentException("Invalid response from power server");
            int count = Integer.parseInt(countAttr.getNodeValue());
            
            // fill the google visualization data table
            NodeList glmmPowerList = doc.getElementsByTagName("glmmPower");
            for(int i = 0; i < count; i++)
            {
            	Node glmmPower = glmmPowerList.item(i);
            	NamedNodeMap attrs = glmmPower.getAttributes();

            	// add a blank row to the data table
            	int row = resultsData.addRow();

            	// fill in the columns
            	int col = 0;
            	Node alphaNode = attrs.getNamedItem("alpha");
            	if (alphaNode != null) 
            	{
            		resultsData.setCell(row, col, Double.parseDouble(alphaNode.getNodeValue()), 
            				alphaNode.getNodeValue(), null);
            	}
            	col++;
            	
            	Node testNode = attrs.getNamedItem("test");
            	if (testNode != null) 
            	{
            		resultsData.setCell(row, col, testNode.getNodeValue(), 
            				formatTestName(testNode.getNodeValue()), null);
            	}
            	col++;
            	
            	Node powerMethodNode = attrs.getNamedItem("powerMethod");
            	if (powerMethodNode != null) 
            	{
            		resultsData.setCell(row, col, powerMethodNode.getNodeValue(), 
            				formatPowerMethodName(powerMethodNode.getNodeValue()), null);
            	}
            	col++;
            	
            	Node quantileNode = attrs.getNamedItem("quantile");
            	if (quantileNode != null) 
            	{
            		resultsData.setCell(row, col, Double.parseDouble(quantileNode.getNodeValue()), 
            				quantileNode.getNodeValue(), null);
            	}
            	col++;
            	
            	Node sigmaScaleNode = attrs.getNamedItem("sigmaScale");
            	if (sigmaScaleNode != null) 
            	{
            		resultsData.setCell(row, col, Double.parseDouble(sigmaScaleNode.getNodeValue()), 
            				sigmaScaleNode.getNodeValue(), null);
            	}
            	col++;
            	
            	Node betaScaleNode = attrs.getNamedItem("betaScale");
            	if (betaScaleNode != null) 
            	{
            		resultsData.setCell(row, col, Double.parseDouble(betaScaleNode.getNodeValue()), 
            				betaScaleNode.getNodeValue(), null);
            	}
            	col++;

            	Node sampleSizeNode = attrs.getNamedItem("sampleSize");
            	if (sampleSizeNode != null) 
            	{
            		resultsData.setCell(row, col, Integer.parseInt(sampleSizeNode.getNodeValue()), 
            				sampleSizeNode.getNodeValue(), null);
            	}
            	col++;
            	
            	Node nominalPowerNode = attrs.getNamedItem("nominalPower");
            	if (nominalPowerNode != null) 
            	{
            		resultsData.setCell(row, col, Double.parseDouble(nominalPowerNode.getNodeValue()), 
            				formatDouble(nominalPowerNode.getNodeValue()), null);
            	}
            	col++;
            	
            	Node actualPowerNode = attrs.getNamedItem("actualPower");
            	if (actualPowerNode != null) 
            	{
            		resultsData.setCell(row, col, Double.parseDouble(actualPowerNode.getNodeValue()), 
            				formatDouble(actualPowerNode.getNodeValue()), null);
            	}
            }
            
        	if (showTable)
        	{
        		resultsTable.draw(resultsData);
        	}
        	
        	if (showCurve)
        	{
        		showCurveResults();
        	}
            
        }
        catch (Exception e)
        {
        	showError(e.getMessage());
        }
    }
    
    private void showCurveResults()
    {
    	DataView view = DataView.create(resultsData);
    	view.setRows(0, 9);
    	view.setColumns(new int[] {1, 8});
    	LineChart chart = new LineChart(view, createCurveOptions());
    	//curveGrid.resize(1, 1);
    	curveGrid.setWidget(0, 0, chart);
    	//chart.draw(view, createCurveOptions());
    }
    
	private LineChart.Options createCurveOptions() {
	    LineChart.Options options = LineChart.Options.create();
	    options.setWidth(400);
	    options.setHeight(240);
	    //options.set3D(true);
	    options.setTitle("Power Curve");
	    options.setShowCategories(true);
	    options.setSmoothLine(true);
	    return options;
	  }
    
    private String formatPowerMethodName(String name)
    {
    	return name; // TODO
    }
    
    private String formatTestName(String name)
    {
    	return name; // TODO
    }
    
    private String formatDouble(String valueStr)
    {
    	try
    	{
    		double value = Double.parseDouble(valueStr);
    		return doubleFormatter.format(value);
    	}
    	catch (Exception e)
    	{
    		return valueStr;
    	}
    }
    
    private void sendPowerRequest()
    {
    	showWorkingDialog();
    	String requestEntityBody = manager.getPowerRequestXML();
    	Window.alert(requestEntityBody);
    	RequestBuilder builder = null;
    	switch(manager.getSolvingFor())
    	{
    	case POWER:
    		builder = new RequestBuilder(RequestBuilder.POST, POWER_URL);
    		break;
    	case SAMPLE_SIZE:
    		builder = new RequestBuilder(RequestBuilder.POST, SAMPLE_SIZE_URL);
    		break;
    	case EFFECT_SIZE:
    		builder = new RequestBuilder(RequestBuilder.POST, EFFECT_SIZE_URL);
    		break;
    	}

    	try 
    	{
    		builder.setHeader("Content-Type", "text/xml");
    		builder.sendRequest(requestEntityBody, new RequestCallback() {

    			public void onError(Request request, Throwable exception) 
    			{
    				hideWorkingDialog();
    				showError("Calculation failed: " + exception.getMessage());	
    			}

    			public void onResponseReceived(Request request, Response response) 
    			{
    				hideWorkingDialog();
    				if (STATUS_CODE_OK == response.getStatusCode() ||
    						STATUS_CODE_CREATED == response.getStatusCode()) 
    				{
    					showResults(response.getText());
    				} 
    				else 
    				{
    					showError("Calculation failed: [HTTP STATUS " + 
    					        response.getStatusCode() + "] " + response.getText());
    				}
    			}
    		});
    	} 
    	catch (Exception e) 
    	{
			hideWorkingDialog();
			showError("Failed to send the request: " + e.getMessage());
    	}
    }

	@Override
	public void onShowCurve(boolean showCurve, XAxisType axis,
			CurveSubset[] curveSubsets)
	{
		this.showCurve = true;
		this.xaxisType = axis;
		this.curveSubsets = curveSubsets;
		resultsCurvePanel.setVisible(showCurve);
	}

	@Override
	public void onShowTable(boolean showTable)
	{
		this.showTable = showTable;
		resultsTablePanel.setVisible(showTable);
		
	}    
    
}
