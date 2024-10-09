package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import javax.swing.text.Document;

/**
 * This class extends {@code FixedLengthTextField} and provides extra control such as checking for
 * printable characters or host address syntax for text entry components.
 */
@SuppressWarnings("serial")
public class ConstrainedTextField extends FixedLengthTextField {

  /**
   * Constructs a new empty text field. A default model is created, the initial string is
   * {@code null}, and the number of columns is set to 0. No restriction is placed on the number of
   * characters entered.
   */
  public ConstrainedTextField() {
    super();
  }

  /**
   * Constructs a new text field initialised with the specified text. A default model is created and
   * the number of columns is 0. No restriction is placed on the number of characters entered.
   *
   * @param text the text to be displayed, or {@code null}.
   */
  public ConstrainedTextField(final String text) {
    super(text);
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
  public ConstrainedTextField(final int columns) {
    super(columns);
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
  public ConstrainedTextField(final String text, final int columns) {
    super(text, columns);
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
  public ConstrainedTextField(final Document doc, final String text, final int columns) {
    super(doc, text, columns);
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
  public ConstrainedTextField(final int columns, final int maximumCharacters) {
    super(columns, maximumCharacters);
  }

  /**
   * Invoke to check whether printable characters have been entered.
   *
   * @return {@code true} if printable characters entered, {@code false} otherwise.
   */
  public final boolean isPrintable() {
    return PresentationUtilities.isPrintable(getText());
  }

  /**
   * Invoke to syntactically check for host address entry.
   *
   * @return {@code true} if host address syntax is correct, {@code false} otherwise.
   */
  public final boolean isHostAddress() {
    return PresentationUtilities.isHostAddress(getText());
  }
}
