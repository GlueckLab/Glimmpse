package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class InputWizardPanel extends Composite
{
	DeckPanel wizardDeck = new DeckPanel();

	public InputWizardPanel()
	{
		VerticalPanel panel = new VerticalPanel();

		wizardDeck.add(new EffectSizeBarChart());
		wizardDeck.add(new LearCovariancePanel());
		wizardDeck.showWidget(0);
		
		panel.add(new Button("See more prototypes", new ClickHandler() {
				public void onClick(ClickEvent e)
				{
					if (wizardDeck.getVisibleWidget() == 0)
						wizardDeck.showWidget(1);
					else
						wizardDeck.showWidget(0);
				}
		}));
		panel.add(wizardDeck);
		initWidget(panel);
	}
	

	
}
