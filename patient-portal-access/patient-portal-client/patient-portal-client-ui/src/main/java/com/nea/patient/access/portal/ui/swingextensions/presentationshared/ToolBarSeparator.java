package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * This class allows a ToolBar line decoration to be drawn when separating ToolBar buttons in a
 * similar fashion to Windows. This overcomes the default {@code JToolBar} decoration which is
 * simply a transparent empty rectangle under most Look&Feels. To use this class simply do
 * {@code JToolBar.add(new ToolBarSeparator())}. This class will decide whether the ToolBar
 * separator line needs to be drawn horizontally or vertically.
 */
@SuppressWarnings("serial")
public final class ToolBarSeparator extends JComponent implements PropertyChangeListener {

  private final int VERTICAL_LINE_WIDTH = 8;

  private final int VERTICAL_LINE_HEIGHT = 20;

  private final int HORIZONTAL_LINE_WIDTH = 20;

  private final int HORIZONTAL_LINE_HEIGHT = 8;

  private final int EDGE_OFFSET = 3;

  private Dimension dimension = new Dimension(VERTICAL_LINE_WIDTH, VERTICAL_LINE_HEIGHT);

  private boolean calculateDimension = false;

  /**
   * Allows a horizontal or vertical line to be drawn when required to separate buttons added to a
   * {@code JToolBar} component.
   */
  public ToolBarSeparator() {
    super();
    initialise();
  }

  private void initialise() {
    setPreferredSize(dimension);
  }

  /**
   * Overridden method of super class to allow a vertical or horizontal line separator to be
   * drawn.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);
    final Container parent = getParent();
    if (parent instanceof JToolBar) {
      if (((JToolBar) parent).getOrientation() == SwingConstants.HORIZONTAL) {
        paintVerticalLine(g);
      } else {
        paintHorizontalLine(g);
      }
    }
  }

  private void paintVerticalLine(final Graphics g) {
    final int centerWidth = (this.getWidth() / 2) - 1;
    final int compHeight = this.getHeight() - 1;
    final Color currentColor = g.getColor();
    g.setColor(new Color(SystemColor.controlShadow.getRGB()));
    g.drawLine(centerWidth, 0, centerWidth, compHeight);
    g.setColor(new Color(SystemColor.controlLtHighlight.getRGB()));
    g.drawLine(centerWidth + 1, 0, centerWidth + 1, compHeight);
    g.setColor(currentColor);
  }

  private void paintHorizontalLine(final Graphics g) {
    final int compWidth = this.getWidth() - 1;
    final int centerHeight = (this.getHeight() / 2) - 1;
    final Color currentColor = g.getColor();
    g.setColor(new Color(SystemColor.controlShadow.getRGB()));
    g.drawLine(0, centerHeight, compWidth, centerHeight);
    g.setColor(new Color(SystemColor.controlLtHighlight.getRGB()));
    g.drawLine(0, centerHeight + 1, compWidth, centerHeight + 1);
    g.setColor(currentColor);
  }

  /**
   * Overridden method of super class to allow the required width and height {@code Dimension} to be
   * returned.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public Dimension getPreferredSize() {
    final Dimension retDimension = super.getPreferredSize();
    if (calculateDimension) {
      final Container parent = getParent();
      if (parent instanceof JToolBar) {
        final JToolBar toolBar = (JToolBar) parent;
        final Rectangle toolBarBounds = toolBar.getBounds();
        if (toolBar.getOrientation() == SwingConstants.HORIZONTAL) {
          dimension.setSize(VERTICAL_LINE_WIDTH, ((toolBarBounds != null) ? toolBarBounds.height
              - (EDGE_OFFSET * 2) : VERTICAL_LINE_HEIGHT));
        } else {
          dimension.setSize(HORIZONTAL_LINE_WIDTH, ((toolBarBounds != null) ? toolBarBounds.width
              - (EDGE_OFFSET * 2) : HORIZONTAL_LINE_HEIGHT));
        }
        calculateDimension = false;
        setPreferredSize(dimension);
        return dimension;
      }
    }
    return retDimension;
  }

  /**
   * Overridden method of super class to allow the required width and height {@code Dimension} to be
   * returned.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  /**
   * Overridden method of super class to allow the required width and height {@code Dimension} to be
   * returned.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  /**
   * Notifies this component that it now has a parent component. When this method is invoked, the
   * chain of parent components is set up with {@code KeyboardAction} event listeners.<br>
   * This method is overridden to allow this component to add itself as a listener to the parent
   * {@code JToolBar} if applicable.
   */
  @Override
  public void addNotify() {
    super.addNotify();

    // Retrieve the parent of this component and determine if it's a JToolBar component.
    // If so add this component as a listener for UI Look and Feel changes in order for us to
    // re-calculate the width and height for the separator.
    final Container cont = this.getParent();
    if (cont instanceof JToolBar) {
      ((JToolBar) cont).addPropertyChangeListener("UI", this);
    }
  }

  /**
   * Implementation of interface {@code PropertyChangeListener}.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void propertyChange(final PropertyChangeEvent e) {
    calculateDimension = true;
    validate();
  }
}
