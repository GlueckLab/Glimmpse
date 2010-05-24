package edu.cudenver.bios.glimmpse.client;

import com.google.gwt.i18n.client.Constants;

public interface GlimmpseConstants extends Constants
{
    // shared style names for input wizard steps
    public static final String STYLE_WIZARD_STEP_PANEL = "wizardStepPanel";
    public static final String STYLE_WIZARD_STEP_INPUT_CONTAINER = "wizardStepInputContainer";
    public static final String STYLE_WIZARD_STEP_HEADER = "wizardStepHeader";
    public static final String STYLE_WIZARD_STEP_DESCRIPTION = "wizardStepDescription";
    public static final String STYLE_WIZARD_STEP_SUBPANEL = "subpanel";
    public static final String STYLE_WIZARD_STEP_SELECTED = "selected";
    public static final String STYLE_WIZARD_STEP_DESELECTED = "deselected";
    public static final String STYLE_WIZARD_STEP_EVEN = "even";
    public static final String STYLE_WIZARD_STEP_ODD = "odd";
    public static final String STYLE_WIZARD_STEP_BUTTON = "wizardStepButton";
    public static final String STYLE_WIZARD_STEP_TABLE_PANEL = "wizardStepTablePanel";
    public static final String STYLE_WIZARD_STEP_TABLE = "wizardStepTable";
    public static final String STYLE_WIZARD_STEP_TABLE_COLUMN_HEADER = "wizardStepTableColumnHeader";
    public static final String STYLE_WIZARD_STEP_TABLE_ROW = "wizardStepTableRow";
    public static final String STYLE_WIZARD_STEP_TABLE_TEXTBOX = "wizardStepTableTextBox";
    public static final String STYLE_MESSAGE = "message";
    public static final String STYLE_MESSAGE_ERROR = "error";
    public static final String STYLE_MESSAGE_OKAY = "okay";

    // toolbar buttons
    public String buttonSaveStudy();
    public String buttonCancel();
    
    // navigation buttons
    public String buttonNext();
    public String buttonPrevious();
    
    // left navigation / steps left panel
    public String stepsLeftOutcomes();
    public String stepsLeftPredictors();
    public String stepsLeftGroups();
    public String stepsLeftHypotheses();
    public String stepsLeftEffectSize();
    public String stepsLeftVariability();
    public String stepsLeftAlpha();
    public String stepsLeftOptions();
    public String stepsLeftResults();
    
    // outcomes panel constants
    public String outcomesTitle();
    public String outcomesDescription();
    public String outcomesLabelRepeated();
    
}
