package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.listener.RelativeGroupSizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class RelativeGroupSizePanel extends WizardStepPanel
implements PredictorsListener, CovariateListener
{
	protected static final int MAX_RELATIVE_SIZE = 10;
    // data table to display possible groups
    protected FlexTable groupSizesTable = new FlexTable();
    // listeners for relative size events
    protected ArrayList<RelativeGroupSizeListener> listeners = new ArrayList<RelativeGroupSizeListener>();
    
	public RelativeGroupSizePanel()
	{
		super();
		complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.relativeGroupSizeTitle());
        HTML description = new HTML(Glimmpse.constants.relativeGroupSizeDescription());     
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(groupSizesTable);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        groupSizesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        
        initWidget(panel);
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap,
			DataTable groups)
	{
    	reset();
    	if (predictorMap.size() > 0)
    	{
    		groupSizesTable.getRowFormatter().setStyleName(0, 
    				GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
    		groupSizesTable.setWidget(0, 0, new HTML(Glimmpse.constants.relativeGroupSizeTableColumn()));
    		for(int col = 0; col < groups.getNumberOfColumns(); col++)
    		{
    			groupSizesTable.setWidget(0, col+1, new HTML(groups.getColumnLabel(col)));
    		}
    		for(int row = 0; row < groups.getNumberOfRows(); row++)
    		{
    			groupSizesTable.setWidget(row+1, 0, createGroupSizeListBox());
    			groupSizesTable.getRowFormatter().setStyleName(row+1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
    			for(int col = 0; col < groups.getNumberOfColumns(); col++)
    			{
    				groupSizesTable.setWidget(row+1, col+1, new HTML(groups.getValueString(row, col)));
    			}
    		}
    	}
	}

	private ListBox createGroupSizeListBox()
	{
		ListBox lb = new ListBox();
		for(int i = 1; i <= MAX_RELATIVE_SIZE; i++) lb.addItem(Integer.toString(i));
		return lb;
	}
	
	public void addRelativeGroupSizeListener(RelativeGroupSizeListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMean(double mean)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVariance(double variance)
	{
		// TODO Auto-generated method stub
		
	}
}
