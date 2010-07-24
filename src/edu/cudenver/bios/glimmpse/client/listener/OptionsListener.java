package edu.cudenver.bios.glimmpse.client.listener;

public interface OptionsListener
{
	public enum XAxisType {
		TOTAL_N,
		EFFECT_SIZE,
		VARIANCE
	};
	
	public enum CurveSubset {
		TOTAL_N,
		EFFECT_SIZE,
		VARIANCE,
		TEST,
		POWER_METHOD,
		QUANTILE
	};
	
	public void onShowTable(boolean showTable);
	
	public void onShowCurve(boolean showCurve, XAxisType axis,
			CurveSubset[] curveSubsets);
}
