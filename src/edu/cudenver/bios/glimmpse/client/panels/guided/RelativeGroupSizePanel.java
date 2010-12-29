package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.PredictorsListener;
import edu.cudenver.bios.glimmpse.client.listener.RelativeGroupSizeListener;
import edu.cudenver.bios.glimmpse.client.listener.VariabilityListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class RelativeGroupSizePanel extends WizardStepPanel
implements PredictorsListener, VariabilityListener, CovariateListener
{
	protected static final int MAX_RELATIVE_SIZE = 10;
    // data table to display possible groups
    protected FlexTable groupSizesTable = new FlexTable();
    // listeners for relative size events
    protected ArrayList<RelativeGroupSizeListener> listeners = new ArrayList<RelativeGroupSizeListener>();
    protected boolean hasCovariate = false;
    protected double mean = 0;
    protected double variance = 1;
    
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
		groupSizesTable.clear();
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
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ESSENCE_MATRIX);

		// build the "fixed" cell means matrix.  Essentially, this is an identity matrix
		// with rows & columns equal to the number of study sub groups
		int size = groupSizesTable.getRowCount() -1;  // skip header

		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, size, size);

		// identity matrix
		for(int row = 0; row < size; row++)
		{
			buffer.append("<r>");
			for(int col = 0; col < size; col++)
			{
				buffer.append("<c>");
				if (row == col)
					buffer.append(1);
				else
					buffer.append(0);
				buffer.append("</c>");
			}
			buffer.append("</r>");
		}
		
		// close tag
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		
		// build row meta data list
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW_META_DATA);

		for(int row = 1; row <= size; row++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(row, 0);
			if (lb != null) buffer.append("<r ratio='" + lb.getItemText(lb.getSelectedIndex()) + "' />");
		}

		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW_META_DATA);
		
		// check if there is a random matrix
		if (hasCovariate)
		{
			// list random column meta data
			buffer.append("<randomColumnMetaData>");
			buffer.append("<c mean='");
			buffer.append(mean);
			buffer.append("' variance='");
			buffer.append(variance);
			buffer.append("'></c></randomColumnMetaData>");

			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, size, 1);
			for(int row = 0; row < size; row++)
			{
				buffer.append("<r><c>1</c></r>");
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
			
		}
		
		// close tag for essence matrix
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ESSENCE_MATRIX);
		
		return buffer.toString();
	}
	
	@Override
	public void onExit()
	{
		ArrayList<Integer> relativeSizes = new ArrayList<Integer>();
		for(int i = 1; i < groupSizesTable.getRowCount(); i++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(i, 0);
			relativeSizes.add(lb.getSelectedIndex()+1);
		}
		for(RelativeGroupSizeListener listener: listeners)
		{
			listener.onRelativeGroupSize(relativeSizes);
		}
	}

	@Override
	public void onOutcomeVariance(List<Double> variancesOfOutcomes)
	{
		// no action needed
	}

	@Override
	public void onCovariateVariance(double varianceOfCovariate)
	{
		this.variance = varianceOfCovariate;
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}
}
