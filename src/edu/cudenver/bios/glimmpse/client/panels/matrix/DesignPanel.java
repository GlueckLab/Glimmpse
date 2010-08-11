package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;
import edu.cudenver.bios.glimmpse.client.panels.CovariatePanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.ListEntryPanel;
import edu.cudenver.bios.glimmpse.client.panels.ListValidator;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class DesignPanel extends WizardStepPanel
implements SolvingForListener, MatrixResizeListener, CovariateListener, ListValidator
{    	
	private static final int MAX_RATIO = 10;
    protected ResizableMatrix essenceFixed = new ResizableMatrix(GlimmpseConstants.MATRIX_DESIGN_FIXED,
			GlimmpseConstants.DEFAULT_N, 
			GlimmpseConstants.DEFAULT_Q, "0", Glimmpse.constants.matrixCategoricalEffectsLabel());
    protected ResizableMatrix essenceCovariate = new ResizableMatrix(GlimmpseConstants.MATRIX_DESIGN_RANDOM,
			GlimmpseConstants.DEFAULT_N,1,"1", Glimmpse.constants.matrixCovariateEffectsLabel());
	protected CovariatePanel covariatePanel = new CovariatePanel(Glimmpse.constants.matrixCovariateDescription());
   	protected Grid rowMDGrid;
   	
   	// list of per group sample sizes
    protected ListEntryPanel perGroupNListPanel =
    	new ListEntryPanel(Glimmpse.constants.perGroupSampleSizeTableColumn(), this);
    // panel containing group sample size list
    protected VerticalPanel perGroupSampleSizePanel = new VerticalPanel();
    
	public DesignPanel()
	{
		super(Glimmpse.constants.stepsLeftDesign());
		
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(Glimmpse.constants.matrixDesignTitle());
        HTML description = new HTML(Glimmpse.constants.matrixDesignDescription());
        
        // build the per group sample size panel
        buildPerGroupSampleSizePanel();
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(createEssenceMatrixPanel());
        panel.add(covariatePanel);
        panel.add(perGroupSampleSizePanel);
        
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
    private ListBox createRowMDTextBox()
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
        panel.add(layoutGrid);
        essenceCovariate.setVisible(false);
    	
        // set style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    	rowMDPanel.setStyleName(GlimmpseConstants.STYLE_MATRIX_PANEL);
    	rowMDGrid.setStyleName(GlimmpseConstants.STYLE_MATRIX_DATA);
    	return panel;
    }
    
    /**
     * Layout the widgets in the per group sample size panel
     */
    private void buildPerGroupSampleSizePanel()
    {
        HTML header = new HTML(Glimmpse.constants.perGroupSampleSizeTitle());
        HTML description = new HTML(Glimmpse.constants.perGroupSampleSizeDescription());
        
    	perGroupSampleSizePanel.add(header);
    	perGroupSampleSizePanel.add(description);
    	perGroupSampleSizePanel.add(perGroupNListPanel);
    	
    	perGroupSampleSizePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
    	perGroupSampleSizePanel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);

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

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub
		
	}
}
