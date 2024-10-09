package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.FontMetrics;

import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * This class extends {@code JTextField} and allows this class to render it as a read only field
 * which makes it non-editable.<br>
 * This class also provides the facility to offer intelligent ToolTip support whereby the ToolTip
 * will only be displayed if the text contents are partially hidden. This will only be enabled if
 * the application wide ToolTip support is enabled.
 */
@SuppressWarnings("serial")
public final class ReadOnlyTextField extends JTextField {

  /**
   * Creates a read only text field with required background rendering and makes the component
   * non-editable.<br>
   * A default model is created, the initial string is {@code null}, and the number of columns is
   * set to 0. The preferred width of the read only text field will be whatever naturally results
   * from the component implementation.
   */
  public ReadOnlyTextField() {
    this(null, 0);
  }

  /**
   * Creates a read only text field with required background rendering and makes the component
   * non-editable.<br>
   * This is the constructor through which the other constructors feed. This component is
   * initialised with the specified text and columns. A default model is created.
   *
   * @param text the text to be displayed, or {@code null}.
   * @param columns the number of columns to use to calculate the preferred width; if columns is set
   *        to 0, the preferred width will be whatever naturally results from the component
   *        implementation.
   * @exception IllegalArgumentException if {@code columns} < 0.
   */
  public ReadOnlyTextField(final String text, final int columns) {
    super(text, columns);
    initialise();
  }

  /**
   * Creates a read only text field with required background rendering and makes the component
   * non-editable.<br>
   * This component is initialised with the specified text. A default model is created and the
   * number of columns is 0.
   *
   * @param text the text to be displayed, or {@code null}.
   */
  public ReadOnlyTextField(final String text) {
    this(text, 0);
  }

  /**
   * Creates a read only text field with required background rendering and makes the component
   * non-editable.<br>
   * This component is initialised with the specified columns. A default model is created and the
   * initial string is set to {@code null}.
   *
   * @param columns the number of columns to use to calculate the preferred width; if columns is set
   *        to 0, the preferred width will be whatever naturally results from the component
   *        implementation.
   * @exception IllegalArgumentException if {@code columns} < 0.
   */
  public ReadOnlyTextField(final int columns) {
    this(null, columns);
  }

  private void initialise() {
    setEditable(false);
    if (PresentationUtilities.isApplicationToAdhereToTMNStyleGuide()) {
      setBackground(PresentationUtilities.TMN_READ_ONLY_FIELDS_BG_COLOUR);
    }
    ToolTipManager.sharedInstance().registerComponent(this);
  }

  /**
   * This method will only be called if application wide ToolTip support is turned on. This method
   * will only return a valid text field's contents if they are partially hidden.
   *
   * @return tooltip to display if text contents are partially hidden, otherwise {@code null} is
   *         returned and therefore no tooltip is displayed by the {@code ToolTipManager}.
   */
  @Override
  public String getToolTipText() {
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
