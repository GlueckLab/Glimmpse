package edu.cudenver.bios.glimmpse.client.listener;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.visualization.client.DataTable;

public interface PredictorsListener
{
	public void onPredictors(HashMap<String,ArrayList<String>> predictorMap, DataTable groups);
}
