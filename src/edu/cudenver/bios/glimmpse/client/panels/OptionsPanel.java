package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.TextValidation;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;

public class OptionsPanel extends WizardStepPanel
implements CovariateListener, ClickHandler
{

	protected VerticalPanel testSubpanel = new VerticalPanel();
	protected VerticalPanel powerMethodSubpanel = new VerticalPanel();
	protected VerticalPanel displaySubpanel = new VerticalPanel();

	// check boxes for statistical tests
	protected CheckBox hotellingLawleyCheckBox = new CheckBox();
	protected CheckBox pillaiBartlettCheckBox = new CheckBox();
	protected CheckBox wilksCheckBox = new CheckBox();
	protected CheckBox unirepCheckBox = new CheckBox();
	protected CheckBox unirepGGCheckBox = new CheckBox();
	protected CheckBox unirepHFCheckBox = new CheckBox();
	protected CheckBox unirepBoxCheckBox = new CheckBox();

	// check boxes for power methods (only used when a baseline covariate is specified)
	protected CheckBox conditionalPowerCheckBox = new CheckBox();
	protected CheckBox unconditionalPowerCheckBox = new CheckBox();
	protected CheckBox quantilePowerCheckBox = new CheckBox();
	protected int numQuantiles = 0;
	
	String[] columnNames = { Glimmpse.constants.quantilesTableColumn()};
    protected DynamicListPanel quantileListPanel = 
    	new DynamicListPanel(columnNames, new DynamicListValidator() {

			@Override
			public void onValidRowCount(int validRowCount)
			{
				numQuantiles = validRowCount;
				checkComplete();
			}

			@Override
			public void validate(String value, int column)
					throws IllegalArgumentException
			{
		    	try
		    	{
		    		TextValidation.parseDouble(value, 0, 1);
		    	}
		    	catch (NumberFormatException nfe)
		    	{
		    		throw new IllegalArgumentException(Glimmpse.constants.errorInvalidQuantile());
		    	}
			}
    	});
    
	public OptionsPanel()
	{
		super(Glimmpse.constants.stepsLeftOptions());
		VerticalPanel panel = new VerticalPanel();

		HTML header = new HTML(Glimmpse.constants.optionsTitle());
		HTML description = new HTML(Glimmpse.constants.optionsDescription());        

		buildTestSubpanel();
		buildPowerMethodSubpanel();
		buildDisplaySubpanel();

		panel.add(header);
		panel.add(description);
		panel.add(testSubpanel);
		panel.add(powerMethodSubpanel);
		panel.add(displaySubpanel);

		powerMethodSubpanel.setVisible(false);

		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		initWidget(panel);
	}

	private void buildTestSubpanel()
	{
		HTML header = new HTML(Glimmpse.constants.testTitle());
		HTML description = new HTML(Glimmpse.constants.testDescription());

		Grid grid = new Grid(7,2);
		grid.setWidget(0, 1, new HTML(Glimmpse.constants.testUnirepLabel()));
		grid.setWidget(0, 0, unirepCheckBox);
		grid.setWidget(1, 1, new HTML(Glimmpse.constants.testUnirepGeisserGreenhouseLabel()));
		grid.setWidget(1, 0, unirepGGCheckBox);
		grid.setWidget(2, 1, new HTML(Glimmpse.constants.testUnirepHuynhFeldtLabel()));
		grid.setWidget(2, 0, unirepHFCheckBox);
		grid.setWidget(3, 1, new HTML(Glimmpse.constants.testUnirepBoxLabel()));
		grid.setWidget(3, 0, unirepBoxCheckBox);
		grid.setWidget(4, 1, new HTML(Glimmpse.constants.testHotellingLawleyTraceLabel()));
		grid.setWidget(4, 0, hotellingLawleyCheckBox);
		grid.setWidget(5, 1, new HTML(Glimmpse.constants.testPillaiBartlettTraceLabel()));
		grid.setWidget(5, 0, pillaiBartlettCheckBox);
		grid.setWidget(6, 1, new HTML(Glimmpse.constants.testWilksLambdaLabel()));
		grid.setWidget(6, 0, wilksCheckBox);
		
		// add callback to check if screen is complete
		unirepCheckBox.addClickHandler(this);
		unirepGGCheckBox.addClickHandler(this);
		unirepHFCheckBox.addClickHandler(this);
		unirepBoxCheckBox.addClickHandler(this);
		hotellingLawleyCheckBox.addClickHandler(this);
		pillaiBartlettCheckBox.addClickHandler(this);
		wilksCheckBox.addClickHandler(this);
		
		testSubpanel.add(header);
		testSubpanel.add(description);
		testSubpanel.add(grid);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		testSubpanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
		
	}

	private void buildPowerMethodSubpanel()
	{
		HTML header = new HTML(Glimmpse.constants.powerMethodTitle());
		HTML description = new HTML(Glimmpse.constants.powerMethodDescription());

		Grid grid = new Grid(4,2);
		grid.setWidget(0, 0, conditionalPowerCheckBox);
		grid.setWidget(0, 1, new HTML(Glimmpse.constants.powerMethodConditionalLabel()));
		grid.setWidget(1, 0, unconditionalPowerCheckBox);
		grid.setWidget(1, 1, new HTML(Glimmpse.constants.powerMethodUnconditionalLabel()));
		grid.setWidget(2, 0, quantilePowerCheckBox);
		grid.setWidget(2, 1, new HTML(Glimmpse.constants.powerMethodQuantileLabel()));
		grid.setWidget(3, 1, quantileListPanel);
		
		quantilePowerCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				quantileListPanel.setVisible(quantilePowerCheckBox.getValue());
			}
		});
		quantileListPanel.setVisible(false);
		
		// add callback to check if screen is complete
		conditionalPowerCheckBox.addClickHandler(this);
		unconditionalPowerCheckBox.addClickHandler(this);
		quantilePowerCheckBox.addClickHandler(this);
		
		// set conditional power on by default
		conditionalPowerCheckBox.setValue(true);
		
		// layout the subpanel
		powerMethodSubpanel.add(header);
		powerMethodSubpanel.add(description);
		powerMethodSubpanel.add(grid);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		powerMethodSubpanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
	}

	private void buildDisplaySubpanel()
	{

	}

	public void reset()
	{
		quantileListPanel.reset();
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		powerMethodSubpanel.setVisible(hasCovariate);
		if (hasCovariate)
		{
			conditionalPowerCheckBox.setValue(false);
			unconditionalPowerCheckBox.setValue(false);
			quantilePowerCheckBox.setValue(false);
			quantileListPanel.reset();
			quantileListPanel.setVisible(false);
		}
		else
		{
			// we always set conditional power for fixed designs
			conditionalPowerCheckBox.setValue(true);
		}
	}

	@Override
	public void onMean(double mean)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onVariance(double variance)
	{
		// TODO Auto-generated method stub

	}

	public String powerMethodListToXML()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("<testList>");
		if (hotellingLawleyCheckBox.getValue())
		{

		}
		buffer.append("<v>");
		if (pillaiBartlettCheckBox.getValue())
		{

		}
		if (wilksCheckBox.getValue())
		{

		}
		buffer.append("</testList>");
		return buffer.toString();
	}

	public String testListToXML()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("<testList>");
		if (hotellingLawleyCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_HOTELLING_LAWLEY_TRACE);
			buffer.append("</v>");
		}
		buffer.append("<v>");
		if (pillaiBartlettCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_PILLAI_BARTLETT_TRACE);
			buffer.append("</v>");
		}
		if (wilksCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_WILKS_LAMBDA);
			buffer.append("</v>");
		}
		if (unirepCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP);
			buffer.append("</v>");
		}
		if (unirepGGCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP_GEISSER_GRENNHOUSE);
			buffer.append("</v>");
		}
		if (unirepHFCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP_HUYNH_FELDT);
			buffer.append("</v>");
		}
		if (unirepBoxCheckBox.getValue())
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.TEST_UNIREP_BOX);
			buffer.append("</v>");
		}
		buffer.append("</testList>");
		return buffer.toString();
	}

	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(testListToXML());
		buffer.append(powerMethodListToXML());
		return buffer.toString();
	}
	@Override
	public void onClick(ClickEvent event)
	{
		checkComplete();			
	}
	
	private void checkComplete()
	{
		// check if continue is allowed
		// must have at least one test checked, at least one power method
		if ((hotellingLawleyCheckBox.getValue() || wilksCheckBox.getValue() || 
				pillaiBartlettCheckBox.getValue() || unirepCheckBox.getValue() ||
				unirepGGCheckBox.getValue() || unirepHFCheckBox.getValue() ||
				unirepBoxCheckBox.getValue()) &&
				(conditionalPowerCheckBox.getValue() || unconditionalPowerCheckBox.getValue() || 
						quantilePowerCheckBox.getValue()))
		{
			if (!quantilePowerCheckBox.getValue() || numQuantiles > 0)
			{
				notifyComplete();
			}
			else
			{
				notifyInProgress();
			}
		}
		else
		{
			notifyInProgress();
		}
	}
	
	
}
