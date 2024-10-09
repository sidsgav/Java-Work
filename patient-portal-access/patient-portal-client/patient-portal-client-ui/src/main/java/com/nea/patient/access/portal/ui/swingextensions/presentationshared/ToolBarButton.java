package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * If a ToolBar button is required to be created then use this class to create the ToolBar button as
 * it ensures all buttons with small variations in image sizes are all drawn to the same size.
 * Otherwise if you add a {@code JButton} to {@code JToolBar} directly each button is drawn to be as
 * large as the image supplied producing a ToolBar presentation with different size buttons.<br>
 * This class does assume that the image supplied will not be larger than approximately 35x35
 * pixels. If the largest ToolBar image is larger or smaller than this then change the width and
 * height values accordingly through the overloaded constructor available.
 */
@SuppressWarnings("serial")
public final class ToolBarButton extends JButton {

  private int minimumButtonWidth = 39;

  private int minimumButtonHeight = 39;

  private Dimension dimension;

  private ImageIcon imageIcon;

  private String toolTipText;

  private boolean hideButtonText = true;

  /**
   * Create a ToolBar button with default width and height of 39 pixels and image set to that
   * supplied and Tooltip support and button text as taken from supplied {@code action} name
   * property.
   *
   * @param action the action used to specify the new button.
   * @param toolTip the tooltip to display for button.
   * @param icon the Icon image to display on the button.
   */
  public ToolBarButton(final Action action, final String toolTip, final ImageIcon icon) {
    super(action);
    toolTipText = toolTip;
    imageIcon = icon;
    initialise();
  }

  /**
   * Create a ToolBar button with preferred width and height set to that supplied and image set to
   * that supplied and Tooltip support and button text as taken from supplied {@code action} name
   * property.
   *
   * @param action the action used to specify the new button.
   * @param toolTip the tooltip to display for button.
   * @param icon the Icon image to display on the button.
   * @param preferredWidthAndHeight new preferred width and height for button.
   */
  public ToolBarButton(final Action action, final String toolTip, final ImageIcon icon,
      final int preferredWidthAndHeight) {
    super(action);
    toolTipText = toolTip;
    imageIcon = icon;
    minimumButtonWidth = preferredWidthAndHeight;
    minimumButtonHeight = preferredWidthAndHeight;
    initialise();
  }

  /**
   * Create a ToolBar button with preferred width and height set to that supplied and image set to
   * that supplied and Tooltip support and allows configuration of button text display.
   *
   * @param action the action used to specify the new button.
   * @param toolTip the tooltip to display for button.
   * @param icon the Icon image to display on the button.
   * @param preferredWidth new preferred width.
   * @param preferredHeight new preferred height.
   * @param hideText {@code true} if text of the button (as taken from {@code action} name property)
   *        is to be displayed, {@code false} otherwise.
   */
  public ToolBarButton(final Action action, final String toolTip, final ImageIcon icon,
      final int preferredWidth, final int preferredHeight, final boolean hideText) {
    super(action);
    toolTipText = toolTip;
    imageIcon = icon;
    minimumButtonWidth = preferredWidth;
    minimumButtonHeight = preferredHeight;
    hideButtonText = hideText;
    initialise();
  }

  private void initialise() {
    dimension = new Dimension(minimumButtonWidth, minimumButtonHeight);
    if (hideButtonText) {
      setText("");
    }
    setToolTipText(toolTipText);
    setIcon(imageIcon);
    setFocusPainted(false);
    setPreferredSize(dimension);
  }

  /**
   * Overridden method of super class to allow the required width and height {@code Dimension} to be
   * returned.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public Dimension getPreferredSize() {
    return dimension;
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
}
