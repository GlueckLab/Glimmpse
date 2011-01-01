package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.XMLUtilities;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.listener.VariabilityListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class DesignPanel extends WizardStepPanel
implements MatrixResizeListener, CovariateListener, VariabilityListener
{    	
	private static final String STYLE_ROWMD_PANEL = "rowMetaDataPanel";
	private static final String STYLE_ROWMD_DATA = "rowMetaDataPanelData";
	private static final int MAX_RATIO = 10;
    protected ResizableMatrix essenceFixed = new ResizableMatrix(GlimmpseConstants.MATRIX_DESIGN_FIXED,
			GlimmpseConstants.DEFAULT_N, 
			GlimmpseConstants.DEFAULT_Q, "0", Glimmpse.constants.matrixCategoricalEffectsLabel());
   	protected FlexTable rowMDTable;
    
   	boolean hasCovariate = false;
   	double mean = 0;
   	double variance = Double.NaN;
   	
	public DesignPanel()
	{
		super();
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.matrixDesignTitle());
        HTML description = new HTML(Glimmpse.constants.matrixDesignDescription());
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(createEssenceMatrixPanel());
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // add style
        initWidget(panel);
	}
	
	/**
	 * Create a drop down list with relative group size entries
	 * 
	 * @return listbox widget
	 */
    private ListBox createRowMDListBox()
    {
    	ListBox list = new ListBox();
    	list.setStyleName(GlimmpseConstants.STYLE_MATRIX_CELL);
    	for(int i = 1; i <= MAX_RATIO; i++)
    	{
    		list.addItem(Integer.toString(i));
    	}		
    	return list;
    }
    
    private VerticalPanel createEssenceMatrixPanel()
    {
    	VerticalPanel panel = new VerticalPanel();			

    	// build the row meta data panel
    	VerticalPanel rowMDPanel = new VerticalPanel();
    	rowMDPanel.add(new HTML(Glimmpse.constants.relativeGroupSizeTableColumn()));
    	rowMDTable = new FlexTable();
    	for(int r = 0; r < GlimmpseConstants.DEFAULT_N; r++)
    	{
    		rowMDTable.setWidget(r, 0, createRowMDListBox());
    	}
    	rowMDPanel.add(rowMDTable);
    	// layout the matrices and row meta data
    	Grid layoutGrid = new Grid(1,2);
    	layoutGrid.setWidget(0, 0, rowMDPanel);
    	layoutGrid.setWidget(0, 1, essenceFixed);
    	layoutGrid.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_TOP);
    	// add listeners
    	essenceFixed.addMatrixResizeListener(this);

        // layout the overall panel
        panel.add(layoutGrid);
    	
        // set style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    	rowMDPanel.setStyleName(STYLE_ROWMD_PANEL);
    	rowMDTable.setStyleName(STYLE_ROWMD_DATA);
    	return panel;
    }
    
	public void reset()
	{
		essenceFixed.reset(GlimmpseConstants.DEFAULT_N, GlimmpseConstants.DEFAULT_Q);
		rowMDTable.removeAllRows();
    	for(int r = 0; r < GlimmpseConstants.DEFAULT_N; r++)
    	{
    		rowMDTable.setWidget(r, 0, createRowMDListBox());
    	}
	}
	
	@Override
    public void onHasCovariate(boolean hasCovariate) 
    {
    	this.hasCovariate = hasCovariate;
    }
    
	public void onRows(String name, int newRows) 
	{
		int currentRows = rowMDTable.getRowCount();
		if (currentRows < newRows)
		{
			for(int r = currentRows; r < newRows; r++)
			{
				rowMDTable.setWidget(r, 0, createRowMDListBox());
			}
		}
		else if (currentRows > newRows)
		{
			for(int r = currentRows-1; r >= newRows; r--)
			{
				rowMDTable.removeRow(r);
			}
		}
	}
	
	public void onColumns(String name, int newCols) 
	{
		// no action needed
	}
	
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseInteger(value, 0, Integer.MAX_VALUE);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidAlpha());
    	}
	}
	
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
	}
	
	
	public void addMatrixResizeListener(MatrixResizeListener listener)
	{
		essenceFixed.addMatrixResizeListener(listener);
	}
	
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ESSENCE_MATRIX);
		// list row meta data
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW_META_DATA);
		for(int r = 0; r < rowMDTable.getRowCount(); r++)
		{
			ListBox lb = (ListBox) rowMDTable.getWidget(r, 0);
			buffer.append("<r ratio='" + lb.getItemText(lb.getSelectedIndex()) + "' />");
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW_META_DATA);
		// if controlling for a covariate, add meta info for the random column
		if (hasCovariate)
		{
			// list random column meta data
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_RANDOM_COLUMN_META_DATA);
			buffer.append("<c mean='");
			buffer.append(mean);
			buffer.append("' variance='");
			buffer.append(variance);
			buffer.append("'></c>");
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_RANDOM_COLUMN_META_DATA);
		}
		// add fixed effects matrix
		buffer.append(essenceFixed.toXML(GlimmpseConstants.MATRIX_FIXED));
		if (hasCovariate)
		{
			int rows = essenceFixed.getRowDimension();
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, rows, 1);
			for(int i = 0; i < rows; i++) buffer.append("<r><c>1</c></r>");
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		// if the user is controlling for a baseline covariate, add the random meta data
		// and random effects matrix to the output

		buffer.append("</essenceMatrix>");
		return buffer.toString();
	}

	@Override
	public void loadFromNode(Node node)
	{
		if (GlimmpseConstants.TAG_ESSENCE_MATRIX.equals(node.getNodeName()))
		{
			NodeList children = node.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				String childName = child.getNodeName();
				if (GlimmpseConstants.TAG_MATRIX.equals(childName))
				{
					NamedNodeMap attrs = child.getAttributes();
					Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
					if (nameNode != null)
					{
						if (GlimmpseConstants.MATRIX_FIXED.equals(nameNode.getNodeValue()))
						{
							essenceFixed.loadFromDomNode(child);
						}
					}
				}
				else if (GlimmpseConstants.TAG_ROW_META_DATA.equals(childName))
				{
					loadRowMetaDataFromNode(child);
				}
			}
			// double check that the size of the row meta data equals the #rows in the fixed matrix
			// if a discrepancy occurs, we adjust the size of the row meta data
			int matrixRows = essenceFixed.getRowDimension();
			int metaDataRows = rowMDTable.getRowCount();
			if (matrixRows > metaDataRows)
			{
				for(int row = metaDataRows; row < matrixRows; row++)
				{
					rowMDTable.setWidget(row, 0, createRowMDListBox());
				}
			}
			else if (matrixRows < metaDataRows)
			{
				for(int row = metaDataRows-1; row >= matrixRows; row--)
				{
					rowMDTable.removeRow(row);
				}
			}
		}
	}

	public void loadRowMetaDataFromNode(Node node)
	{
		reset();
		NodeList children = node.getChildNodes();
		for(int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			NamedNodeMap attrs = child.getAttributes();
			Node ratioNode = attrs.getNamedItem(GlimmpseConstants.ATTR_RATIO);
			ListBox tb = createRowMDListBox();
			if (ratioNode != null)
			{
				try
				{
					int value = Integer.parseInt(ratioNode.getNodeValue());
					if (value >= 1 || value <= MAX_RATIO) tb.setSelectedIndex(value-1);
				}
				catch (NumberFormatException nfe) 
				{ 
					// catch, but no action needed for this exception
				}
				
			}
			rowMDTable.setWidget(i, 0, tb);
		}
	
	}
	
	@Override
	public void onOutcomeVariance(List<Double> variancesOfOutcomes)
	{
		// no action needed for this panel
	}

	@Override
	public void onCovariateVariance(double varianceOfCovariate)
	{
		this.variance = varianceOfCovariate;
	}

}
