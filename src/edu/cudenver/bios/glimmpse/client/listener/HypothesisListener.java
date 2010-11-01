package edu.cudenver.bios.glimmpse.client.listener;

public interface HypothesisListener
{
	public void onMainEffectsHypothesis(String predictor);
	
	public void onInteractionHypothesis(String predictor, String interactionPredictor);
}
