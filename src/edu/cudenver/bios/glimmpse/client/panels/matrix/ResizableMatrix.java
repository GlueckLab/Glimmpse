/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;

/**
 * Resizable matrix widget
 * @author Sarah Kreidler
 *
 */
public class ResizableMatrix extends Composite 
{
	protected static final int MAX_ROWS = 50;
	protected static final int MIN_ROW_COL = 1;
	protected static final int MAX_COLS = 50;
	protected Grid matrixData;
	protected TextBox rowTextBox;
	protected TextBox columnTextBox;
	protected boolean isSquare;
	protected boolean isSymmetric;
	protected HTML title = new HTML("");
	protected String defaultValue = "1";
	protected HTML errorHTML = new HTML();
	protected ArrayList<MatrixResizeListener> resizeListeners = new ArrayList<MatrixResizeListener>();
	protected String name = null;
	public ResizableMatrix(String name, int rows, int cols, String defaultValue, String title) 
	{	
		this.name = name;
	    if (title != null && !title.isEmpty()) 
	    	this.title = new HTML(title);
	    else
	    	this.title = new HTML();
	    
	    if (defaultValue != null && !defaultValue.isEmpty()) this.defaultValue = defaultValue;
	    
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
					notifyOnRows(newRows);
					TextValidation.displayOkay(errorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(errorHTML, "Please enter an integer value");
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
					notifyOnColumns(newCols);
					TextValidation.displayOkay(errorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					columnTextBox.setText(Integer.toString(matrixData.getCellCount(0)));
					TextValidation.displayError(errorHTML, "Please enter an integer value");
				}
			}
		});
		
		// layout the matrix dimensions
		HorizontalPanel matrixDimensions = new HorizontalPanel();
		matrixDimensions.add(rowTextBox);
		matrixDimensions.add(new HTML(Glimmpse.constants.matrixDimensionSeparator()));
		matrixDimensions.add(columnTextBox);
	    
		// build matrix itself
		matrixData = new Grid(rows, cols);
		initMatrixData();
		
		// add the widgets to the vertical panel
		matrixPanel.add(this.title);
		matrixPanel.add(matrixDimensions);
		matrixPanel.add(matrixData);
		matrixPanel.add(errorHTML);
		
		// set up styles
		matrixPanel.setStyleName(GlimmpseConstants.STYLE_MATRIX_PANEL);
		matrixDimensions.setStyleName(GlimmpseConstants.STYLE_MATRIX_DIMENSION);
		matrixData.setStyleName(GlimmpseConstants.STYLE_MATRIX_DATA);
		
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
				        for(int c = oldRows; c < newRows; c++) fillColumn(c, defaultValue, true);
					for(int r = oldRows; r < newRows; r++) fillRow(r, defaultValue, true);
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
                        for(int r = oldCols; r < oldCols; r++) fillRow(r, defaultValue, true);
					for(int c = oldCols; c < newCols; c++) fillColumn(c, defaultValue, true);
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
			fillRow(r, defaultValue, true);
		}
	}
		
	private void setData(int row, int col, String value, boolean enabled)
	{
		TextBox textBox = new TextBox();
		textBox.setValue(value);
		textBox.setStyleName(GlimmpseConstants.STYLE_MATRIX_CELL);
		textBox.setEnabled(enabled);
		matrixData.setWidget(row, col, textBox);
	}
	
	private void fillRow(int row, String diagonalValue, boolean enabled)
	{
		for (int c = 0; c < matrixData.getColumnCount(); c++)
		{
			if (c == row)
				setData(row, c, diagonalValue, enabled);
			else
				setData(row, c, "0", enabled);
		}
	}
	
	private void fillColumn(int col, String diagonalValue, boolean enabled)
	{
		for (int row= 0; row < matrixData.getRowCount(); row++) 
		{
			if (col == row)
				setData(row, col, diagonalValue, enabled);
			else
				setData(row, col, "0", enabled);
		}
	}
		
	public String toXML()
	{
		return toXML(name);
	}
	
	public String toXML(String matrixName)
	{
		StringBuffer buffer = new StringBuffer();
		
		int start = 0;
		int rows = matrixData.getRowCount();
		int cols = matrixData.getCellCount(0);

		buffer.append("<matrix name='" + matrixName + "' rows='" +  rows + "' columns='" + 
		        cols + "'>");

		for(int r = start; r < matrixData.getRowCount(); r++)
		{
			buffer.append("<r>");
			for(int c = start; c < cols; c++)
			{
				TextBox txt = (TextBox) matrixData.getWidget(r, c);
				buffer.append("<c>" + txt.getValue() + "</c>");
			}
			buffer.append("</r>");
		}
		buffer.append("</matrix>");

		return buffer.toString();
	}
		
	public void addMatrixResizeListener(MatrixResizeListener listener)
	{
		resizeListeners.add(listener);
	}
        
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

    public void reset(int newRows, int newColumns)
    {
        matrixData.resize(newRows, newColumns);
        rowTextBox.setText(Integer.toString(newRows));
        columnTextBox.setText(Integer.toString(newColumns));
        for(int r = 0; r < matrixData.getRowCount(); r++)
        {
            fillRow(r, defaultValue, true);
        }
    }
    
    public void notifyOnColumns(int newCols)
    {
		for(MatrixResizeListener listener: resizeListeners) 
			listener.onColumns(name, newCols);
    }
    
    public void notifyOnRows(int newRows)
    {
		for(MatrixResizeListener listener: resizeListeners) 
			listener.onRows(name, newRows);
    }
    
    public void setEnabledRowDimension(boolean enabled)
    {
    	rowTextBox.setEnabled(enabled);
    }
    
    public void setEnabledColumnDimension(boolean enabled)
    {
    	columnTextBox.setEnabled(enabled);
    }
    
    public void reset()
    {
    	reset(this.getRowDimension(), this.getColumnDimension());
    }
}
