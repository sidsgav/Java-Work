package com.nea.patient.access.portal.ui.swingextensions.presentationshared.statusbar.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 * Provides a custom border around components. Paints a single line border which provides a pushed
 * in 3d effect.
 */
@SuppressWarnings("serial")
public final class CustomBorder extends AbstractBorder {

  /**
   * Constructor
   */
  public CustomBorder() {}

  /**
   * Static method used to create a {@code CustomBorder} object.
   *
   * @return a custom border instance.
   */
  public static Border create() {
    return new CustomBorder();
  }

  /**
   * Returns the insets for this border. This is an overridden method from {@code AbstractBorder}.
   *
   * @param c the component for which this border insets value applies.
   * @return the new {@code Insets} object.
   */
  @Override
  public Insets getBorderInsets(final Component c) {
    return new Insets(1, 1, 1, 1);
  }

  /**
   * Paints the border with a single line providing a pushed in 3d effect.
   *
   * @param c the component for which this border is being painted.
   * @param g the paint graphics.
   * @param x the x position of the painted border.
   * @param y the y position of the painted border.
   * @param width the width of the painted border.
   * @param height the height of the painted border.
   */
  @Override
  public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width,
      final int height) {
    final int h = height - 1;
    final int w = width - 1;

    g.setColor(UIManager.getColor("Button.shadow"));
    g.drawLine(x, y, (x + w), y);
    g.drawLine(x, y, x, (y + h));

    g.setColor(UIManager.getColor("Button.light"));
    g.drawLine((x + w), y, (x + w), (y + h));
    g.drawLine(x, (y + h), (x + w), (y + h));
  }
}
