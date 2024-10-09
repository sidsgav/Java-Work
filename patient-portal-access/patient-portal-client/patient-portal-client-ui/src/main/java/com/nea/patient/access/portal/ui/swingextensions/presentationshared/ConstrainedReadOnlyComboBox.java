package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * This class extends {@code JComboBox} and configures this component to be read only. This class
 * allows display column restriction to be set and provides intelligent ToolTip support for selected
 * ComboBox entry and displayed drop down list. The ToolTip will only be displayed if the text
 * contents are partially hidden. This will only be enabled if the application wide ToolTip support
 * is enabled. In addition, this class will ensure that the items displayed by this
 * {@code JComboBox} will be correctly aligned (i.e. numbers right aligned, text left aligned,
 * images with text description left aligned and images without any description center aligned).
 */
@SuppressWarnings("serial")
public final class ConstrainedReadOnlyComboBox extends JComboBox implements ItemListener,
    ToolTipValidation {

  private int maxDisplayedColumns = 0;

  private Dimension preferredWidthDim;

  private static final int ICON_TEXT_GAP = 4;

  /**
   * Create a read only {@code JComboBox} with a default data model and no column restriction. The
   * preferred width of the read only ComboBox will be whatever naturally results from the component
   * implementation.
   */
  public ConstrainedReadOnlyComboBox() {
    super();
    initialise();
  }

  /**
   * Create a read only {@code JComboBox} with a default data model and allows display column
   * restriction to be set.
   *
   * @param maximumDisplayedColumns the number of columns to use to calculate the preferred width >=
   *        0; if columns is set to zero, the preferred width will be whatever naturally results
   *        from the component implementation.
   * @exception IllegalArgumentException if {@code maximumDisplayedColumns} < 0.
   */
  public ConstrainedReadOnlyComboBox(final int maximumDisplayedColumns) {
    super();
    if (maximumDisplayedColumns < 0) {
      throw new IllegalArgumentException(
          "Maximum display columns less than zero.");
    }
    maxDisplayedColumns = maximumDisplayedColumns;
    initialise();
  }

  /**
   * Create a read only {@code JComboBox} that takes its items from an existing
   * {@code ComboBoxModel} and allows display column restriction to be set.
   *
   * @param model the model that provides the displayed list of items.
   * @param maximumDisplayedColumns the number of columns to use to calculate the preferred width >=
   *        0; if columns is set to zero, the preferred width will be whatever naturally results
   *        from the component implementation.
   * @exception IllegalArgumentException if {@code maximumDisplayedColumns} < 0.
   */
  public ConstrainedReadOnlyComboBox(final ComboBoxModel model, final int maximumDisplayedColumns) {
    super(model);
    if (maximumDisplayedColumns < 0) {
      throw new IllegalArgumentException(
          "Maximum display columns less than zero.");
    }
    maxDisplayedColumns = maximumDisplayedColumns;
    initialise();
  }

  /**
   * Create a read only {@code JComboBox} that takes its items from an existing
   * {@code ComboBoxModel} with no column restriction. The preferred width of the read only
   * {@code JComboBox} will be whatever naturally results from the component implementation.
   *
   * @param model the model that provides the displayed list of items.
   */
  public ConstrainedReadOnlyComboBox(final ComboBoxModel model) {
    this(model, 0);
  }

  /**
   * Create a read only {@code JComboBox} that contains the elements in the supplied array and
   * allows display column restriction to be set.
   *
   * @param items an array of objects to insert into the {@code JComboBox}.
   * @param maximumDisplayedColumns the number of columns to use to calculate the preferred width >=
   *        0; if columns is set to zero, the preferred width will be whatever naturally results
   *        from the component implementation.
   * @exception IllegalArgumentException if {@code maximumDisplayedColumns} < 0.
   */
  public ConstrainedReadOnlyComboBox(final Object[] items, final int maximumDisplayedColumns) {
    super(items);
    if (maximumDisplayedColumns < 0) {
      throw new IllegalArgumentException(
          "Maximum display columns less than zero.");
    }
    maxDisplayedColumns = maximumDisplayedColumns;
    initialise();
  }

  /**
   * Create a read only {@code JComboBox} that contains the elements in the supplied array with no
   * column restriction. The preferred width of the read only {@code JComboBox} will be whatever
   * naturally results from the component implementation.
   *
   * @param items an array of objects to insert into the {@code JComboBox}.
   */
  public ConstrainedReadOnlyComboBox(final Object[] items) {
    this(items, 0);
  }

  /**
   * Create a read only {@code JComboBox} that contains the elements in the supplied {@code Vector}
   * and allows display column restriction to be set.
   *
   * @param items the container that provides the displayed list of items.
   * @param maximumDisplayedColumns - the number of columns to use to calculate the preferred width
   *        >= 0; if columns is set to zero, the preferred width will be whatever naturally results
   *        from the component implementation.
   * @exception IllegalArgumentException if {@code maximumDisplayedColumns} < 0.
   */
  public ConstrainedReadOnlyComboBox(final Vector<?> items, final int maximumDisplayedColumns) {
    super(items);
    if (maximumDisplayedColumns < 0) {
      throw new IllegalArgumentException(
          "Maximum display columns less than zero.");
    }
    maxDisplayedColumns = maximumDisplayedColumns;
    initialise();
  }

  /**
   * Create a read only {@code JComboBox} that contains the elements in the supplied {@code Vector}
   * with no column restriction. The preferred width of the read only {@code JComboBox} will be
   * whatever naturally results from the component implementation.
   *
   * @param items the container that provides the displayed list of items.
   */
  public ConstrainedReadOnlyComboBox(final Vector<?> items) {
    this(items, 0);
  }

  private void initialise() {
    preferredWidthDim = new Dimension(0, 0);
    setEditable(false);
    addItemListener(this);
    // Override Combobox renderer to allow ToolTip support to be provided
    // on posted drop down list and in addition provide correct item
    // alignment rendering.
    setRenderer(new ComboBoxListCellRendererWithToolTipSupport(this));
  }

  /**
   * The following method implements the {@code ItemListener} interface. It is used to provide
   * ToolTip support for selected JComboBox entry. This is public as a consequence of the
   * implementation.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void itemStateChanged(final ItemEvent e) {
    setToolTipText(validateToolTipToDisplay(e.getItem(), true));
  }

  /**
   * This method is overridden to allow this class to restrict the width of the displayed ComboBox
   * if column restriction has been specified.
   *
   * @return preferred size.
   */
  @Override
  public Dimension getPreferredSize() {
    if ((getItemCount() == 0) || (maxDisplayedColumns == 0)) {
      return super.getPreferredSize();
    }
    if (maxDisplayedColumns > 0) {
      final int preferredWidth = maxDisplayedColumns * getColumnWidth();
      final int largestListEntryWidth = determineLargestListEntryWidth();
      if (largestListEntryWidth > preferredWidth) {
        // Add Combobox arrow button offset to width.
        preferredWidthDim.setSize(preferredWidth + getArrowButtonWidth(),
            super.getPreferredSize().height);
        return preferredWidthDim;
      }
    }
    return super.getPreferredSize();
  }

  /**
   * This method is overridden to allow this class to restrict the width of the displayed ComboBox
   * if column restriction has been specified.
   *
   * @return preferred size.
   */
  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  /**
   * This method is overridden to allow this class to restrict the width of the displayed ComboBox
   * if column restriction has been specified.
   *
   * @return preferred size.
   */
  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  // This method is similar to that of {@code JTextField}, which takes the width
  // of character <em>m</em> for the font used to determine the width of a single column.
  // This method is used in order to restrict the displayed width of the ComboBox.
  private int getColumnWidth() {
    final FontUIResource fontUIResource = (FontUIResource) UIManager.get("ComboBox.font");
    final FontMetrics fontMetrics = getFontMetrics(fontUIResource);
    return fontMetrics.charWidth('m');
  }

  // It is not possible to determine the size of the Arrow Down button (used when a
  // {@code JComboBox} component is rendered) unless we provide our own UI component
  // to render the {@code JComboBox} by extending class {@code BasicComboBoxUI}.
  // However, in doing this we have to provide extended versions for the different Look&Feel
  // representations. Therefore, by default assume the button width occupies two columns of
  // the displayed ComboBox.
  // The following method is used for the ToolTip calculation and also for restricting the
  // {@code JComboBox} width.
  private int getArrowButtonWidth() {
    return (getColumnWidth() * 2);
  }

  private int determineLargestListEntryWidth() {
    int largestEntryWidth = 0;

    final FontUIResource fontUIResource = (FontUIResource) UIManager.get("ComboBox.font");
    final FontMetrics fontMetrics = getFontMetrics(fontUIResource);

    final ComboBoxModel model = getModel();
    for (int i = 0; i < model.getSize(); i++) {
      final Object item = model.getElementAt(i);
      if (item != null) {
        final int textWidth = fontMetrics.stringWidth(item.toString());
        if (textWidth > largestEntryWidth) {
          largestEntryWidth = textWidth;
        }
      }
    }
    return largestEntryWidth;
  }

  /**
   * This method implements the only method of {@code ToolTipValidation} interface and will only
   * return valid ToolTip if text field contents are partially hidden.<br>
   * This method is used to validate Tooltip support for a {@code JComboBox} popup list items and
   * also for the {@code JComboBox} selected item, in which case the parameter
   * {@code listScrollBarDisplayed} supplied is set to {@code true} also as generally the width of
   * the {@code JComboBox} button is the same as the list scrollbar.
   *
   * @param value contents to convert to Tooltip.
   * @param listScrollBarDisplayed {@code true} if the {@code JComboBox} popup list's vertical
   *        scroll bar is displayed, {@code false} otherwise.
   * @return the Tooltip to display or {@code null}.
   */
  @Override
  public String validateToolTipToDisplay(final Object value, final boolean listScrollBarDisplayed) {
    String toolTipToSet = null;
    if (value != null) {
      final FontUIResource fontUIResource = (FontUIResource) UIManager.get("ComboBox.font");
      final FontMetrics fontMetrics = getFontMetrics(fontUIResource);
      int textWidth = fontMetrics.stringWidth(value.toString());
      if (value instanceof ImageIcon) {
        final ImageIcon imageIcon = (ImageIcon) value;
        final String description = imageIcon.getDescription();
        if ((description != null) && (description.length() > 0)) {
          // Calculate the width to include the width of the image and separation offset.
          textWidth = fontMetrics.stringWidth(description) + imageIcon.getIconWidth() +
              ICON_TEXT_GAP;
        } else {
          return null;
        }
      }
      int componentWidth = getWidth();
      if (listScrollBarDisplayed) {
        // It is assumed here the ComboBox button width is the same as the list scroll bar
        // width
        // and the ComboBox button width is approximated.
        componentWidth -= getArrowButtonWidth();
      }
      if (textWidth > componentWidth) {
        toolTipToSet = PresentationUtilities.convertStringToHTMLFormatWithLineBreaks(
            value.toString(),
            PresentationUtilities.getDefaultColumnsToDisplayForTextComponents());
      }
    }
    return toolTipToSet;
  }

  /**
   * Overridden method to allow default Tooltip value to be specified.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void setSelectedIndex(final int anIndex) {
    super.setSelectedIndex(anIndex);

    setToolTipText((anIndex == -1) ? null : validateToolTipToDisplay(getSelectedItem(), true));
  }
}
