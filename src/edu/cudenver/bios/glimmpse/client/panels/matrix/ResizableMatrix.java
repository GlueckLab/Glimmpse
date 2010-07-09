package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.TextValidation;

public class ResizableMatrix extends Composite 
{
    protected static final String MATRIX_STYLE = "matrix";
	protected static final String DIMENSION_STYLE = "matrixDimensions";
	protected static final String MATRIX_DATA_STYLE = "matrixData";
	protected static final String MATRIX_CELL_STYLE = "matrixCell";
	protected static final String DEFAULT_VALUE = "0";
	protected static final String DEFAULT_COVARIATE_VALUE = "*";
	protected static final int MAX_ROWS = 50;
	protected static final int MIN_ROW_COL = 1;
	protected static final int MAX_COLS = 50;
	protected Grid matrixData;
	protected TextBox rowTextBox;
	protected TextBox columnTextBox;
	protected boolean isSquare;
	protected boolean isSymmetric;
	protected String name;
//	protected ArrayList<MatrixResizeListener> resizeListeners = new ArrayList<MatrixResizeListener>();
//	protected ArrayList<MetaDataListener> metaDataListeners = new ArrayList<MetaDataListener>();
//	
	protected boolean hasCovariateColumn = false;
	protected boolean hasCovariateRow = false;
	
	public ResizableMatrix(String name, int rows, int cols) 
	{
	    this.name = name;
		// overall layout panel    
	    VerticalPanel matrixPanel = new VerticalPanel();

		// matrix dimensions
		rowTextBox = new TextBox();
		rowTextBox.setValue(Integer.toString(rows), false);
		rowTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				try
				{
					int newRows = edu.cudenver.bios.glimmpse.client.TextValidation.parseInteger(rowTextBox.getText(), MIN_ROW_COL, MAX_ROWS);
					setRowDimension(newRows);
					// notify listeners of row change
					//for(MatrixResizeListener listener: resizeListeners) listener.onRows(newRows);
				}
				catch (NumberFormatException nfe)
				{
				    Window.alert("invalid value: " + rowTextBox.getText() + ", " + nfe.getMessage());
					rowTextBox.setText(Integer.toString(matrixData.getRowCount()));
				}
			}
		});
		columnTextBox = new TextBox();
		columnTextBox.setValue(Integer.toString(cols), false);		
		columnTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				try
				{
					int newCols = TextValidation.parseInteger(columnTextBox.getText(), MIN_ROW_COL, MAX_ROWS);
					setColumnDimension(newCols);
					// notify listeners of row change
					//for(MatrixResizeListener listener: resizeListeners) listener.onColumns(newCols);
				}
				catch (NumberFormatException nfe)
				{
					columnTextBox.setText(Integer.toString(matrixData.getCellCount(0)));
				}
			}
		});
		
		// layout the matrix dimensions
		HorizontalPanel matrixDimensions = new HorizontalPanel();
		matrixDimensions.add(rowTextBox);
		//matrixDimensions.add(new HTML(Glimmpse.constants.matrixDimensionSeparator()));
		matrixDimensions.add(columnTextBox);
	    
		// build matrix itself
		matrixData = new Grid(rows, cols);
		initMatrixData();
		
		// add the widgets to the vertical panel
		matrixPanel.add(matrixDimensions);
		matrixPanel.add(matrixData);
		
		// set up styles
		matrixPanel.setStyleName(MATRIX_STYLE);
		matrixDimensions.setStyleName(DIMENSION_STYLE);
		matrixData.setStyleName(MATRIX_DATA_STYLE);
		
		// initialize the widget
		initWidget(matrixPanel);
	}
    	
	public void setIsSquare(boolean isSquare, boolean isSymmetric)
	{
		this.isSymmetric = isSymmetric;
		this.isSquare = isSquare;
		if (isSquare)
		{
			int rows = Integer.parseInt(rowTextBox.getValue());
			int cols = Integer.parseInt(columnTextBox.getValue());
			if (rows != cols)
			{
				columnTextBox.setText(rowTextBox.getValue());
				setColumnDimension(rows);
			}
		}
		columnTextBox.setEnabled(!isSquare);
		
		//TODO: symmetry
	}
	
	public void setRowDimension(int newRows)
	{
		if (newRows >= MIN_ROW_COL && newRows <= MAX_ROWS)
		{
			int oldRows = matrixData.getRowCount();
			if (oldRows != newRows)
			{
			    
	             if (isSquare)
	                    matrixData.resize(newRows, newRows);
	                else
	                    matrixData.resizeRows(newRows);
				if (newRows > oldRows)
				{
				    if (isSquare) 
				        for(int c = oldRows; c < newRows; c++) fillColumn(c, DEFAULT_VALUE, true);
					for(int r = oldRows; r < newRows; r++) fillRow(r, DEFAULT_VALUE, true);
					if (hasCovariateRow)  fillRow(oldRows-1, DEFAULT_VALUE, true);

				} 
				if (hasCovariateRow) 
				{
					fillRow(newRows - 1, DEFAULT_COVARIATE_VALUE, false);
				}
			}
		}
		
		rowTextBox.setText(Integer.toString(matrixData.getRowCount()));
        if (isSquare) columnTextBox.setText(Integer.toString(matrixData.getColumnCount()));

	}
	
	public void setColumnDimension(int newCols)
	{
		if (newCols >= MIN_ROW_COL && newCols <= MAX_COLS)
		{
			int oldCols = matrixData.getColumnCount();
			if (oldCols != newCols)
			{
			    if (isSquare)
			        matrixData.resize(newCols, newCols);
			    else
			        matrixData.resizeColumns(newCols);
			    
				if (newCols > oldCols)
				{
                    if (isSquare) 
                        for(int r = oldCols; r < oldCols; r++) fillRow(r, DEFAULT_VALUE, true);
					for(int c = oldCols; c < newCols; c++) fillColumn(c, DEFAULT_VALUE, true);
					if (hasCovariateColumn) fillColumn(oldCols-1, DEFAULT_VALUE, true);
				} 
				if (hasCovariateColumn) 
				{
					fillColumn(newCols - 1, DEFAULT_COVARIATE_VALUE, false);
				}
			}
			
			columnTextBox.setText(Integer.toString(matrixData.getColumnCount()));
			if (isSquare) rowTextBox.setText(Integer.toString(matrixData.getRowCount()));
		}

	}
	
	public int getRowDimension()
	{
		return matrixData.getRowCount();
	}
	
	public int getColumnDimension()
	{
		return matrixData.getCellCount(0);
	}
	
	private void initMatrixData()
	{
		for(int r = 0; r < matrixData.getRowCount(); r++)
		{
			fillRow(r, DEFAULT_VALUE, true);
		}
	}
		
	private void setData(int row, int col, String value, boolean enabled)
	{
		TextBox textBox = new TextBox();
		textBox.setValue(value);
		textBox.setStyleName(MATRIX_CELL_STYLE);
		textBox.setEnabled(enabled);
		matrixData.setWidget(row, col, textBox);
	}
	
	private void fillRow(int row, String value, boolean enabled)
	{
		int cols = matrixData.getColumnCount();
		int c = 0;
		for (; c < cols-1; c++)
		{
			setData(row, c, value, enabled);
		}
		if (hasCovariateColumn)
			setData(row, c, DEFAULT_COVARIATE_VALUE, false);
		else
			setData(row, c, value, enabled);
	}
	
	private void fillColumn(int col, String value, boolean enabled)
	{
		int rows = matrixData.getRowCount();
		int row= 0;
		for (; row < rows-1; row++) 
			setData(row, col, value, enabled);
		if (hasCovariateRow) 
			setData(row, col, DEFAULT_COVARIATE_VALUE, false);
		else
			setData(row, col, value, enabled);
	}
		
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		int start = 0;
		int rows = matrixData.getRowCount();
		int cols = matrixData.getCellCount(0);

		buffer.append("<matrix name='" + name + "' rows='" +  rows + "' columns='" + 
		        cols + "'>");

		for(int r = start; r < matrixData.getRowCount(); r++)
		{
			buffer.append("<r>");
			for(int c = start; c < cols; c++)
			{
				TextBox txt = (TextBox) matrixData.getWidget(r, c);
				String val = txt.getValue();
				if (val.equals(DEFAULT_COVARIATE_VALUE)) val = "1";
				buffer.append("<c>" + txt.getValue() + "</c>");
			}
			buffer.append("</r>");
		}
		buffer.append("</matrix>");

		return buffer.toString();
	}
		
//	public void addMatrixResizeListener(MatrixResizeListener listener)
//	{
//		resizeListeners.add(listener);
//	}
        
    public void loadFromDomNode(Node matrixNode)
    {
        NamedNodeMap attrs = matrixNode.getAttributes();
        Node rowNode = attrs.getNamedItem("rows");
        Node colNode = attrs.getNamedItem("columns");
        if (rowNode != null && colNode != null)
        {           
        	int rows = Integer.parseInt(rowNode.getNodeValue());
        	int cols = Integer.parseInt(colNode.getNodeValue());
        	matrixData.resize(rows, cols);
        	rowTextBox.setText(rowNode.getNodeValue());
        	columnTextBox.setText(colNode.getNodeValue());
        	
            NodeList rowNodeList = matrixNode.getChildNodes();
            for(int r = 0; r < rowNodeList.getLength(); r++)
            {
                NodeList colNodeList = rowNodeList.item(r).getChildNodes();
                for(int c = 0; c < colNodeList.getLength(); c++)
                {
                    Node colItem = colNodeList.item(c).getFirstChild();
                    if (colItem != null) 
                    {
                    	setData(r,c,colItem.getNodeValue(),true);
                    }
                }
            }
        }
    }
    
    public void setCovariateColumn(boolean hasCovariateColumn)
    {
    	if (hasCovariateColumn)
    	{
    		if (!this.hasCovariateColumn)
    		{
    			int newCols = matrixData.getColumnCount()+1;
    			matrixData.resizeColumns(newCols);
    			fillColumn(newCols-1,DEFAULT_COVARIATE_VALUE, false);
    		}
    	}
    	else 
    	{
    		if (this.hasCovariateColumn)
    		{
    			matrixData.resizeColumns(matrixData.getColumnCount()-1);
    		}
    	}
    	this.hasCovariateColumn = hasCovariateColumn;
		columnTextBox.setText(Integer.toString(matrixData.getCellCount(0)));
    }
    
    public void setCovariateRow(boolean hasCovariateRow)
    {
    	if (hasCovariateRow)
    	{
    		if (!this.hasCovariateRow)
    		{
    			int newRows = matrixData.getRowCount()+1;
    			matrixData.resizeRows(newRows);
    			fillRow(newRows-1,DEFAULT_COVARIATE_VALUE, false);
    		}
    	}
    	else 
    	{
    		if (this.hasCovariateRow)
    		{
    			matrixData.removeRow(matrixData.getRowCount()-1);
    		}
    	}
    	this.hasCovariateRow = hasCovariateRow;
		rowTextBox.setText(Integer.toString(matrixData.getRowCount()));
    }
    
    public void reset(int newRows, int newColumns)
    {
        matrixData.resize(newRows, newColumns);
        rowTextBox.setText(Integer.toString(newRows));
        columnTextBox.setText(Integer.toString(newColumns));
        for(int r = 0; r < matrixData.getRowCount(); r++)
        {
            fillRow(r, DEFAULT_VALUE, true);
        }
    }
}
