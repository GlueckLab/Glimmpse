package edu.cudenver.bios.glimmpse.client.panels.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.OutcomesListener;
import edu.cudenver.bios.glimmpse.client.listener.RepeatedMeasuresListener;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListPanel;
import edu.cudenver.bios.glimmpse.client.panels.DynamicListValidator;
import edu.cudenver.bios.glimmpse.client.panels.RowCheckBox;
import edu.cudenver.bios.glimmpse.client.panels.RowTextBox;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class RepeatedMeasuresPanel extends WizardStepPanel
implements OutcomesListener, ClickHandler, ChangeHandler
{
	private static final String REPEATED_MEAUSRES_RADIO_GROUP = "repeatedMeasuresGroup";
	protected RadioButton singleMeasureRadioButton = 
		new RadioButton(REPEATED_MEAUSRES_RADIO_GROUP, Glimmpse.constants.singleMeasureLabel());
	protected RadioButton repeatedMeasures1DRadioButton = 
		new RadioButton(REPEATED_MEAUSRES_RADIO_GROUP, Glimmpse.constants.repeatedMeasures1DLabel());
	protected RadioButton repeatedMeasures2DRadioButton = 
		new RadioButton(REPEATED_MEAUSRES_RADIO_GROUP, Glimmpse.constants.repeatedMeasures2DLabel());
	
	// text boxes for each dimension
	protected TextBox repetitions1DTextBox = new TextBox();
	protected TextBox units1DTextBox = new TextBox();
	protected TextBox repetitions2DOuterTextBox = new TextBox();
	protected TextBox units2DOuterTextBox = new TextBox();
	protected TextBox repetitions2DInnerTextBox = new TextBox();
	protected TextBox units2DInnerTextBox = new TextBox();

    protected HTML errorHTML = new HTML();
    
    // listeners for repeated measures events
    protected ArrayList<RepeatedMeasuresListener> listeners = new ArrayList<RepeatedMeasuresListener>();
    
	public RepeatedMeasuresPanel()
	{
		super();
    	VerticalPanel panel = new VerticalPanel();
    	
        // create the repeated measures header/instruction text
        HTML header = new HTML(Glimmpse.constants.repeatedMeasuresTitle());
        HTML description = new HTML(Glimmpse.constants.repeatedMeasuresDescription());
                
        panel.add(header);
        panel.add(description);
        panel.add(singleMeasureRadioButton);
        panel.add(repeatedMeasures1DRadioButton);
        panel.add(createSinglyRepeatedMeasuresPanel());
        panel.add(repeatedMeasures2DRadioButton);
        panel.add(createDoublyRepeatedMeasuresPanel());

        // add callbacks
        singleMeasureRadioButton.addClickHandler(this);
        repeatedMeasures1DRadioButton.addClickHandler(this);
        repeatedMeasures2DRadioButton.addClickHandler(this);
        
        repetitions1DTextBox.addChangeHandler(this);
        units1DTextBox.addChangeHandler(this);
        repetitions2DOuterTextBox.addChangeHandler(this);
        units2DOuterTextBox.addChangeHandler(this);
        repetitions2DInnerTextBox.addChangeHandler(this);
        units2DInnerTextBox.addChangeHandler(this);
        
        // select non-repeated measures by default
        reset();
        
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        initWidget(panel);
	}

	private VerticalPanel createSinglyRepeatedMeasuresPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML(Glimmpse.constants.repeatedMeasuresRepeatsLabel()));
		grid.setWidget(0, 1, repetitions1DTextBox);
		grid.setWidget(1, 0, new HTML(Glimmpse.constants.repeatedMeasuresUnitsLabel()));
		grid.setWidget(1, 1, units1DTextBox);
		panel.add(grid);
		
		return panel;
	}
	
	private VerticalPanel createDoublyRepeatedMeasuresPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Grid grid = new Grid(3,4);
		grid.setWidget(0, 0, new HTML("1st Dimension"));
		grid.setWidget(1, 0, new HTML(Glimmpse.constants.repeatedMeasuresRepeatsLabel()));
		grid.setWidget(1, 1, repetitions2DOuterTextBox);
		grid.setWidget(2, 0, new HTML(Glimmpse.constants.repeatedMeasuresUnitsLabel()));
		grid.setWidget(2, 1, units2DOuterTextBox);
		
		grid.setWidget(0, 2, new HTML("2nd Dimension"));
		grid.setWidget(1, 2, new HTML(Glimmpse.constants.repeatedMeasuresRepeatsLabel()));
		grid.setWidget(1, 3, repetitions2DInnerTextBox);
		grid.setWidget(2, 2, new HTML(Glimmpse.constants.repeatedMeasuresUnitsLabel()));
		grid.setWidget(2, 3, units2DInnerTextBox);
		
		panel.add(grid);
		
		return panel;
	}

	
    private void notifyRepeatedMeasures()
    {
    	ArrayList<RepeatedMeasure> rmList = new ArrayList<RepeatedMeasure>();
    	
    	if (repeatedMeasures1DRadioButton.getValue())
    	{
    		rmList.add(new RepeatedMeasure(units1DTextBox.getText(), 
    				Integer.parseInt(repetitions1DTextBox.getText())));
    	}
    	else if (repeatedMeasures2DRadioButton.getValue())
    	{
    		rmList.add(new RepeatedMeasure(units2DOuterTextBox.getText(), 
    				Integer.parseInt(repetitions2DOuterTextBox.getText())));
    		rmList.add(new RepeatedMeasure(units2DInnerTextBox.getText(), 
    				Integer.parseInt(repetitions2DInnerTextBox.getText())));
    	}

    	for(RepeatedMeasuresListener listener: listeners) listener.onRepeatedMeasures(rmList);
    }
    
    public void addRepeatedMeasuresListener(RepeatedMeasuresListener listener)
    {
    	listeners.add(listener);
    }
    
    @Override
    public void onExit()
    {
    	notifyRepeatedMeasures();
    }
    
	@Override
	public void reset()
	{
		singleMeasureRadioButton.setValue(true);
		repetitions1DTextBox.setEnabled(false);
		units1DTextBox.setEnabled(false);
		repetitions2DOuterTextBox.setEnabled(false);
		units2DOuterTextBox.setEnabled(false);
		repetitions2DInnerTextBox.setEnabled(false);
		units2DInnerTextBox.setEnabled(false);
		checkComplete();
	}

	@Override
	public void loadFromNode(Node node)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOutcomes(List<String> outcomes)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(ClickEvent event)
	{
		repetitions1DTextBox.setEnabled(repeatedMeasures1DRadioButton.getValue());
		units1DTextBox.setEnabled(repeatedMeasures1DRadioButton.getValue());
		repetitions2DOuterTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		units2DOuterTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		repetitions2DInnerTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		units2DInnerTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		checkComplete();
	}
	
	private void checkComplete()
	{
		if (singleMeasureRadioButton.getValue())
		{
			notifyComplete();
		}
		else if (repeatedMeasures1DRadioButton.getValue())
		{		
			if (!repetitions1DTextBox.getText().isEmpty() && 
					!units1DTextBox.getText().isEmpty())
			{
				notifyComplete();
			}
			else
			{
				notifyInProgress();
			}
		}
		else if (repeatedMeasures2DRadioButton.getValue())
		{
			Window.alert(repetitions2DOuterTextBox.getText() + " empty? " + repetitions2DOuterTextBox.getText().isEmpty());
			if (!repetitions2DOuterTextBox.getText().isEmpty() && 
					!units2DOuterTextBox.getText().isEmpty() &&
					!repetitions2DInnerTextBox.getText().isEmpty() &&
					!units2DInnerTextBox.getText().isEmpty())
			{
				notifyComplete();
			}
			else
			{
				notifyInProgress();
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event)
	{
		checkComplete();
	}

}
