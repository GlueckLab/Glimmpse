package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;

public class MatrixDisplayPanel extends Composite
{
	protected FlexTable tableOfMatrices = new FlexTable();
	
	public MatrixDisplayPanel()
	{
		VerticalPanel panel = new VerticalPanel();

		panel.add(tableOfMatrices);
		
		// style 
		tableOfMatrices.setBorderWidth(1);
		
		// initialize
		initWidget(panel);
	}
	
	public void loadFromXML(String matrixXML)
	{
		Document doc = XMLParser.parse(matrixXML);
		
		int row = 0;
		
		Node paramsNode = doc.getFirstChild();
		if (paramsNode != null)
		{
			NodeList children = paramsNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if (GlimmpseConstants.TAG_ESSENCE_MATRIX.equals(child.getNodeName()))
				{
					tableOfMatrices.setWidget(row, 0, new HTML("X \"Essence\""));
					tableOfMatrices.setWidget(row++, 1, buildFixedRandomMatrixGrid(child, "*"));
				}
				else if (GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX.equals(child.getNodeName()))
				{
					// TODO: matrix names
					NamedNodeMap attrs = child.getAttributes();
					Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
					tableOfMatrices.setWidget(row, 0, new HTML(nameNode.getNodeValue()));
					tableOfMatrices.setWidget(row++, 1, buildFixedRandomMatrixGrid(child, "1"));
				}
				else if (GlimmpseConstants.TAG_MATRIX.equals(child.getNodeName()))
				{
					NamedNodeMap attrs = child.getAttributes();
					Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
					if (nameNode != null)
					{
						tableOfMatrices.setWidget(row, 0, new HTML(nameNode.getNodeValue()));
					}

					FlexTable table = new FlexTable();
					table.setBorderWidth(1);
					addMatrixData(child, table, 0, 0);
					tableOfMatrices.setWidget(row++, 1, table);
				}
			}
		}
	}
	
	public void reset()
	{
		tableOfMatrices.removeAllRows();
	}
	
    public FlexTable buildFixedRandomMatrixGrid(Node matrixNode, String randomValue)
    {
    	FlexTable table = new FlexTable();
    	table.setBorderWidth(1);
    	if (matrixNode != null)
    	{
    		NamedNodeMap frAttrs = matrixNode.getAttributes();
    		boolean combineHorizontal = true;
    		Node combineHorizontalNode = frAttrs.getNamedItem(GlimmpseConstants.ATTR_COMBINE_HORIZONTAL);
    		if (combineHorizontalNode != null) combineHorizontal = Boolean.parseBoolean(combineHorizontalNode.getNodeValue());   		
    		
    		Node fixedNode = null;
    		Node randomNode = null;
    		NodeList children = matrixNode.getChildNodes();
    		for(int i = 0; i < children.getLength() && (fixedNode == null || randomNode == null); i++)
    		{
    			Node child = children.item(i);
    			if (GlimmpseConstants.TAG_MATRIX.equals(child.getNodeName()))
    			{
    				NamedNodeMap attrs = child.getAttributes();
    				Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
    				if (GlimmpseConstants.MATRIX_FIXED.equals(nameNode.getNodeValue()))
    					fixedNode = child;
    				else if (GlimmpseConstants.MATRIX_FIXED.equals(nameNode.getNodeValue()))
    					randomNode = child;
    			}
    		}
    		
    		int startRow = 0;
    		int startCol = 0;
    		if (fixedNode != null)
    		{
    			addMatrixData(fixedNode, table, startRow, startCol);
    		}
    		if (randomNode != null)
    		{
    			if (combineHorizontal)
    			{
    				startCol = table.getCellCount(0);
    			}
    			else
    			{
    				startRow = table.getRowCount();
    			}
    			addMatrixData(fixedNode, table, startRow, startCol);

    		}
    	}
    	
    	
    	return table;
    }

	
	
    public void addMatrixData(Node matrixNode, FlexTable table, int startRow, int startCol)
    {    	
        NamedNodeMap attrs = matrixNode.getAttributes();
        Node rowNode = attrs.getNamedItem("rows");
        Node colNode = attrs.getNamedItem("columns");
        if (rowNode != null && colNode != null)
        {                   
            NodeList rowNodeList = matrixNode.getChildNodes();
            for(int r = 0; r < rowNodeList.getLength(); r++)
            {
                NodeList colNodeList = rowNodeList.item(r).getChildNodes();
                for(int c = 0; c < colNodeList.getLength(); c++)
                {
                    Node colItem = colNodeList.item(c).getFirstChild();
                    if (colItem != null) 
                    {
                    	table.setWidget(startRow + r, startCol + c, new Label(colItem.getNodeValue()));
                    }
                }
            }
        }
    }
	
}
