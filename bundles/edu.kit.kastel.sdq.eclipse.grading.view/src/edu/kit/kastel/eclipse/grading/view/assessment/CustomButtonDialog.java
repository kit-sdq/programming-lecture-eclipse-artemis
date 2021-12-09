package edu.kit.kastel.eclipse.grading.view.assessment;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;

/**
 * This class is the view class for the custom penalty dialog. It has two
 * fields, custom message and custom penalty.
 *
 */
public class CustomButtonDialog extends Dialog {

	// Constants used for scaling the big custom penalty field
	private static final int CUSTOM_PENALTY_FIELD_WIDTH_MULTIPLIER = 24;
	private static final int CUSTOM_PENALTY_FIELD_HEIGHT_MULTIPLIER = 8;
	
	private Text customMessageInputField;
	private Spinner customPenaltyInputField;
	private String customMessage;
	private Double customPenalty;
	private IMistakeType customMistake;
	private final AssessmentViewController viewController;
	private final String ratingGroupName;

	public CustomButtonDialog(Shell parentShell, AssessmentViewController viewController, String ratingGroupName, IMistakeType mistake) {
		super(parentShell);
		this.viewController = viewController;
		this.ratingGroupName = ratingGroupName;
		this.customMistake = mistake;
	}

	@Override
	protected void cancelPressed() {
		this.customMessageInputField.setText("");
		super.cancelPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create custom error");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);
		
		boolean userWantsBigWindow = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.PREFERS_LARGE_PENALTY_TEXT_PATH);

		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = userWantsBigWindow ? 1 : 2;

		final Label customMessageLabel = new Label(comp, SWT.RIGHT);
		customMessageLabel.setText("Custom Message: ");

		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		
		GridData customMessageInputFieldData;
		if (userWantsBigWindow) {
			this.customMessageInputField = new Text(comp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL  |SWT.H_SCROLL);
			customMessageInputFieldData = new GridData(GridData.FILL_BOTH);
			
			// Calculating height and width based on the lineHeight (theoretically) ensures proper scaling across screen-sizes.
			// However lacking of a 4K-screen this has not been tested entirely.
			customMessageInputFieldData.minimumHeight = this.customMessageInputField.getLineHeight() * CUSTOM_PENALTY_FIELD_HEIGHT_MULTIPLIER;
			customMessageInputFieldData.minimumWidth = this.customMessageInputField.getLineHeight() * CUSTOM_PENALTY_FIELD_WIDTH_MULTIPLIER;
			
			this.customMessageInputField.addKeyListener(new MultiLineTextEditorKeyListener(this));
		} else {
			this.customMessageInputField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			customMessageInputFieldData = data;
		}
		
		final Label customPenaltyLabel = new Label(comp, SWT.RIGHT);
		customPenaltyLabel.setText("Custom Penalty: ");
		this.customPenaltyInputField = new Spinner(comp, SWT.SINGLE | SWT.BORDER);
		this.customPenaltyInputField.setDigits(1);
		this.customPenaltyInputField.setIncrement(5);
				
		this.customMessageInputField.setLayoutData(customMessageInputFieldData);
		this.customPenaltyInputField.setLayoutData(data);

		return comp;
	}

	public String getCustomMessage() {
		return this.customMessage;
	}

	public Double getCustomPenalty() {
		return this.customPenalty;
	}

	@Override
	protected void okPressed() {
		this.customMessage = this.customMessageInputField.getText();
		this.customPenalty = Double.parseDouble(this.customPenaltyInputField.getText().replace(',', '.'));
		this.viewController.addAssessmentAnnotation(this.customMistake, this.customMessage, this.customPenalty, this.ratingGroupName);
		super.okPressed();
	}
	
	/**
	 * This class implements a {@link KeyListener} that will prevent pressing RETURN from creating
	 * a new line. Instead it'll confirm the dialog. Also pressing TAB will jump out of the message-field
	 * in select the penalty-box. Newlines and tabs can be created by pressing SHIFT + (RETURN | TAB) 
	 * 
	 * This is required to mimic the behavior of a single-line text input, hence multi-line-text will 
	 * handle those keys differently.
	 * 
	 * @author Shirkanesi
	 */
	private static class MultiLineTextEditorKeyListener implements KeyListener {
		// Required due to Windows using \r\n, UNIX-like systems just \n
		private static final int LINE_SEPARATOR_LENGTH = System.lineSeparator().length();
		
		private boolean isShiftPressed;
		private final CustomButtonDialog customButtonDialog;
		
		public MultiLineTextEditorKeyListener(CustomButtonDialog customButtonDialog) {
			this.customButtonDialog = customButtonDialog;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
			if (e.keyCode == SWT.SHIFT) {
				this.isShiftPressed = false;
			}
			
			if (!this.isShiftPressed) {
				if (e.keyCode == SWT.TAB || isReturnCharacter(e.keyCode)) {
					int insertedLength;
					if (isReturnCharacter(e.keyCode)) {
						insertedLength = LINE_SEPARATOR_LENGTH;
					} else {
						insertedLength = 1;
					}
					
					// Removed the inserted character(s)
					int pos = this.customButtonDialog.customMessageInputField.getCaretPosition();
					String text = this.customButtonDialog.customMessageInputField.getText();
					String modified = text.substring(0, pos - insertedLength) + text.substring(pos);
					this.customButtonDialog.customMessageInputField.setText(modified);
					
					// Determine how to jump out of the text-field (either by closing the dialog or selecting the penalty-input)
					if (isReturnCharacter(e.keyCode)) {
						this.customButtonDialog.okPressed();
					} else {								
						this.customButtonDialog.customPenaltyInputField.setFocus();
					}
				}				
			}
		}
		
		private boolean isReturnCharacter(int keyCode) {
			return keyCode == SWT.CR || keyCode == SWT.LF;
		}
			
		
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.keyCode == SWT.SHIFT) {
				this.isShiftPressed = true;
			}
		}
	}
	
}
