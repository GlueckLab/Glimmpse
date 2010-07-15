package edu.cudenver.bios.glimmpse.client;

import com.google.gwt.i18n.client.Constants;

public interface GlimmpseConstants extends Constants
{
    // shared style names for input wizard steps
	public static final String STYLE_GLIMMPSE_PANEL = "glimmpsePanel";
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

    // mode names
    public static final String MODE_MATRIX = "matrix";
    public static final String MODE_GUIDED = "guided";

    // matrix names
    public static final String MATRIX_DESIGN_FIXED = "designFixed";
    public static final String MATRIX_DESIGN_RANDOM = "designRandom";
    public static final String MATRIX_BETA_FIXED = "betaFixed";
    public static final String MATRIX_BETA_RANDOM = "betaRandom";
    public static final String MATRIX_BETWEEN_CONTRAST_FIXED = "betweenSubjectContrastFixed";
    public static final String MATRIX_BETWEEN_CONTRAST_RANDOM = "betweenSubjectContrastRandom";
    public static final String MATRIX_WITHIN_CONTRAST = "withinSubjectContrast";
    public static final String MATRIX_SIGMA_ERROR = "sigmaError";
    public static final String MATRIX_SIGMA_OUTCOME = "sigmaOutcome";
    public static final String MATRIX_SIGMA_OUTCOME_COVARIATE = "sigmaOutcomeGaussianRandom ";
    public static final String MATRIX_SIGMA_COVARIATE = "sigmaGaussianRandom";
    public static final String MATRIX_THETA = "thetaNull";
    
	// dimension names derived from linear model theory.
	// ensures that default matrix dimensions conform properly
    public static final int DEFAULT_N = 3;
    public static final int DEFAULT_Q = 3;
    public static final int DEFAULT_P = 2;
    public static final int DEFAULT_A = 2;
    public static final int DEFAULT_B = 1;
    
    // toolbar buttons
    public String buttonSaveStudy();
    public String buttonCancel();
    
    // navigation buttons
    public String buttonNext();
    public String buttonPrevious();
    
    // start panel constants
    public String startTitle();
    public String startDescription();
    public String startGoButton();
    public String startMatrixTitle();
    public String startMatrixDescription();
    public String startGuidedTitle();
    public String startGuidedDescription();
    public String startUploadTitle();
    public String startUploadDescription();
    
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
    public String stepsLeftDesign();
    public String stepsLeftContrast();
    public String stepsLeftBeta();
    public String stepsLeftTheta();
    public String stepsLeftSigma();
    
    // alpha panel constants
    public String alphaTitle();
    public String alphaDescription();
    public String alphaTableColumn();
    
    // outcomes panel constants
    public String outcomesTitle();
    public String outcomesDescription();
    public String outcomesTableColumn();
    public String repeatedMeasuresTitle();
    public String repeatedMeasuresDescription();
    public String repeatedOverTableColumn();
    public String repetitionsTableColumn();
    
    // predictors panel constants
    public String predictorsTitle();
    public String predictorsDescription();
    public String predictorsTableColumn();
    public String categoriesTableColumn();
    
    // study groups panel constants
    public String studyGroupsTitle();
    public String studyGroupsDescription();
    public String equalGroupsLabel();
    
    // matrix constants
    public String matrixDimensionSeparator();
    
    // design matrix screen
    public String perGroupSampleSizeTableColumn();
    public String betaScaleTableColumn();
    public String sigmaScaleTableColumn();
    
    // error messages
    public String errorUploadFailed();
    public String errorUploadInvalidStudyFile();
    public String errorInvalidAlpha();
    public String errorInvalidMean();
    public String errorInvalidVariance();

    
}
