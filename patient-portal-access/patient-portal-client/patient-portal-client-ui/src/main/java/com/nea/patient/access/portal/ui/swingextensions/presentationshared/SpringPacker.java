package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

/**
 * This class provides an implementation of the "Labelled Widget" paradigm commonly used with data
 * entry windows. Normally a labelled widget (i.e. labels aligned in a left-hand column and the
 * widgets they refer to aligned in the right-hand column) can be implemented using the Java Box
 * class or a Grid class. However, these layout managers only work within a container and do not
 * preserve alignment across tables. The {@code SpringPacker} (so named because it is based on the
 * {@code SpringLayout} manager) provides alignment across containers. Therefore, this class
 * supports rows formatted as a label followed by one or two {@code JComponent}s. The labels and
 * {@code JComponent}s are columnated.<br>
 * This class is used in combination with {@code SpringPackerEqualiser} to provide consistent
 * columnation across multiple {@code SpringPacker}s which can be in different {@code JPanel}s.<br>
 * This class implements the {@code Scrollable} interface, which provides control information to a
 * scrolling container like {@code JScrollPane}. If this panel is added to a {@code JScrollPane}'s
 * view port the method {@code setVisibleRowCount} allows setting of the preferred number of rows
 * for this panel's components that can be displayed without a scrollbar. The preferred visible row
 * count will only be realistically honoured if the components added to each row are of the same
 * height and are not resizeable. The default visible row count value held by this class is 0 and if
 * not overridden the preferred size of the panel is returned when queried.
 */
@SuppressWarnings("serial")
public final class SpringPacker extends JPanel implements Scrollable {

  // 4 pixels between the left edge of panel and left of label.
  private static final int X_INSET = 4;

  // 8 pixels between the label and value column.
  private static final int COLUMN_SEPARATION = 8;

  private static final Spring columnSeparation = Spring.constant(COLUMN_SEPARATION);

  // Add 3 pixel gap between rows.
  private static final int VERTICAL_SPACING = 3;

  private static final Spring verticalSpacing = Spring.constant(VERTICAL_SPACING);

  private static final int LABEL_HALIGNMENT = SwingConstants.LEADING;

  private static final int LABEL_VALIGNMENT = SwingConstants.CENTER;

  private static final String separator = " :";

  private List<JLabel> labels;

  private List<ComponentField> componentsColumn1;

  private List<ComponentField> componentsColumn2;

  // Default to single column of components.
  private int nComponentColumns = 1;

  private SpringLayout layout;

  // Current widths of the three columns.
  private Spring labelWidth;

  private Spring component1Width;

  private Spring component2Width;

  // Current total width of the three columns (maintained as a convenience so the three Springs
  // don't have to keep
  // being added together).
  private Spring totalComponentWidth;

  private Spring rowHeight;

  // Current y-offset down the JPanel.
  private Spring y;

  private int visibleRowCount = 0;

  /**
   * Constructs a new {@code JPanel} providing implementation of the "Labelled Widget" paradigm by
   * the use of the {@code SpringLayout} manager.
   */
  public SpringPacker() {
    super();
    labels = new ArrayList<JLabel>();
    componentsColumn1 = new ArrayList<ComponentField>();
    componentsColumn2 = new ArrayList<ComponentField>();
    layout = new SpringLayout();
    setLayout(layout);
    final int BORDER_OFFSET = 2;
    setBorder(new javax.swing.border.EmptyBorder(BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET));
    labelWidth = Spring.constant(0);
    component1Width = Spring.constant(0);
    component2Width = Spring.constant(0);
    totalComponentWidth = Spring.constant(0);
    rowHeight = Spring.constant(0);
    y = Spring.constant(VERTICAL_SPACING);
  }

  /**
   * Specify a label and a {@code JComponent}. The label horizontal alignment is defaulted to left
   * and vertical alignment defaulted to centre.
   *
   * @param labelString value to supply to create instance of {@code JLabel}.
   * @param field component to display to the right of the label.
   */
  public void addRow(final String labelString, final ComponentField field) {
    addRow(labelString, LABEL_HALIGNMENT, LABEL_VALIGNMENT, field);
  }

  /**
   * Specify a label and a {@code JComponent}. The label horizontal alignment is defaulted to left
   * and vertical alignment is set to that supplied.
   *
   * @param labelString value to supply to create instance of {@code JLabel}.
   * @param labelVertAlignment value represents either {@code JLabel.TOP/CENTER/BOTTOM}.
   * @param field component to display to the right of the label.
   */
  public void addRow(final String labelString, final int labelVertAlignment, final ComponentField field) {
    addRow(labelString, LABEL_HALIGNMENT, labelVertAlignment, field);
  }

  /**
   * Specify a label and a {@code JComponent}. The label horizontal and vertical alignment can be
   * specified using Java alignment constants to make the label left/right aligned and top, centre
   * or bottom aligned.
   *
   * @param labelString value to supply to create instance of {@code JLabel}.
   * @param labelHorizAlignment value represents either {@code JLabel.LEADING/LEFT/RIGHT}.
   * @param labelVertAlignment value represents either {@code JLabel.TOP/CENTER/BOTTOM}.
   * @param field component to display to the right of the label.
   */
  public void addRow(final String labelString, final int labelHorizAlignment, final int labelVertAlignment,
      final ComponentField field) {
    // Create a label from the String.
    JLabel label = null;
    if (labelString.length() > 0) {
      label = new JLabel(labelString + separator, labelHorizAlignment);
      label.setVerticalAlignment(labelVertAlignment);
    } else {
      label = new JLabel();
    }
    addRow(label, field);
  }

  /**
   * Supply own instance of {@code JLabel} representing the left hand column label and a
   * {@code JComponent}.
   *
   * @param label
   * @param field component to display to the right of the label.
   */
  public void addRow(final JLabel label, final ComponentField field) {
    // Record the label in the labels Vector (for later packing calculations) and add to the
    // JPanel.
    labels.add(label);
    add(label);

    // Update the maximum label width for this panel in case this label
    // is wider than anything that has been added before it.
    final SpringLayout.Constraints labelConstraints = layout.getConstraints(label);
    labelWidth = Spring.max(labelWidth, labelConstraints.getWidth());

    // Record the component in the first component Vector (for later packing calculations) and
    // add to the JPanel.
    componentsColumn1.add(field);
    add(field.getComponent());

    // Update the maximum component width for the first component column of this panel
    // in case this component is wider than anything that has been added before it.
    // If the component spans multiple columns then only update the total width; it
    // should not cause a specific column to get wider.
    final SpringLayout.Constraints component1Constraints = layout.getConstraints(field.getComponent());
    if (field.getColSpan() == 1) {
      component1Width = Spring.max(component1Width, component1Constraints.getWidth());
    } else {
      totalComponentWidth = Spring.max(totalComponentWidth, component1Constraints.getWidth());
    }

    // This method only takes one component field, therefore add a dummy ComponentField for
    // the second column.
    componentsColumn2.add(new ComponentField(null));

    // Find the height of the tallest thing on the row (either the label or, more likely, the
    // Component.
    final Spring height = Spring.max(labelConstraints.getHeight(), component1Constraints.getHeight());

    // Update the maximum row height for this panel.
    rowHeight = Spring.max(rowHeight, height);

    // Set the label and component to the same height and position them at the current y-offset.
    labelConstraints.setY(y);
    labelConstraints.setHeight(height);
    component1Constraints.setY(y);
    if (field.getVerticallyResizable()) {
      component1Constraints.setHeight(height);
    }
    // Update the y-offset to move it past this row.
    y = Spring.sum(y, Spring.sum(height, verticalSpacing));
  }

  /**
   * Specify a label and two {@code JComponent}s. This gives two component columns, e.g. 2 columns
   * of buttons. The label horizontal alignment is defaulted to left and vertical alignment
   * defaulted to centre.
   *
   * @param labelString value to supply to create instance of {@code JLabel}.
   * @param field1 component to display to the right of the label.
   * @param field2 component to display to the right of the first component.
   */
  public void addRow(final String labelString, final ComponentField field1, final ComponentField field2) {
    addRow(labelString, LABEL_HALIGNMENT, LABEL_VALIGNMENT, field1, field2);
  }

  /**
   * Specify a label and two {@code JComponent}s. This gives two component columns, e.g. 2 columns
   * of buttons. The label horizontal alignment is defaulted to left and vertical alignment is set
   * to that supplied.
   *
   * @param labelString value to supply to create instance of {@code JLabel}.
   * @param labelVertAlignment value represents either {@code JLabel.TOP/CENTER/BOTTOM}.
   * @param field1 component to display to the right of the label.
   * @param field2 component to display to the right of the first component.
   */
  public void addRow(final String labelString, final int labelVertAlignment, final ComponentField field1,
      final ComponentField field2) {
    addRow(labelString, LABEL_HALIGNMENT, labelVertAlignment, field1, field2);
  }

  /**
   * Specify a label and two {@code JComponent}s. This gives two component columns, e.g. 2 columns
   * of buttons. The label horizontal and vertical alignment can be specified using Java alignment
   * constants to make the label left/right aligned and top, centre or bottom aligned.
   *
   * @param labelString value to supply to create instance of {@code JLabel}.
   * @param labelHorizAlignment value represents either {@code JLabel.LEADING/LEFT/RIGHT}.
   * @param labelVertAlignment value represents either {@code JLabel.TOP/CENTER/BOTTOM}.
   * @param field1 component to display to the right of the label.
   * @param field2 component to display to the right of the first component.
   */
  public void addRow(final String labelString, final int labelHorizAlignment, final int labelVertAlignment,
      final ComponentField field1,
      final ComponentField field2) {
    // Create a label from the String
    JLabel label = null;
    if (labelString.length() > 0) {
      label = new JLabel(labelString + separator, labelHorizAlignment);
      label.setVerticalAlignment(labelVertAlignment);
    } else {
      label = new JLabel();
    }
    addRow(label, field1, field2);
  }

  /**
   * Supply own instance of {@code JLabel} representing the left hand column label and two
   * {@code JComponent}s.
   *
   * @param label
   * @param field1 component to display to the right of the label.
   * @param field2 component to display to the right of the first component.
   */
  public void addRow(final JLabel label, final ComponentField field1, final ComponentField field2) {
    nComponentColumns = 2;

    // Record the label in the labels Vector (for later packing calculations) and add to the
    // JPanel.
    labels.add(label);
    add(label);

    // Update the maximum label width for this panel in case this label
    // is wider than anything that has been added before it.
    final SpringLayout.Constraints labelConstraints = layout.getConstraints(label);
    labelWidth = Spring.max(labelWidth, labelConstraints.getWidth());

    // Record the component in the first component Vector (for later packing calculations) and
    // add to the JPanel.
    componentsColumn1.add(field1);
    add(field1.getComponent());

    // Update the maximum component width for the first component column of this panel
    // in case this component is wider than anything that has been added before it.
    // This component cannot be spanning 2 columns because there is another component further
    // along the row.
    final SpringLayout.Constraints component1Constraints = layout.getConstraints(field1.getComponent());
    component1Width = Spring.max(component1Width, component1Constraints.getWidth());

    // Record the component in the second component Vector (for later packing calculations) and
    // add to the JPanel.
    componentsColumn2.add(field2);
    add(field2.getComponent());

    // Update the maximum component width for the second component column of this panel
    // in case this component is wider than anything that has been added before it.
    // If this component spans multiple columns then let it hang off the end because this
    // component is
    // in the last column.
    final SpringLayout.Constraints component2Constraints = layout.getConstraints(field2.getComponent());
    if (field2.getColSpan() == 1) {
      component2Width = Spring.max(component2Width, component2Constraints.getWidth());
    } else {
      // Since this component spans multiple columns then only update the total width; it
      // should not cause a specific column to get wider.
      totalComponentWidth = Spring.max(totalComponentWidth, Spring.sum(component1Constraints.getWidth(),
          component2Constraints.getWidth()));
    }

    // Find the height of the tallest thing on the row (either the label or, more likely, one of
    // the Components.
    Spring height = Spring.max(labelConstraints.getHeight(), component1Constraints.getHeight());
    height = Spring.max(height, component2Constraints.getHeight());

    // Update the maximum row height for this panel.
    rowHeight = Spring.max(rowHeight, height);

    // Set the label and components to the same height and position them at the current
    // y-offset.
    labelConstraints.setY(y);
    labelConstraints.setHeight(height);
    component1Constraints.setY(y);
    component2Constraints.setY(y);

    if (field1.getVerticallyResizable()) {
      component1Constraints.setHeight(height);
    }
    if (field2.getVerticallyResizable()) {
      component2Constraints.setHeight(height);
    }
    // Update the y-offset to move it past this row.
    y = Spring.sum(y, Spring.sum(height, verticalSpacing));
  }

  /**
   * Accessor method which is used by class {@code SpringPackerEqualiser}.
   * 
   * @return number of component columns.
   */
  public int getNComponentColumns() {
    return nComponentColumns;
  }

  /**
   * Accessor method which is used by class {@code SpringPackerEqualiser}.
   * 
   * @return label column width.
   */
  public Spring getLabelsWidth() {
    return labelWidth;
  }

  /**
   * Accessor method which is used by class {@code SpringPackerEqualiser}. Allow the equaliser to
   * force the label column wider if there is another {@code SpringPacker} containing a wider label
   * than computed widest label.
   *
   * @param newLabelWidth override our widest label width with that supplied.
   */
  public void setLabelsWidth(final Spring newLabelWidth) {
    labelWidth = newLabelWidth;
  }

  /**
   * Accessor method which is used by class {@code SpringPackerEqualiser}.
   *
   * @param columnNo width of component in specified column.
   * @return specified column width.
   */
  public Spring getComponentWidth(final int columnNo) {
    if (columnNo == 0) {
      return component1Width;
    }
    return component2Width;
  }

  /**
   * Accessor method which is used by class {@code SpringPackerEqualiser}.
   *
   * @return total component width.
   */
  public Spring getTotalComponentWidth() {
    // Return either the running totalComponentWidth or the sum of the column component widths,
    // whichever is the greatest. The values may be different if component1Width or
    // component2Width have been updated by the SpringPackerEqualiser. It is also
    // possible that totalComponentWidth is still larger if a particularly wide
    // component has been added with colspan == 2 so it is not included in either of
    // the individual column widths.
    if (nComponentColumns == 1) {
      return Spring.max(totalComponentWidth, component1Width);
    }
    return Spring.max(totalComponentWidth, Spring.sum(Spring.sum(component1Width, columnSeparation),
        component2Width));
  }

  /**
   * Accessor method which is used by class {@code SpringPackerEqualiser}. Allows the equaliser to
   * force the component column wider if there is another {@code SpringPacker} containing a wider
   * component in this column than the widest component.
   *
   * @param columnNo the column for which to set the component width.
   * @param componentWidth the component width to set.
   */
  public void setComponentWidth(final int columnNo, final Spring componentWidth) {
    if (columnNo == 0) {
      component1Width = componentWidth;
    } else {
      if (columnNo == 1) {
        component2Width = componentWidth;
      }
    }
  }

  /**
   * Layout the components in this {@code SpringPacker} panel; this would be called after the
   * {@code SpringPackerEqualizer} has done its job if there are multiple {@code SpringPacker}s.
   */
  public void pack() {
    // Keep track of current offset from left margin.
    Spring x = Spring.constant(X_INSET);

    // Force the width of the labels to match the widest in the column.
    for (JLabel label : labels) {
      final SpringLayout.Constraints constraints = layout.getConstraints(label);
      constraints.setX(x);
      constraints.setWidth(labelWidth);
    }
    // Add the width of this column to the offset from the left margin.
    x = Spring.sum(x, Spring.sum(labelWidth, columnSeparation));

    // Get the total component width: note this is freshly calculated by the
    // method rather than relying on the running total.
    final Spring newTotalComponentWidth = getTotalComponentWidth();

    // Force the width of the components in the first column to match the widest in the column
    // or, if the component has colspan == 2, make it the total width of the component area.
    for (ComponentField c : componentsColumn1) {
      final SpringLayout.Constraints constraints = layout.getConstraints(c.getComponent());
      constraints.setX(x);
      if (c.getHorizontallyResizable()) {
        if (c.getColSpan() == 1) {
          constraints.setWidth(component1Width);
        } else {
          if (c.getColSpan() == 2) {
            constraints.setWidth(newTotalComponentWidth);
          }
        }
      }
    }
    // Add the width of this column to the offset from the left margin.
    x = Spring.sum(x, Spring.sum(component1Width, columnSeparation));

    // Force the width of the components in the second column to match the widest in the column.
    for (ComponentField c : componentsColumn2) {
      // Ensure that a dummy component has not been added.
      final JComponent component = c.getComponent();
      if (component != null) {
        final SpringLayout.Constraints constraints = layout.getConstraints(component);
        constraints.setX(x);
        if (c.getHorizontallyResizable() && c.getColSpan() == 1) {
          constraints.setWidth(component2Width);
        }
      }
    }
    // Add the width of this column to the offset from the left margin.
    x = Spring.sum(Spring.sum(labelWidth, columnSeparation), Spring.sum(newTotalComponentWidth, columnSeparation));

    // Set the panel's size.
    final SpringLayout.Constraints packerCons = layout.getConstraints(this);
    packerCons.setConstraint(SpringLayout.SOUTH, y);
    packerCons.setConstraint(SpringLayout.EAST, x);
  }

  /**
   * Returns the preferred number of visible rows.
   *
   * @return the preferred number of rows to display without using a scroll bar.
   * @see #setVisibleRowCount
   */
  public int getVisibleRowCount() {
    return visibleRowCount;
  }

  /**
   * Sets the preferred number of rows for this {@code JPanel}'s components that can be displayed
   * without a scrollbar, as determined by the nearest {@code JViewport} ancestor, if any.
   * <p>
   * The default value of this property is 0.
   * </p>
   * This method should be called if this {@code JPanel} is required to be added to a scrollable
   * {@code JViewport} ancestor and the preferred number of rows that can be displayed without a
   * scrollbar is required to be set. In addition, the preferred visible row count will only be
   * realistically honoured if the components added to each row are of the same height and are not
   * resizeable.
   *
   * @param preferredRowCount specifying the preferred number of visible rows.
   * @see JViewport
   */
  public void setVisibleRowCount(final int preferredRowCount) {
    visibleRowCount = Math.max(0, preferredRowCount);
  }

  // The following five methods implement the Scrollable interface methods.
  // By overriding the Scrollable interface, it provides information to a scrolling
  // container like JScrollPane.

  /**
   * This method is implementation of one method of interface {@code Scrollable}.<br>
   * Computes the size of the view port needed to display {@code visibleRowCount} rows. The height
   * of the largest row component is multiplied by {@code visibleRowCount} and allowing for any row
   * separation. If the row components are of different heights then this method will return a value
   * that will produce unpredictable results.<br>
   * If for some reason the row component width and height were unable to be computed then 16 pixels
   * per visible row, and 256 pixels for the width is allocated.<br>
   * If {@code visibleRowCount} returns the default value 0, then this method will return the value
   * from {@code getPreferredSize}.
   *
   * @return the size of the view port needed to display {@code visibleRowCount} rows, or value from
   *         {@code getPreferredSize} if {@code visibleRowCount} is the default value 0.
   */
  @Override
  public Dimension getPreferredScrollableViewportSize() {
    if (getVisibleRowCount() <= 0) {
      return getPreferredSize();
    }
    final Insets insets = getInsets();
    final int dx = insets.left + insets.right;
    final int dy = insets.top + insets.bottom;

    final int preferredRowCount = getVisibleRowCount();

    // Get the total component width.
    final Spring newCellWidth = Spring.sum(Spring.sum(Spring.constant(X_INSET), labelWidth), Spring.sum(
        columnSeparation, getTotalComponentWidth()));

    if ((newCellWidth.getValue() > 0) && (rowHeight.getValue() > 0)) {
      final int width = newCellWidth.getValue() + dx;
      final int height = (preferredRowCount * (rowHeight.getValue() + verticalSpacing.getValue())) + dy;
      return new Dimension(width, height);
    }
    final int FIXED_CELL_WIDTH = 256;
    final int FIXED_CELL_HEIGHT = 16;
    return new Dimension(FIXED_CELL_WIDTH, FIXED_CELL_HEIGHT * preferredRowCount);
  }

  /**
   * This method is implementation of one method of interface {@code Scrollable}.<br>
   * Returns the distance to scroll to expose the next or previous block. If scrolling down, the
   * last visible element should become the first completely visible element. If scrolling up, the
   * first visible element should become the last completely visible element.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orietation, final int direction) {
    final int MIN_BLOCK_INCREMENT = 50;
    int blockIncrement = MIN_BLOCK_INCREMENT;

    final Dimension preferredRect = getPreferredScrollableViewportSize();

    if ((visibleRect != null) && (preferredRect != null)) {
      blockIncrement = visibleRect.height;
      if (rowHeight.getValue() > 0) {
        final int rectDiff = visibleRect.height - preferredRect.height;
        final Insets insets = getInsets();
        final int offset = rowHeight.getValue() + verticalSpacing.getValue() + insets.top + insets.bottom;
        final int height = visibleRect.height - rectDiff - offset;
        if (height > 0) {
          blockIncrement = height;
        }
      }
    }
    return blockIncrement;
  }

  /**
   * This method is implementation of one method of interface {@code Scrollable}.<br>
   * Returns {@code true} if this {@code JPanel} is displayed in a {@code JViewport} and the view
   * port is taller than {@code JPanel}'s preferred height, or if the number for
   * {@code visibleRowCount} is &lt;=0 otherwise returns {@code false}.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public boolean getScrollableTracksViewportHeight() {
    if (getVisibleRowCount() <= 0) {
      return true;
    }
    if (getParent() instanceof JViewport) {
      return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
    }
    return false;
  }

  /**
   * This method is implementation of one method of interface {@code Scrollable}.<br>
   * Returns {@code true} if this {@code JPanel} is displayed in a {@code JViewport} and the view
   * port is wider than {@code JPanel}'s preferred width, otherwise returns {@code false}. <br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public boolean getScrollableTracksViewportWidth() {
    if (getParent() instanceof JViewport) {
      return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
    }
    return false;
  }

  /**
   * This method is implementation of one method of interface {@code Scrollable}.<br>
   * Returns the distance to scroll to expose the next or previous row for vertical scrolling. <br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
    final int MIN_ROW_HEIGHT = 26;
    int distance = MIN_ROW_HEIGHT;

    if (rowHeight.getValue() > 0) {
      distance = rowHeight.getValue() + verticalSpacing.getValue();
    }
    return distance;
  }

  /**
   * A class to provide a wrapper around a {@code JComponent} and to store some other attributes
   * associated with it, such as its column span. This class allows a component to have its size
   * (vertical and/or horizontal) limited to its natural size.
   */
  public final static class ComponentField {

    private JComponent component;

    private boolean horizontallyResizable;

    private boolean verticallyResizable;

    private int colSpan;

    /**
     * Construct a wrapper around the supplied {@code JComponent} and allows this instance to be
     * added to the {@code SpringPacker} panel. The column span is by default 1 and the component is
     * not resizeable.
     *
     * @param theComponent the component to display.
     */
    public ComponentField(final JComponent theComponent) {
      this(theComponent, false, false, 1);
    }

    /**
     * Construct a wrapper around the supplied {@code JComponent} and allows this instance to be
     * added to the {@code SpringPacker} panel. The column span is by default 1 and the component is
     * not vertically resizeable.
     *
     * @param theComponent the component to display.
     * @param isHorizontallyResizable specifies whether this component is horizontally resizeable.
     */
    public ComponentField(final JComponent theComponent, final boolean isHorizontallyResizable) {
      this(theComponent, isHorizontallyResizable, false, 1);
    }

    /**
     * Construct a wrapper around the supplied {@code JComponent} and allows this instance to be
     * added to the {@code SpringPacker} panel. The component is not vertically resizeable.
     *
     * @param theComponent the component to display.
     * @param columnSpan number of columns the component is required to span.
     */
    public ComponentField(final JComponent theComponent, final int columnSpan) {
      this(theComponent, true, false, columnSpan);
    }

    /**
     * Construct a wrapper around the supplied {@code JComponent} and allows this instance to be
     * added to the {@code SpringPacker} panel.
     *
     * @param theComponent the component to display.
     * @param isHorizontallyResizable specifies whether this component is horizontally resizeable.
     * @param isVerticallyResizable specifies whether this component is vertically resizeable.
     * @param columnSpan number of columns the component is required to span.
     */
    public ComponentField(final JComponent theComponent, final boolean isHorizontallyResizable,
        final boolean isVerticallyResizable, final int columnSpan) {
      if (theComponent != null) {
        // Must do this first so that the max size is accounted for in the
        // constraints later.
        if (!isHorizontallyResizable) {
          if (!isVerticallyResizable) {
            theComponent.setMaximumSize(theComponent.getPreferredSize());
          } else {
            theComponent.setMaximumSize(new Dimension(theComponent.getPreferredSize().width,
                theComponent.getMaximumSize().height));
          }
        } else {
          if (!isVerticallyResizable) {
            theComponent.setMaximumSize(new Dimension(theComponent.getMaximumSize().width,
                theComponent.getPreferredSize().height));
          }
        }
        component = theComponent;
        horizontallyResizable = isHorizontallyResizable;
        verticallyResizable = isVerticallyResizable;
        colSpan = columnSpan;
      }
    }

    /**
     * Return component required to be displayed.
     *
     * @return component to be displayed
     */
    public JComponent getComponent() {
      return component;
    }

    /**
     * Return whether component to display is horizontally resizeable.
     *
     * @return {@code true} if component is horizontally resizeable, {@code false} otherwise.
     */
    public boolean getHorizontallyResizable() {
      return horizontallyResizable;
    }

    /**
     * Return whether component to display is vertically resizeable.
     *
     * @return {@code true} if component is vertically resizeable, {@code false} otherwise.
     */
    public boolean getVerticallyResizable() {
      return verticallyResizable;
    }

    /**
     * Return how many columns does the component to display span.
     *
     * @return number of display column taken by the component.
     */
    public int getColSpan() {
      return colSpan;
    }
  }
}
