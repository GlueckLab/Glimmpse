package edu.cudenver.bios.glimmpse.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.http.client.URL;
import com.google.gwt.visualization.client.DataTable;

public class ChartRequestBuilder
{
	private static final String CURVE_URL = "/webapps/chart/scatter";
	private static final String CURVE_3D_URL = "/webapps/chart/scatter3d";
	private static final String LEGEND_URL = "/webapps/chart/legend";
	
	public enum AxisType {
		NONE,
		SAMPLE_SIZE,
		BETA_SCALE,
		SIGMA_SCALE
	};
	
	public enum CurveType {
		TEST,
		TOTAL_N,
		BETA_SCALE,
		SIGMA_SCALE,
		ALPHA,
		POWER_METHOD,
		QUANTILE
	};
	
	// labeling options
	protected String title = null;
	protected ArrayList<String> axisLabels = new ArrayList<String>();
	protected ArrayList<String> legendLabels = new ArrayList<String>();
	protected ArrayList<String> lineStyles = new ArrayList<String> ();

	// indicates which value is represented on the X axis for 2D plots,
	// and the X and Y axes for 3D plots
	protected AxisType XAxisType = AxisType.SAMPLE_SIZE;
	protected AxisType YAxisType = AxisType.NONE; // for 3D plots only - coming soon! TODO
	
	// indicates what the curves represent
	protected CurveType curveType = CurveType.BETA_SCALE;
	
	// curve group description
	protected String test = null;
	protected double alpha = Double.NaN;
	protected int sampleSize = -1;
	protected double betaScale = Double.NaN;
	protected double sigmaScale = Double.NaN;
	protected String powerMethod = null;
	protected double quantile = Double.NaN;
	
	protected DataTable powerResults = null;
	protected String queryString = null;
	
	// hash map used to define subgroups of rows in the power results table which 
	// correspond to data series in the curve
	protected HashMap<String,ArrayList<Integer>> groups = new HashMap<String,ArrayList<Integer>>();
	
	public ChartRequestBuilder() {}
	
	public void reset()
	{
		
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	public void addAxisLabel(String label)
	{
		axisLabels.add(label);
	}

	public void addLegendLabel(String label)
	{
		legendLabels.add(label);
	}
	
	public void addLineStyle(int thickness, int dashLength, int spaceLength)
	{
		lineStyles.add(thickness + "," + dashLength + "," + spaceLength);
	}
	
	public void setXAxisType(AxisType xAxisType)
	{
		XAxisType = xAxisType;
	}

	public void setYAxisType(AxisType yAxisType)
	{
		YAxisType = yAxisType;
	}

	public void setCurveType(CurveType curveType)
	{
		this.curveType = curveType;
	}

	public void setTest(String test)
	{
		this.test = test;
	}

	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}

	public void setSampleSize(int sampleSize)
	{
		this.sampleSize = sampleSize;
	}

	public void setBetaScale(double betaScale)
	{
		this.betaScale = betaScale;
	}

	public void setSigmaScale(double sigmaScale)
	{
		this.sigmaScale = sigmaScale;
	}

	public void setPowerMethod(String powerMethod)
	{
		this.powerMethod = powerMethod;
	}

	public void setQuantile(double quantile)
	{
		this.quantile = quantile;
	}

	public void loadData(DataTable data)
	{
		this.powerResults = data;
		defineGroups();
		queryString = buildQueryString();
	}
	
	private void defineGroups()
	{	
		groups.clear();
		for(int row = 0; row < powerResults.getNumberOfRows(); row++)
		{
			StringBuffer groupKey = new StringBuffer();
			
			if (curveType != CurveType.TEST && test != null)
			{
				int col = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_TEST);
				String currentTest = powerResults.getValueString(row, col);
				if (!currentTest.equals(test)) continue;
				groupKey.append("test=" +  currentTest + "&");
			}
			
			if (curveType != CurveType.TOTAL_N && XAxisType != AxisType.SAMPLE_SIZE && sampleSize > 0)
			{
				int col = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_SAMPLE_SIZE);
				int currentSampleSize = powerResults.getValueInt(row, col);
				if (sampleSize != currentSampleSize) continue;
				groupKey.append("n=" +  sampleSize + "&");
			}
			
			if (curveType != CurveType.BETA_SCALE && XAxisType != AxisType.BETA_SCALE && !Double.isNaN(betaScale))
			{
				int col = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_BETA_SCALE);
				double currentBetaScale = powerResults.getValueDouble(row, col);
				if (betaScale != currentBetaScale) continue;
				groupKey.append("betaScale=" +  betaScale + "&");
			}
			
			if (curveType != CurveType.SIGMA_SCALE && XAxisType != AxisType.SIGMA_SCALE && !Double.isNaN(sigmaScale))
			{
				int col = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_BETA_SCALE);
				double currentSigmaScale = powerResults.getValueDouble(row, col);
				if (sigmaScale != currentSigmaScale) continue;
				groupKey.append("sigmaScale=" +  currentSigmaScale + "&");
			}

			if (curveType != CurveType.ALPHA && !Double.isNaN(alpha))
			{
				int col = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_ALPHA);
				double currentAlpha = powerResults.getValueDouble(row, col);
				if (alpha != currentAlpha) continue;
				groupKey.append("alpha=" +  currentAlpha + "&");
			}
			
			if (curveType != CurveType.POWER_METHOD && powerMethod != null && !powerMethod.isEmpty())
			{
				int col = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_POWER_METHOD);
				String currentPowerMethod =  powerResults.getValueString(row, col);
				if (!powerMethod.equals(currentPowerMethod)) continue;
				groupKey.append("method=" +  currentPowerMethod + "&");
			}

			if (curveType != CurveType.QUANTILE && !Double.isNaN(quantile))
			{
				int col = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_POWER_METHOD);
				double currentQuantile = powerResults.getValueDouble(row, col);
				groupKey.append("quantile=" +  currentQuantile + "&");
			}			

			ArrayList<Integer> groupRows = groups.get(groupKey.toString());
			if (groupRows == null) 
			{
				groupRows = new ArrayList<Integer>();
				groups.put(groupKey.toString(), groupRows);
			}
			groupRows.add(row);
		}
	}
	
	public String buildChartRequestURL()
	{
		StringBuffer uri = new StringBuffer();
		if (powerResults != null)
		{
			uri.append(CURVE_URL);
			uri.append("?");
			uri.append(queryString);
			uri.append(buildSizeQueryString(300, 300));
		}
		return uri.toString();
	}
	
	public String buildLegendRequestURL()
	{
		StringBuffer uri = new StringBuffer();
		if (powerResults != null)
		{
			uri.append(LEGEND_URL);
			uri.append("?");
			uri.append(queryString);
			uri.append(buildSizeQueryString(300, 300));
		}
		return uri.toString();
	}
	
	private String buildQueryString()
	{
		// build the full chart xml
		StringBuffer buffer = new StringBuffer();

		// determine which column of the data table corresponds to the xAxis
		int xColumn = -1;
		switch(XAxisType)
		{
		case SAMPLE_SIZE:
			xColumn = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_SAMPLE_SIZE);
			break;
		case BETA_SCALE:
			xColumn = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_BETA_SCALE);
			break;
		case SIGMA_SCALE:
			xColumn = powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_SIGMA_SCALE);
			break;
		}

		appendPipeDelimitedList(buffer, "chxl=", axisLabels);
		appendPipeDelimitedList(buffer, "chdl=", legendLabels);
		appendPipeDelimitedList(buffer, "chls=", lineStyles);

		// build the data series x..x|y...y|z...z
		buffer.append("&chd=t:");
		boolean firstSeries = true;
		for(ArrayList<Integer> groupRows: groups.values())
		{
			if (!firstSeries) buffer.append("|"); 
			firstSeries = false;

			StringBuffer xBuffer = new StringBuffer();
			StringBuffer yBuffer = new StringBuffer();
			boolean first = true;
			for(Integer row: groupRows)
			{

				if (!first)
				{
					xBuffer.append(",");
					yBuffer.append(",");
				}
				first =false;
				xBuffer.append(powerResults.getValueDouble(row, xColumn));
				yBuffer.append(powerResults.getValueDouble(row, 
						powerResults.getColumnIndex(GlimmpseConstants.COLUMN_NAME_ACTUAL_POWER)));
			}
			buffer.append(xBuffer);
			buffer.append("|");
			buffer.append(yBuffer);
		}

		return buffer.toString();
	}
	
	private void appendPipeDelimitedList(StringBuffer buffer, String qParam, ArrayList<String> labels)
	{
		if (labels.size() > 0)
		{
			boolean first = true;
			for(String label: labels)
			{
				if (first)
				{
					buffer.append(qParam);
					first = false;
				}
				else
				{
					buffer.append("|");
				}
				buffer.append(URL.encode(label));
			}
		}	
	}
	
	private String buildSizeQueryString(int width, int height)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("&chs=");
		buffer.append(width);
		buffer.append("x");
		buffer.append(height);
		return buffer.toString();
	}
	
	
}
