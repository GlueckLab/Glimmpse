package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class DesignPanel extends WizardStepPanel
implements MatrixResizeListener, CovariateListener, DynamicListValidator
{    	
	private static final int MAX_RATIO = 10;
    protected ResizableMatrix essenceFixed;
    protected ResizableMatrix essenceCovariate;
	protected CovariatePanel covariatePanel = new CovariatePanel();
   	protected Grid rowMDGrid;
   	
   	// list of per group sample sizes
	String[] columnNames = { Glimmpse.constants.perGroupSampleSizeTableTitle() };
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
					GlimmpseConstants.DEFAULT_N,1,"*", "Random Effects");

      // build the row meta data panel
      VerticalPanel rowMDPanel = new VerticalPanel();
      rowMDPanel.add(new HTML("Ratio of group sizes"));
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

      // add listeners
      covariatePanel.addCovariateListener(this);
      essenceFixed.addMatrixResizeListener(this);
      
      // layout the overall panel
      panel.add(header);
      panel.add(description);
      panel.add(layoutGrid);
      panel.add(covariatePanel);
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
}
