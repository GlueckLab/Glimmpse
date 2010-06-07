package edu.cudenver.bios.glimmpse.client.listener;

import java.util.ArrayList;
import java.util.HashMap;

public interface PredictorsListener
{
	public void onPredictors(HashMap predictorMap);
	
	public void onCategories(String predictor, ArrayList<String> categories);
}
