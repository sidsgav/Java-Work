package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.FontMetrics;

import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.Document;

/**
 * This class extends {@code JTextField} and uses class {@code FixedLengthDocument} to allow
 * restrictions to be specified on the number of characters entered for {@code JTextField}
 * components.<br>
 * This class also provides the facility to offer intelligent ToolTip support whereby the ToolTip
 * will only be displayed if the text contents are partially hidden. This will only be enabled if
 * the application wide ToolTip support is enabled.
 */
@SuppressWarnings("serial")
public class FixedLengthTextField extends JTextField {

  private int maxCharactersAllowed = 0;

  /**
   * Constructs a new empty text field. A default model is created, the initial string is
   * {@code null}, and the number of columns is set to 0. No restriction is placed on the number of
   * characters entered.
   */
  public FixedLengthTextField() {
    super();
    initialise();
  }

  /**
   * Constructs a new text field initialised with the specified text. A default model is created and
   * the number of columns is 0. No restriction is placed on the number of characters entered.
   *
   * @param text the text to be displayed, or {@code null}.
   */
  public FixedLengthTextField(final String text) {
    super(text);
    initialise();
  }

  /**
   * Constructs a new empty text field with the specified number of columns. A default model is
   * created and the initial string is set to {@code null}. No restriction is placed on the number
   * of characters entered.
   *
   * @param columns the number of columns to use to calculate the preferred width; if columns is set
   *        to 0, the preferred width will be whatever naturally results from the component
   *        implementation.
   * @exception IllegalArgumentException if {@code columns} < 0.
   */
  public FixedLengthTextField(final int columns) {
    super(columns);
    initialise();
  }

  /**
   * Constructs a new text field initialised with the specified text and columns. A default model is
   * created. No restriction is placed on the number of characters entered.
   *
   * @param text the text to be displayed, or {@code null}.
   * @param columns the number of columns to use to calculate the preferred width; if columns is set
   *        to 0, the preferred width will be whatever naturally results from the component
   *        implementation.
   * @exception IllegalArgumentException if {@code columns} < 0.
   */
  public FixedLengthTextField(final String text, final int columns) {
    super(text, columns);
    initialise();
  }

  /**
   * Constructs a new text field that uses the given text storage model and the given number of
   * columns. If the document is {@code null}, a default model is created. No restriction is placed
   * on the number of characters entered.
   *
   * @param doc the text storage to use; if this is {@code null}, a default will be provided by
   *        calling the {@code createDefaultModel} method.
   * @param text the initial string to display, or {@code null}.
   * @param columns the number of columns to use to calculate the preferred width >= 0; if
   *        {@code columns} is set to 0, the preferred width will be whatever naturally results from
   *        the component implementation.
   * @exception IllegalArgumentException if {@code columns} < 0.
   */
  public FixedLengthTextField(final Document doc, final String text, final int columns) {
    super(doc, text, columns);
    initialise();
  }

  /**
   * Constructs a new empty text field component initialised with the specified number of columns
   * and maximum number of characters allowed for entry. The initial string is set to {@code null}.
   *
   * @param columns the number of columns to use to calculate the preferred width >= 0; if
   *        {@code columns} is set to 0, the preferred width will be whatever naturally results from
   *        the component implementation.
   * @param maximumCharacters maximum number of characters allowed for entry; if specified as 0 then
   *        no restrictions are placed on the number of characters entered.
   * @exception IllegalArgumentException if {@code maximumCharacters} or {@code columns} < 0.
   */
  public FixedLengthTextField(final int columns, final int maximumCharacters) {
    super(columns);
    if (maximumCharacters < 0) {
      throw new IllegalArgumentException(
          "Maximum allowed characters for entry less than zero.");
    }
    maxCharactersAllowed = maximumCharacters;
    initialise();
  }

  private void initialise() {
    // If enterable maximum is specified then override the document model
    // with our specialisation.
    if (maxCharactersAllowed > 0) {
      setDocument(new FixedLengthDocument(this, maxCharactersAllowed));
    }
    ToolTipManager.sharedInstance().registerComponent(this);
  }

  /**
   * Get maximum number of enterable characters allowed.
   *
   * @return maximum number of characters allowed to be entered >= 0.
   */
  public final int getMaximumNumberOfCharactersAllowed() {
    return maxCharactersAllowed;
  }

  /**
   * Set maximum enterable characters.
   *
   * @param maximumCharacters maximum number of characters allowed for entry; if specified as 0 then
   *        no restrictions are placed on the number of characters entered.
   * @exception IllegalArgumentException if {@code maximumCharacters} is less than 0.
   */
  public final void setMaximumNumberOfCharactersAllowed(final int maximumCharacters) {
    if (maximumCharacters < 0) {
      throw new IllegalArgumentException(
          "Maximum allowed characters for entry less than zero.");
    }
    maxCharactersAllowed = maximumCharacters;
    setDocument(
        (maxCharactersAllowed > 0) ? new FixedLengthDocument(this, maxCharactersAllowed) : createDefaultModel());
  }

  /**
   * This method will only be called if application wide ToolTip support is turned on. It will only
   * return a valid text field's contents if they are partially hidden.
   *
   * @return tooltip to display if text contents are partially hidden, otherwise {@code null} is
   *         returned and therefore no tooltip is displayed by the {@code ToolTipManager}.
   */
  @Override
  public final String getToolTipText() {
    final String textContents = getText();
    if (textContents != null) {
      final FontUIResource fontUIResource = (FontUIResource) UIManager.get("TextField.font");
      final FontMetrics fontMetrics = getFontMetrics(fontUIResource);
      final int textWidth = fontMetrics.stringWidth(textContents);
      if (textWidth > 0) {
        final int displayedColumnWidth = getColumns() * getColumnWidth();
        if (textWidth > displayedColumnWidth) {
          return PresentationUtilities.convertStringToHTMLFormatWithLineBreaks(textContents,
              PresentationUtilities.getDefaultColumnsToDisplayForTextComponents());
        }
      }
    }
    return null;
  }
}
