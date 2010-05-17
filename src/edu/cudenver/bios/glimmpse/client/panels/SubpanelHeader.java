package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;

public class SubpanelHeader extends Composite
{
    protected PopupPanel detailsPanel = null;
    protected HTML detailsHTML;
    protected HTML headerHTML;
    protected HTML helpHTML = new HTML();
    
    public SubpanelHeader(String title, String details)
    {
        HorizontalPanel panel = new HorizontalPanel();
        
        // create the header text
        headerHTML = new HTML(title);
        // create the popup text
        if (details != null && !details.isEmpty())
        {
            detailsHTML = new HTML(details);
            detailsHTML.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent e)
                {
                    detailsPanel.hide();
                }
            });
            detailsPanel = new PopupPanel();
            detailsPanel.add(detailsHTML);
            
            helpHTML.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent e)
                {
                    detailsPanel.showRelativeTo(helpHTML);
                }
            });
        }
        panel.add(headerHTML);
        panel.add(helpHTML);
        
        headerHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        headerHTML.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        helpHTML.setStyleName("wizardStepHelp");
        helpHTML.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        //panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        //panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);        

        initWidget(panel);
    }
}
