package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.panels.StepsLeftPanel;


public class InputWizardPanel extends Composite
{
    // top steps left panel
    protected StepsLeftPanel stepsLeftPanel = new StepsLeftPanel();
    
    // deck panel containing all steps in the input wizard
	DeckPanel wizardDeck = new DeckPanel();

	public InputWizardPanel()
	{
		HorizontalPanel panel = new HorizontalPanel();
		VerticalPanel contentPanel = new VerticalPanel();
		
		wizardDeck.add(new OutcomesPanel());
		wizardDeck.add(new EffectSizeBarChart());
		wizardDeck.add(new LearCovariancePanel());

		wizardDeck.showWidget(0);
		
		contentPanel.add(new Button("See more prototypes", new ClickHandler() {
				public void onClick(ClickEvent e)
				{
					if (wizardDeck.getVisibleWidget() == 0)
						wizardDeck.showWidget(1);
					else
						wizardDeck.showWidget(0);
				}
		}));
		
		contentPanel.add(wizardDeck);
		
		panel.add(stepsLeftPanel);		
		panel.add(contentPanel);
		initWidget(panel);
	}
	

	
}
