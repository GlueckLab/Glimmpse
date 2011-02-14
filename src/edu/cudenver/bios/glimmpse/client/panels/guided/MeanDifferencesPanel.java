package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class MeanDifferencesPanel extends WizardStepPanel
implements OutcomesListener, PredictorsListener, ChangeHandler,
CovariateListener
{
	protected FlexTable meansTable = new FlexTable();

	protected boolean hasCovariate = false;
	List<String> outcomes = null;
	HashMap<String, ArrayList<String>> predictorMap;
	protected DataTable groups = null;

	protected HTML errorHTML = new HTML();
	
	public MeanDifferencesPanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();

		HTML header = new HTML(Glimmpse.constants.meanDifferenceTitle());
		HTML description = new HTML(Glimmpse.constants.meanDifferenceDescription());

		panel.add(header);
		panel.add(description);
		panel.add(meansTable);
		panel.add(errorHTML);

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        meansTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
		initWidget(panel);
	}

	@Override
	public void reset()
	{
		meansTable.removeAllRows();
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

	}

	public void onEnter()
	{
		if (changed)
		{
			changed = false;
			reset();
	    	if (predictorMap.size() > 0 && outcomes.size() > 0)
	    	{
	    		// create the table header row
	    		meansTable.getRowFormatter().setStyleName(0, 
	    				GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
	    		int col = 0;
	    		for(;col < groups.getNumberOfColumns(); col++)
	    		{
	    			meansTable.setWidget(0, col, new HTML(groups.getColumnLabel(col)));
	    		}
				for(String outcome: outcomes)
				{
					meansTable.setWidget(0, col, new HTML(outcome));
					col++;
				}
				
				// now fill in the group values, and add "0" text boxes for entering the means
	    		for(int row = 0; row < groups.getNumberOfRows(); row++)
	    		{
	    			meansTable.getRowFormatter().setStyleName(row+1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
	    			for(col = 0; col < groups.getNumberOfColumns(); col++)
	    			{
	    				meansTable.setWidget(row+1, col, new HTML(groups.getValueString(row, col)));
	    			}
					for(String outcome: outcomes)
					{
						TextBox tb = new TextBox();
						tb.setText("0");
						tb.addChangeHandler(this);
						meansTable.setWidget(row+1, col, tb);
						col++;
					}
	    		}
	    	}
		}
	}

	// TODO: remove
	@Override
	public void onExit()
	{
		Window.alert(toXML());
	}
	
	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap,
			DataTable groups)
	{
		this.predictorMap = predictorMap;
		this.groups = groups; 
		changed = true;
	}

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		this.outcomes = outcomes;
		changed = true;
	}

	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip && complete)
		{
			XMLUtilities.fixedRandomMatrixOpenTag(buffer, GlimmpseConstants.MATRIX_BETA, false);

			int columns = meansTable.getCellCount(0);
			int rows = meansTable.getRowCount();
			
			// main effects hypothesis
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, 
					groups.getNumberOfRows(), outcomes.size());
			for(int row = 1; row < rows; row++)
			{
				buffer.append("<r>");
				for(int col = groups.getNumberOfColumns(); col < columns; col++)
				{
					buffer.append("<c>");
					buffer.append(((TextBox) meansTable.getWidget(row, col)).getText());
					buffer.append("</c>");
				}
				buffer.append("</r>");
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

			if (hasCovariate)
			{
				XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, 1, columns);
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
				for(int col = 0; col < columns; col++)
				{
					XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
					buffer.append(1);
					XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
				}
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		}
		return buffer.toString();
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}
	
	@Override
	public void onChange(ChangeEvent event)
	{
		TextBox tb = (TextBox) event.getSource();
		try
		{
			Double.parseDouble(tb.getText());
			TextValidation.displayOkay(errorHTML, "");
		}
		catch (NumberFormatException nfe)
		{
			TextValidation.displayError(errorHTML, Glimmpse.constants.errorInvalidNumber());
			tb.setText("0");
		}
	}
}
