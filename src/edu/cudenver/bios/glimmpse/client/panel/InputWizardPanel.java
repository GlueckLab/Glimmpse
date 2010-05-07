package edu.cudenver.bios.glimmpse.client.panel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;

public class InputWizardPanel extends Composite
{
    protected DeckPanel wizardSteps = new DeckPanel();
    
    public InputWizardPanel()
    {
        
        initWidget(wizardSteps);
    }
}
