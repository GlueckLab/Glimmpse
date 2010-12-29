package edu.cudenver.bios.glimmpse.client.panels.guided;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class VariabilityScalePanel extends WizardStepPanel
{
    protected CheckBox scaleCheckBox = new CheckBox();
    
	public VariabilityScalePanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.variabilityScaleTitle());
        HTML description = new HTML(Glimmpse.constants.variabilityScaleDescription());
        // create the beta scale checkbox - asks if the user wants to test 0.5,1,and 2 times the estimated
        // mean difference
        HorizontalPanel checkBoxContainer = new HorizontalPanel();
        checkBoxContainer.add(scaleCheckBox);
        checkBoxContainer.add(new HTML(Glimmpse.constants.variabilityScaleAnswer()));

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(new HTML(Glimmpse.constants.variabilityScaleQuestion()));
        panel.add(checkBoxContainer);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		initWidget(panel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reset()
	{
		scaleCheckBox.setValue(false);
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_SIGMA_SCALE_LIST);
		buffer.append("<v>1</v>");
		if (scaleCheckBox.getValue())
		{
			buffer.append("<v>0.5</v><v>2</v>");
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_SIGMA_SCALE_LIST);
		return buffer.toString();
	}

}
