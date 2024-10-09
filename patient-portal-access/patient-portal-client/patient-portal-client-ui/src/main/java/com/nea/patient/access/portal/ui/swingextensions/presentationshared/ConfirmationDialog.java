package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class provides a dialog box supporting a configurable status line, central view
 * component with a scrollbar if the window is sized too small, and a button box containing at least
 * an 'OK' button but supporting optional 'Apply', 'Cancel', 'Print', 'Help', 'Reset', 'Default'
 * buttons and custom buttons if required. The dialog inherits from {@code LockableDialog}, so it
 * will support the busy cursor functionality in concert with time consuming or long running
 * activity.<br>
 * At construction no resources as required by this class are instantiated until the method
 * {@code layoutWindow} is called where the central view component (to be inserted centrally into
 * the {@code JDialog} content pane) is supplied and configuration for buttons and status line is
 * also specified. The optional buttons are configured for display via setting up a bit mask and
 * supplying it as an parameter to method {@code layoutWindow}.<br>
 * The reason why we simply cannot supply the central view component and custom buttons into the
 * constructor of this class is that the subclass has to call {@code super} on this class as the
 * first statement in it's constructor and therefore any required resource components (i.e. central
 * view component and if required custom buttons) of the subclass will not be instantiated and
 * initialised until after the return from method {@code super}. There are two ways to overcome
 * this, but both of these implementations have their limitations:
 * <ol>
 * <li>Declare the required resource components as static in the subclass, which will allow them to
 * be supplied in the call to {@code super} and thus this class constructor. The design does not
 * lend itself to further specialisation by inheritance extension.</li>
 * <li>Have a parent class declare an inner class which extends this class and the parent class
 * instantiates the required central view component before instantiating the inner class. Again,
 * such a design leads to complications when the inner class requires further specialisation.</li>
 * </ol>
 * Callback methods (with empty implementation) are available for each optional button which need to
 * be overridden as required by the subclass. It's also the responsibility of the subclass to setup
 * and handle {@code ActionListener} event for each supplied custom button.<br>
 * <br>
 * The button layout with all buttons configured including supplied custom buttons is as
 * follows:<br>
 * <br>
 * [OK] [Apply] [Custom Buttons...] [Print] [Reset] [Default] [Cancel] [Help]<br>
 * <br>
 * The above button display order is in accordance with the TMN Product User Interface Style Guide.
 */
@SuppressWarnings("serial")
public abstract class ConfirmationDialog extends LockableDialog {

  // Log4j
  private static final Logger logger = LoggerFactory.getLogger(ConfirmationDialog.class);

  // Declare button masks of buttons to be displayed.

  /** Constant used for OK button */
  public static final int OK_BUTTON = 0;

  /** Constant used for display configuration of Cancel button */
  public static final int CANCEL_BUTTON = 1;

  /** Constant used for display configuration of Apply button */
  public static final int APPLY_BUTTON = 2;

  /** Constant used for display configuration of Print button */
  public static final int PRINT_BUTTON = 4;

  /** Constant used for display configuration of Help button */
  public static final int HELP_BUTTON = 8;

  /** Constant used for display configuration of Reset button */
  public static final int RESET_BUTTON = 16;

  /** Constant used for display configuration of Default button */
  public static final int DEFAULT_BUTTON = 32;

  private static String applyLabel, okLabel, cancelLabel, printLabel, resetLabel, defaultLabel, helpLabel;

  private static char applyMnemonic, okMnemonic, cancelMnemonic, printMnemonic;

  private static char resetMnemonic, defaultMnemonic, helpMnemonic;

  // Panel container for button display.
  private JPanel buttonBox;

  // Instance member declarations for buttons that need to be accessed by non
  // initialise code aspects of this component.
  private JButton okButton;

  private JButton applyButton;

  private JButton cancelButton;

  // Reference to this dialog owner.
  private Window parent;

  private boolean confirmationGreyed = false;

  // Status line display component.
  private JLabel statusInfoLabel;

  /**
   * This instance member field is required because the super class ({@code MDialog} - extended from
   * {@code LockableDialog}) may need to reduce the size of the drawn window if it is detected that
   * the window is to large to fit within the confines of the display screen resolution. If the
   * window is reduced in size then a {@code JScrollPane} is introduced for the ContentPane's centre
   * view component. However, there is no means to retrieve the ContentPane's centre contents by the
   * super class, thus the need to store this instance member, which is assigned to the central
   * panel as supplied by the subclass. This reference is requested by super class {@code MDialog}
   * if a {@code JScrollPane} is required to be introduced through the method
   * {@code getContentPaneCentreViewComponent}.<br>
   * NOTE : Introducing the centre area {@code JScrollPane} by default is undesirable, as the scroll
   * bars can come into view unexpectedly even though the window is able to display its contents
   * comfortably. Furthermore, other undesirable behaviour has been noticed when the
   * {@code JScrollPane} is available by default even with it's scroll bars turned off.
   */
  private Component contentPaneCentrePiece;

  private boolean displayDefaultOkButton = true;

  static {
    applyLabel = "Apply";
    okLabel = "OK";
    cancelLabel = "Cancel";
    printLabel = "Print...";
    resetLabel = "Reset";
    defaultLabel = "Help...";
    applyMnemonic = 'A';
    okMnemonic = 'O';
    cancelMnemonic = 'C';
    printMnemonic = 'P';
    resetMnemonic = 'R';
    defaultMnemonic = 'D';
    helpMnemonic = 'H';
  }

  /**
   * Create a resizeable dialog with the specified title, owner {@code Frame} and modality and
   * {@code GraphicsConfiguration}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Frame} is
   *        used; if {@code dialogOwner} is also {@code null}, the system default
   *        {@code GraphicsConfiguration} is assumed.
   */
  public ConfirmationDialog(final Frame dialogOwner, final String title, final boolean modal,
      final GraphicsConfiguration gc) {
    super(dialogOwner, title, modal, gc);
    parent = dialogOwner;
  }

  /**
   * Create a resizeable dialog with the specified title and owner {@code Frame}. Dialog modality is
   * set to {@code false} by default and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   */
  public ConfirmationDialog(final Frame dialogOwner, final String title) {
    super(dialogOwner, title);
    parent = dialogOwner;
  }

  /**
   * Create a resizeable dialog with the specified title, owner {@code Dialog} and modality and
   * {@code GraphicsConfiguration}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Dialog} is
   *        used; if {@code dialogOwner} is also {@code null}, the system default
   *        {@code GraphicsConfiguration} is assumed.
   */
  public ConfirmationDialog(final Dialog dialogOwner, final String title, final boolean modal,
      final GraphicsConfiguration gc) {
    super(dialogOwner, title, modal, gc);
    parent = dialogOwner;
  }

  /**
   * Create a resizeable dialog with the specified title and owner {@code Dialog}. Dialog modality
   * is set to {@code false} by default and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   */
  public ConfirmationDialog(final Dialog dialogOwner, final String title) {
    super(dialogOwner, title);
    parent = dialogOwner;
  }

  /**
   * Create a resizeable dialog with the specified title, owner {@code Window} and modality and
   * {@code GraphicsConfiguration}.<br>
   *
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   * @param modalityType specifies whether dialog blocks input to other windows when shown. Value
   *        {@code null} and unsupported modality types are equivalent to {@code MODELESS}.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Window} is
   *        used; if {@code dialogOwner} is also {@code null}, the system default
   *        {@code GraphicsConfiguration} is assumed.
   *
   * @since 1.6
   */
  public ConfirmationDialog(final Window dialogOwner, final String title, final Dialog.ModalityType modalityType,
      final GraphicsConfiguration gc) {
    super(dialogOwner, title, modalityType, gc);
    parent = dialogOwner;
  }

  /**
   * Create a resizeable dialog with the specified title and owner {@code Window}. Dialog modality
   * is set to {@code false} by default and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   *
   * @since 1.6
   */
  public ConfirmationDialog(final Window dialogOwner, final String title) {
    super(dialogOwner, title);
    parent = dialogOwner;
  }

  /**
   * Invoke if the default 'OK' button is not required to be displayed. It needs to be called before
   * method {@code layoutWindow} is called.
   */
  protected final void disableDefaultOkButtonDisplay() {
    displayDefaultOkButton = false;
  }

  /**
   * Invoke to allow the 'OK' button text and mnemonic to be changed to that supplied. Generally,
   * invoke this method if you wish to rename the 'OK' button to say 'Open' for example.<br>
   * This method need to be called after {@code layoutWindow} method has been called.
   *
   * @param buttonText new text, needs to be internationalised if required.
   * @param mnemonic mnemonic to set for the new text.
   */
  protected final void overrideOKButtonTextAndMnemonic(final String buttonText, final char mnemonic) {
    if (okButton != null) {
      okButton.setText(buttonText);
      okButton.setMnemonic(mnemonic);
    }
  }

  /**
   * Invoke to allow the 'Cancel' button text and mnemonic to be changed to that supplied.
   * Generally, invoke this method if you wish to rename the 'Cancel' button to say 'Close' for
   * example.<br>
   * This method need to be called after {@code layoutWindow} method has been called.
   *
   * @param buttonText new text, needs to be internationalised if required.
   * @param mnemonic mnemonic to set for the new text.
   */
  protected final void overrideCancelButtonTextAndMnemonic(final String buttonText, final char mnemonic) {
    if (cancelButton != null) {
      cancelButton.setText(buttonText);
      cancelButton.setMnemonic(mnemonic);
    }
  }

  /**
   * Invoke by the subclass in order for this dialog's view contents to be setup correctly for
   * display purposes. Furthermore, this method is required to be called before the dialog is made
   * visible.<br>
   * It's in this method that the declared resources of this class to setup the button box and
   * status line are instantiated and initialised. <br>
   * This method will make the dialog resizeable. If this is not required call method
   * {@link #setResizable} after returning from this method.
   * 
   *
   * @param centrepiece contents to display in the central area of this dialog window.
   * @param optionalButtons optional buttons to display as provided by this class. The value for
   *        this parameter is an 'OR' of the button masks declared in this class, e.g.
   *        {@code APPLY_BUTTON} | {@code CANCEL_BUTTON}. No need to include {@code OK_BUTTON} in
   *        mask as 'OK' button is provided by default.
   * @param extraButtons array of {@code JButton} for additional custom buttons to display in the
   *        button box. Note this class will not manage the {@code Action} events for the custom
   *        buttons, therefore the specialised subclass needs to setup and handle
   *        {@code ActionListener} events for each supplied custom button if required and also setup
   *        text and mnemonics for each button accordingly.
   * @param displayStatusLine {@code true} if need to display status line component at bottom of
   *        dialog window, {@code false} otherwise.
   */
  protected final void layoutWindow(final Component centrepiece, final int optionalButtons,
      final JButton[] extraButtons, final boolean displayStatusLine) {
    // First remove all contents of the Dialog's content pane in case this
    // method is called more than once.
    final Container contentPane = getContentPane();
    contentPane.removeAll();

    // Store centrepiece reference locally and add it centrally to JDialog's
    // ContentPane.
    contentPaneCentrePiece = centrepiece;
    if (contentPaneCentrePiece != null) {
      contentPane.add(contentPaneCentrePiece, BorderLayout.CENTER);
    }

    // Build up the button panel.
    buttonBox = new JPanel();

    if (displayDefaultOkButton) {
      okButton = new JButton(okLabel);
      okButton.addActionListener(new ButtonCallback(OK_BUTTON));
      okButton.setMnemonic(okMnemonic);
      buttonBox.add(okButton);
    }

    if ((optionalButtons & APPLY_BUTTON) == APPLY_BUTTON) {
      applyButton = new JButton(applyLabel);
      applyButton.addActionListener(new ButtonCallback(APPLY_BUTTON));
      applyButton.setMnemonic(applyMnemonic);
      buttonBox.add(applyButton);
    }

    // Determine if any extra buttons need to be added as supplied by
    // specialised class.
    if (extraButtons != null) {
      for (int i = 0; i < extraButtons.length; i++) {
        buttonBox.add(extraButtons[i]);
      }
    }

    if ((optionalButtons & PRINT_BUTTON) == PRINT_BUTTON) {
      final JButton printButton = new JButton(printLabel);
      printButton.setMnemonic(printMnemonic);
      printButton.addActionListener(new ButtonCallback(PRINT_BUTTON));
      buttonBox.add(printButton);
    }

    if ((optionalButtons & RESET_BUTTON) == RESET_BUTTON) {
      final JButton resetButton = new JButton(resetLabel);
      resetButton.setMnemonic(resetMnemonic);
      resetButton.addActionListener(new ButtonCallback(RESET_BUTTON));
      buttonBox.add(resetButton);
    }

    if ((optionalButtons & DEFAULT_BUTTON) == DEFAULT_BUTTON) {
      final JButton defaultButton = new JButton(defaultLabel);
      defaultButton.setMnemonic(defaultMnemonic);
      defaultButton.addActionListener(new ButtonCallback(DEFAULT_BUTTON));
      buttonBox.add(defaultButton);
    }

    if ((optionalButtons & CANCEL_BUTTON) == CANCEL_BUTTON) {
      cancelButton = new JButton(cancelLabel);
      cancelButton.setMnemonic(cancelMnemonic);
      cancelButton.addActionListener(new ButtonCallback(CANCEL_BUTTON));
      buttonBox.add(cancelButton);
    }

    if ((optionalButtons & HELP_BUTTON) == HELP_BUTTON) {
      final JButton helpButton = new JButton(helpLabel);
      helpButton.setMnemonic(helpMnemonic);
      helpButton.addActionListener(new ButtonCallback(HELP_BUTTON));
      buttonBox.add(helpButton);
    }

    // Set the leftmost non-greyed button to be the default button.
    for (int b = 0; b < buttonBox.getComponentCount(); ++b) {
      final JButton button = (JButton) buttonBox.getComponent(b);
      // Don't make button the default if it is disabled.
      if (button.isEnabled()) {
        getRootPane().setDefaultButton(button);
        break;
      }
    }

    // Set layout manager for buttons panel - GridLayout makes all the
    // buttons the same size.
    buttonBox.setLayout(new GridLayout(1, buttonBox.getComponentCount(),
        PresentationUtilities.BUTTON_SPACING, 0));

    // Add the button panel to another panel which has the button alignment
    // correctly configured.
    final JPanel boxWrapper = PresentationUtilities.getButtonPanelWithRequiredButtonAlignment();
    boxWrapper.add(buttonBox);

    // Determine if we need to instantiate and setup the Status Line components.
    if (displayStatusLine) {
      final JPanel panel = new JPanel(new BorderLayout());

      panel.add(boxWrapper, BorderLayout.NORTH);
      panel.add(getStatusLinePanel(), BorderLayout.SOUTH);
      contentPane.add(panel, BorderLayout.SOUTH);
    } else {
      contentPane.add(boxWrapper, BorderLayout.SOUTH);
    }
    // Set to resizeable by default.
    setResizable(true);
  }

  private JPanel getStatusLinePanel() {
    // Instantiate and initialise the components of the Status Line.
    final JPanel statusBar = new JPanel(new GridLayout(1, 1));
    statusInfoLabel = new JLabel(" ");

    statusInfoLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.add(statusInfoLabel);

    return statusBar;
  }

  /**
   * Overridden method of super class {@code JDialog} to allow this dialog to handle the window
   * closing event.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  protected final void processWindowEvent(final WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      doClose();
    }
  }

  /**
   * Implementation for abstract method in super class {@code MDialog}.
   *
   * @return central display panel for {@code JDialog}.
   *
   * @see #contentPaneCentrePiece
   */
  @Override
  protected final Component getContentPaneCentreViewComponent() {
    return contentPaneCentrePiece;
  }

  /**
   * Invoke to allow this dialog window to be dimensioned according to used layout manager(s) and
   * allow it to be centred on its parent window before being displayed.
   */
  protected final void realize() {
    if ((buttonBox == null) && (contentPaneCentrePiece == null)) {
      logger.error("Dialog window is about to be realized without any required" +
          " resources instantiated because of ommitted method invocation to layoutWindow()");
    }
    pack();
    centreOnParent();
  }

  /**
   * Centre this dialog window on its parent window.
   */
  protected final void centreOnParent() {
    PresentationUtilities.centerWindowAndClipIfRequired(parent, this);
  }

  /**
   * Get parent window reference.
   *
   * @return reference to supplied parent via constructor.
   */
  protected final Window getParentWindow() {
    return parent;
  }

  /**
   * Grey or ungrey 'OK' and 'Apply' buttons.
   *
   * @param greyIt {@code true} to grey buttons, {@code false} otherwise.
   */
  protected final void setConfirmationGreyed(final boolean greyIt) {
    if (greyIt) {
      greyConfirmation();
    } else {
      ungreyConfirmation();
    }
  }

  /**
   * Grey 'OK' and 'Apply' buttons. The leftmost non-greyed button is set to be the default button
   * with the exception of 'Reset' and 'Cancel'.
   */
  protected final void greyConfirmation() {
    confirmationGreyed = true;

    // Grey the Apply button.
    if (applyButton != null) {
      applyButton.setEnabled(false);
    }

    // Grey the OK button.
    if (okButton != null) {
      okButton.setEnabled(false);

      if (okButton.isDefaultButton()) {
        // Set the leftmost non-greyed button to be the default button.
        for (int b = 0; b < buttonBox.getComponentCount(); b++) {
          final JButton button = (JButton) buttonBox.getComponent(b);
          // Don't let cancel button (or any buttons to the right of it) be default
          // button because you could press Enter on a text entry UI component which
          // has focus and end up closing the window by accident.
          if ((cancelButton != null) && (button == cancelButton)) {
            break;
          }
          // Ensure the default button is not also the Reset, as it can cause
          // problems with Enter being pressed in an editable JComboBox.
          if (button.getText().compareTo(resetLabel) == 0) {
            break;
          }
          // Don't make button the default if it is greyed.
          if (button.isEnabled()) {
            getRootPane().setDefaultButton(button);
            break;
          }
        }
      }
    }
  }

  /**
   * Ungrey the 'OK' and 'Apply' buttons. The 'OK' button is set to be the default button if it's
   * displayed, otherwise the leftmost non-greyed button is set to be the default button.
   */
  protected final void ungreyConfirmation() {
    confirmationGreyed = false;

    // Ungrey the Apply button.
    if (applyButton != null) {
      applyButton.setEnabled(true);
    }

    if (okButton != null) {
      // Ungrey the OK button.
      okButton.setEnabled(true);
      // Since it will be the leftmost button and it is definitely
      // ungreyed, it should be the default button.
      getRootPane().setDefaultButton(okButton);
    } else {
      // Set the leftmost non-greyed button to be the default button.
      for (int b = 0; b < buttonBox.getComponentCount(); b++) {
        final JButton button = (JButton) buttonBox.getComponent(b);
        // Don't make button the default if it is greyed.
        if (button.isEnabled()) {
          getRootPane().setDefaultButton(button);
          break;
        }
      }
    }
  }

  /**
   * Determine whether 'OK' and 'Apply' buttons have been greyed out or not.
   *
   * @return {@code true} means the buttons are greyed, {@code false} for ungreyed.
   */
  public final boolean isConfirmationGreyed() {
    return confirmationGreyed;
  }

  /**
   * Update the Status Line UI component with supplied text. This method has no effect on displayed
   * dialog if the Status Line component was not configured for display in method invocation
   * {@code layoutWindow}.
   *
   * @param text to display in status line, internationalise text contents if required.
   */
  public final void setStatusBarInformationText(final String text) {
    if (statusInfoLabel != null) {
      statusInfoLabel.setText(" " + text);
    }
  }

  /**
   * Clear the Status Line UI component contents. This method has no effect on displayed dialog if
   * the Status Line component was not configured to display in method invocation
   * {@code layoutWindow}.
   */
  public final void clearStatusBarInformationText() {
    if (statusInfoLabel != null) {
      statusInfoLabel.setText(" ");
    }
  }

  /**
   * Implementation for window close event or 'Cancel' button press. It will dispose of this dialog
   * window and any of it's child windows that may be launched.
   */
  protected final void doClose() {
    doForcedDisposeAction();
  }

  /**
   * Callback method for the 'OK' button press with no implementation. Override in subclass if this
   * button is required to be displayed.
   */
  protected void doOk() {}

  /**
   * Callback method for the 'Apply' button press with no implementation. Override in subclass if
   * this button is required to be displayed.
   */
  protected void doApply() {}

  /**
   * Callback method for the 'Reset' button press with no implementation. Override in subclass if
   * this button is required to be displayed.
   */
  protected void doReset() {}

  /**
   * Callback method for the 'Default' button press with no implementation. Override in subclass if
   * this button is required to be displayed.
   */
  protected void doDefault() {}

  /**
   * Callback method for the 'Print' button press with no implementation. Override in subclass if
   * this button is required to be displayed.
   */
  protected void doPrint() {}

  /**
   * Callback method for the 'Help' button press with no implementation. Override in subclass if
   * this button is required to be displayed.
   */
  protected void doHelp() {}

  /**
   * Invoke to lock the dialog window (with a busy cursor) and prevent further processing of mouse
   * and keyboard events.
   */
  protected final void goBusy() {
    final Runnable createRunForBusy = new Runnable() {

      @Override
      public void run() {
        setWindowBusyCursor();
      }
    };
    // Add Runnable object to AWTEventQueue to be executed when ready.
    SwingUtilities.invokeLater(createRunForBusy);
  }

  /**
   * Invoke to unlock the dialog window, which will clear the busy cursor state and re-enable mouse
   * and keyboard event processing.
   */
  protected final void unBusy() {
    final Runnable createRunForUnBusy = new Runnable() {

      @Override
      public void run() {
        setWindowDefaultCursor();
      }
    };
    // Add Runnable object to AWTEventQueue to be executed when ready.
    SwingUtilities.invokeLater(createRunForUnBusy);
  }

  /**
   * Inner class which allows an ActionListener to be instantiated against each available optional
   * button of parent class {@code ConfirmationDialog}.
   */
  private final class ButtonCallback implements ActionListener {

    int buttonType;

    private ButtonCallback(final int theButtonType) {
      buttonType = theButtonType;
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
      switch (buttonType) {
        case OK_BUTTON:
          ConfirmationDialog.this.doOk();
          break;
        case CANCEL_BUTTON:
          ConfirmationDialog.this.doClose();
          break;
        case APPLY_BUTTON:
          ConfirmationDialog.this.doApply();
          break;
        case PRINT_BUTTON:
          ConfirmationDialog.this.goBusy();
          ConfirmationDialog.this.doPrint();
          ConfirmationDialog.this.unBusy();
          break;
        case HELP_BUTTON:
          ConfirmationDialog.this.doHelp();
          break;
        case RESET_BUTTON:
          ConfirmationDialog.this.doReset();
          break;
        case DEFAULT_BUTTON:
          ConfirmationDialog.this.doDefault();
          break;
        default:
          break;
      }
    }
  }
}
