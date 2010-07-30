package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class DesignPanel extends WizardStepPanel
implements SolvingForListener, MatrixResizeListener, CovariateListener, DynamicListValidator
{    	
	private static final int MAX_RATIO = 10;
    protected ResizableMatrix essenceFixed;
    protected ResizableMatrix essenceCovariate;
	protected CovariatePanel covariatePanel = new CovariatePanel();
   	protected Grid rowMDGrid;
   	
   	// list of per group sample sizes
	String[] columnNames = { Glimmpse.constants.perGroupSampleSizeTableColumn() };
    protected DynamicListPanel perGroupNListPanel =
    	new DynamicListPanel(columnNames, this);
   	
	public DesignPanel()
	{
		super(Glimmpse.constants.stepsLeftDesign());
		
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML("Specify your Design \"Essence\" Matrix");
        HTML description = new HTML("your design matrix and stuff");
		// create the fixed and covariate matrices
		essenceFixed = new ResizableMatrix(GlimmpseConstants.MATRIX_DESIGN_FIXED,
				GlimmpseConstants.DEFAULT_N, 
				GlimmpseConstants.DEFAULT_Q, "0", "Fixed Effects");
		essenceCovariate = 
			new ResizableMatrix(GlimmpseConstants.MATRIX_DESIGN_RANDOM,
					GlimmpseConstants.DEFAULT_N,1,"1", "Random Effects");

      // build the row meta data panel
      VerticalPanel rowMDPanel = new VerticalPanel();
      rowMDPanel.add(new HTML("Relative<br>Group<br>Size"));
      rowMDGrid = new Grid(GlimmpseConstants.DEFAULT_N, 1);
      for(int r = 0; r < GlimmpseConstants.DEFAULT_N; r++)
      {
      	rowMDGrid.setWidget(r, 0, createRowMDTextBox());
      }
      rowMDPanel.add(rowMDGrid);
      // layout the matrices and row meta data
      Grid layoutGrid = new Grid(1,3);
      layoutGrid.setWidget(0, 0, rowMDPanel);
      layoutGrid.setWidget(0, 1, essenceFixed);
      layoutGrid.setWidget(0, 2, essenceCovariate);
      layoutGrid.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_BOTTOM);
      // add listeners
      covariatePanel.addCovariateListener(this);
      essenceFixed.addMatrixResizeListener(this);
      
      // layout the overall panel
      panel.add(header);
      panel.add(description);
      panel.add(covariatePanel);
      panel.add(layoutGrid);
      panel.add(perGroupNListPanel);
      essenceCovariate.setVisible(false);
      
      // add style
      initWidget(panel);
	}
	
    private ListBox createRowMDTextBox()
    {
    	ListBox list = new ListBox();
    	for(int i = 1; i <= MAX_RATIO; i++)
    	{
    		list.addItem(Integer.toString(i));
    	}		
    	return list;
    }
    
	public void reset()
	{
		
	}
	
    public void onHasCovariate(boolean hasCovariate) 
    {
    	essenceCovariate.setVisible(hasCovariate);
    }
    
    public void onMean(double mean) {}
    
    public void onVariance(double variance) {}
    
	public void onRows(String name, int newRows) 
	{
		int currentRows = rowMDGrid.getRowCount();
		if (currentRows != newRows)
		{
			essenceCovariate.setRowDimension(newRows);
			rowMDGrid.resizeRows(newRows);
			for(int r = currentRows; r < newRows; r++)
			{
				rowMDGrid.setWidget(r, 0, createRowMDTextBox());
			}
		}
	}
	
	public void onColumns(String name, int newCols) 
	{
		
	}
	
	public void validate(String value, int column) throws IllegalArgumentException
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
	
	public void addCovariateListener(CovariateListener listener)
	{
		covariatePanel.addCovariateListener(listener);
	}
	
	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		if (perGroupNListPanel.isVisible())
		{
			buffer.append(perGroupNListPanel.toXML("sampleSizeList"));
		}
		
		buffer.append("<essenceMatrix>");
		// list row meta data
		buffer.append("<rowMetaData>");
		for(int r = 0; r < rowMDGrid.getRowCount(); r++)
		{
			ListBox lb = (ListBox) rowMDGrid.getWidget(r, 0);
			buffer.append("<r ratio='" + lb.getItemText(lb.getSelectedIndex()) + "' />");
		}
		buffer.append("</rowMetaData>");

		// add fixed effects matrix
		buffer.append(essenceFixed.toXML(GlimmpseConstants.MATRIX_FIXED));
		// TODO: decide what we are doing here
		
		// if the user is controlling for a baseline covariate, add the random meta data
		// and random effects matrix to the output
		if (covariatePanel.hasCovariate())
		{
			// list random column meta data
			buffer.append("<randomColumnMetaData>");
			buffer.append("<c mean='");
			buffer.append(covariatePanel.getMean());
			buffer.append("' variance='");
			buffer.append(covariatePanel.getVariance());
			buffer.append("'></c></randomColumnMetaData>");
			buffer.append(essenceCovariate.toXML(GlimmpseConstants.MATRIX_RANDOM));
		}
		buffer.append("</essenceMatrix>");
		return buffer.toString();
	}

	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		perGroupNListPanel.setVisible(solutionType != SolutionType.TOTAL_N);
		if (solutionType != SolutionType.TOTAL_N)
		{
			notifyComplete();
		}
	}
}
