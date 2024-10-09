package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This class extends {@code PlainDocument} and is used directly by class
 * {@code FixedLengthTextField} amongst others, and allows restrictions to be set on the number of
 * characters entered on {@code JTextField} and {@code JTextArea} components which may be used on
 * windows for name or data entry etc.
 */
@SuppressWarnings("serial")
public final class FixedLengthDocument extends PlainDocument {

  private final JComponent parentTextComponent;

  private final int maximumCharsAllowed;

  /**
   * Constructs a new text document with specified restriction on the number of enterable characters
   * allowed.
   *
   * @param parentTextComponent the text component for which this document is being created for.
   * @param maximumCharsAllowed maximum number of characters allowed for text entry.
   */
  public FixedLengthDocument(final JComponent parentTextComponent, final int maximumCharsAllowed) {
    super();
    this.parentTextComponent = parentTextComponent;
    this.maximumCharsAllowed = maximumCharsAllowed;
  }

  /**
   * Overridden {@code insertString} method of {@code PlainDocument} in order for the enterable
   * characters restriction to be detected and capped.
   *
   * @param offset the starting offset >= 0.
   * @param s the string to insert; does nothing with {@code null}/empty strings.
   * @param a the attributes for the inserted content.
   * @exception BadLocationException the given insert position is not a valid position within the
   *            document.
   */
  @Override
  public void insertString(final int offset, final String s, final AttributeSet a) throws BadLocationException {
    if ((s == null) || (s.isEmpty())) {
      return;
    }
    if (getLength() + s.length() > maximumCharsAllowed) {
      Toolkit.getDefaultToolkit().beep();
      // Retrieve parent window for text entry component.
      final Window parentWindow = PresentationUtilities.getWindowForComponent(
          parentTextComponent);
      PresentationUtilities.displayMessageDialogOfRequiredType(parentWindow,
          "Maximum number of characters allowed for this field is " + maximumCharsAllowed + ".",
          JOptionPane.INFORMATION_MESSAGE, true);
      parentTextComponent.requestFocus();
      return;
    }
    super.insertString(offset, s, a);
  }
}
