package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class EssenceMatrix extends Composite 
//implements MatrixResizeListener, CovariateListener
{
	private static final int MAX_RATIO = 10;
	protected CovariatePanel covariatePanel = new CovariatePanel();
    protected ResizableMatrix essence;
   	protected Grid rowMDGrid;
//    protected ArrayList<MatrixResizeListener> resizeListeners = new ArrayList<MatrixResizeListener>();
//    protected ArrayList<MetaDataListener> metaDataListeners = new ArrayList<MetaDataListener>();

    protected int minimumN;
    
    public EssenceMatrix(String name, int rows, int cols) 
    {
        essence = new ResizableMatrix(name, rows, cols);
//        essence.addMatrixResizeListener(this);

        // start with 1:1 ratio amongst groups
        minimumN = rows;
        
        VerticalPanel panel = new VerticalPanel();
                
        // build the row meta data panel
        VerticalPanel rowMDPanel = new VerticalPanel();
        rowMDPanel.add(new HTML("Ratio of group sizes"));
        rowMDGrid = new Grid(rows, 1);
        for(int r = 0; r < rows; r++)
        {
        	rowMDGrid.setWidget(r, 0, createRowMDTextBox());
        }
        rowMDPanel.add(rowMDGrid);
        // layout the essence matrix and row meta data
        Grid essenceGrid = new Grid(1,2);
        essenceGrid.setWidget(0, 0, essence);
        essenceGrid.setWidget(0, 1, rowMDPanel);

        
        // build covariate panel

            	
            	//essence.setCovariateColumn(covariateCheckBox.getValue());
            	
            	//for(MetaDataListener listener: metaDataListeners) listener.onCovariate(covariateCheckBox.getValue());



        // add listener to the covariate panel
       // covariatePanel.addCovariateListener(this);
        
        // layout the overall panel
        panel.add(essenceGrid);
        panel.add(covariatePanel);
        
        // add style
        initWidget(panel);
    }
    
    private ListBox createRowMDTextBox()
    {
    	ListBox list = new ListBox();
    	for(int i = 1; i <= MAX_RATIO; i++)
    	{
    		list.addItem(Integer.toString(i));
    		list.addChangeHandler(new ChangeHandler() {
    			public void onChange(ChangeEvent e)
    			{
    				minimumN = calculateMinimumN();
    				//for(MetaDataListener listener: metaDataListeners) listener.onMinimumSampleSize(minimumN);
    			}
    		});
    	}
    	return list;
    }
    
//    public void addMatrixResizeListener(MatrixResizeListener listener)
//    {
//        essence.addMatrixResizeListener(listener);
//        resizeListeners.add(listener);
//    }
//    
//    public void addMetaDataListener(MetaDataListener listener)
//    {
//        metaDataListeners.add(listener);
//        // notify of current sample size in case user does not change anything.
//        listener.onMinimumSampleSize(minimumN);
//    }
    
//    public void addCovariateListener(CovariateListener listener)
//    {
//        covariatePanel.addCovariateListener(listener);
//    }
    
    public void loadFromDomNode(Node node)
    {
        NodeList children = node.getChildNodes();
        Node rowMD = null;
        Node colMD = null;
        // locate the row/column meta data
        for(int i = 0; i < children.getLength(); i++)
        {
            Node child = children.item(i);
            String name = child.getNodeName();
            if (name.equals("matrix"))
            {
                essence.loadFromDomNode(child);
                onMatrixResize(essence.getRowDimension(), essence.getColumnDimension());
            }
            else if (name.equals("rowMetaData"))
                rowMD = child;
            else if (name.equals("columnMetaData"))
                colMD = child;
        }
        
        // parse the column meta data if there is any
        if (colMD != null)
        {
            
        }
        
        // parse the row meta data if there is any
        if (rowMD != null)
        {
            NodeList rowNodeList = rowMD.getChildNodes();
            for(int r = 0; r < rowNodeList.getLength(); r++)
            {
                NamedNodeMap attrs = rowNodeList.item(r).getAttributes();
                Node ratioNode = attrs.getNamedItem("ratio");
                if (ratioNode != null)
                {
                    try 
                    {
                        int ratio = Integer.parseInt(ratioNode.getNodeValue());
                        if (ratio >= 1 && ratio < MAX_RATIO)
                        {
                            ListBox lb = (ListBox) rowMDGrid.getWidget(r, 0);
                            lb.setSelectedIndex(ratio-1);
                        }
                    }
                    catch (NumberFormatException e) { /* ignore */ }
                }
            }
        }
        
    }
    
    public void setRowDimension(int newRows)
    {
        essence.setRowDimension(newRows);
    }
    
    public void setColumnDimension(int newCols)
    {
        essence.setColumnDimension(newCols);
    }

    public int getRowDimension()
    {
        return essence.getRowDimension();
    }
    
    public int getColumnDimension()
    {
        return essence.getColumnDimension();
    }
    
    public String toXML(int totalN)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<essenceMatrix>");
        buffer.append(columnMetaDataToXML());
        buffer.append(rowMetaDataToXML(totalN));
        buffer.append(essence.toXML());
        buffer.append("</essenceMatrix>");
        return buffer.toString();
    }
        
    private String rowMetaDataToXML(int totalN)
    {    	
    	// totalN must be multiple of the minimum sample size
    	int nMultiplier = totalN / minimumN;
    	
        StringBuffer buffer = new StringBuffer();
        buffer.append("<rowMetaData>");
        for(int r = 0; r < rowMDGrid.getRowCount(); r++)
        {
            ListBox ratioList = (ListBox) rowMDGrid.getWidget(r, 0);
            int ratio = Integer.parseInt(ratioList.getItemText(ratioList.getSelectedIndex()));
            buffer.append("<r ratio='" + ratio + "' ");
            int reps = (nMultiplier > 0 ? ratio*nMultiplier : ratio);
            buffer.append(" reps='" + reps + "' ");
            buffer.append("></r>");
        }
        buffer.append("</rowMetaData>");
        return buffer.toString();
    }
    
    private String columnMetaDataToXML()
    {        
        StringBuffer buffer = new StringBuffer();
        buffer.append("<columnMetaData>");
        for(int c = 0; c < essence.getColumnDimension(); c++) 
        {
            buffer.append("<c type='fixed'></c>");            
        }
        if (covariatePanel.hasCovariate())
        {
        	buffer.append("<c type='random' mean='" + covariatePanel.getMean() + "' variance='" + 
        	        covariatePanel.getVariance() + "' ></c>");
        }
        buffer.append("</columnMetaData>");
        return buffer.toString();
    }
    
    public void onRows(int newRows)
    {
    	int currentRows = rowMDGrid.getRowCount();
    	if (newRows != currentRows)
    	{
        	rowMDGrid.resizeRows(newRows);
        	for(int r = currentRows; r < newRows; r++)
        	{
        		rowMDGrid.setWidget(r, 0, createRowMDTextBox());
        	}
        	
        	// calculate the minimum sample size based on ratio of group sizes
        	minimumN = 0;
        	for(int r = 0; r < rowMDGrid.getRowCount(); r++) 
        	{
        	    ListBox tb = (ListBox) rowMDGrid.getWidget(r, 0);
        	    minimumN += Integer.parseInt(tb.getItemText(tb.getSelectedIndex()));
        	}
        	//for(MetaDataListener listener: metaDataListeners) listener.onMinimumSampleSize(minimumN);
    	}
    }
    
    public void onColumns(int newCols) {};
    
    public void onMatrixResize(int newRows, int newCols)
    {
    	int currentRows = rowMDGrid.getRowCount();
    	if (newRows != currentRows)
    	{
        	rowMDGrid.resizeRows(newRows);
        	for(int r = currentRows; r < newRows; r++)
        	{
        		rowMDGrid.setWidget(r, 0, createRowMDTextBox());
        	}
    	}
    }
    
    private int calculateMinimumN()
    {
        int minN = 0;
        for(int r = 0; r < rowMDGrid.getRowCount(); r++)
        {
            ListBox ratioList = (ListBox) rowMDGrid.getWidget(r, 0);
            int ratio = Integer.parseInt(ratioList.getItemText(ratioList.getSelectedIndex()));
            minN += ratio;
        }
        return minN;
    }
    
    public void reset(int newRows, int newColumns)
    {
        essence.reset(newRows, newColumns);
        onMatrixResize(newRows, newColumns);
    }
    
    public void onHasCovariate(boolean hasCovariate)
    {
        essence.setCovariateColumn(hasCovariate);
        //for(MetaDataListener listener: metaDataListeners) listener.onCovariate(hasCovariate);
    }
    
    public void onMean(double mean) {}
    
    public void onVariance(double variance) {}
}
