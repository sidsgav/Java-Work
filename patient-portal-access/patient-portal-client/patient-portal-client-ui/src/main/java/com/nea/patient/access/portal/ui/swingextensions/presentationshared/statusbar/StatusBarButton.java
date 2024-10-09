package com.nea.patient.access.portal.ui.swingextensions.presentationshared.statusbar;

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;

import com.nea.patient.access.portal.ui.swingextensions.presentationshared.statusbar.border.CustomBorder;

/**
 * A specialised {@code JButton} which is to be used when adding a button to the {@code StatusBar}.
 * It's envisaged that buttons on the {@code StatusBar} do not contain text (except as a tool tip),
 * but just an icon.<br>
 *
 * Icons which are being placed on a {@code StatusBarButton} should not be larger than 15x25 (height
 * x width) pixels in size. If this is the case, the {@code StatusBarButton} will throw an
 * exception.
 */
@SuppressWarnings("serial")
public final class StatusBarButton extends JButton implements StatusBarComponent {

  private final int MAX_ICON_HEIGHT = 15; // pixels

  private final int MAX_ICON_WIDTH = 25; // pixels

  private final int ICON_OFFSET = 2;

  private Icon icon;

  private int priority = StatusBar.LOW_PRIORITY;

  // Instance member to determine the visibility of this component.
  // Used for repainting the button.
  private boolean componentVisible = true;

  /**
   * This constructor will create a {@code StatusBarButton} with low priority.
   *
   * @param theIcon the {@code Icon} that is to be displayed on the button. Its size should not
   *        exceed 15x25 (height x width) pixels.
   * @throws Exception thrown if the button cannot be created for some reason, including if the
   *         {@code Icon} for the button exceeds the maximum allowed size.
   */
  public StatusBarButton(final Icon theIcon) throws Exception {
    super();
    setStatusBarButtonIcon(theIcon);
  }

  /**
   * This constructor will create a {@code StatusBarButton} with specified priority.
   *
   * @param theIcon the {@code Icon} that is to be displayed on the button. Its size should not
   *        exceed 15x25 (height x width) pixels.
   * @param thePriority the priority of this component, either {@code StatusBar.HIGH_PRIORITY} or
   *        {@code StatusBar.LOW_PRIORITY}.
   * @throws Exception thrown if the button cannot be created for some reason, including if the
   *         {@code Icon} for the button exceeds the maximum allowed size.
   */
  public StatusBarButton(final Icon theIcon, final int thePriority) throws Exception {
    super();
    setStatusBarButtonIcon(theIcon);
    setPriority(thePriority);
  }

  /**
   * Overridden method from super class which allows for the sizing and look & feel of this
   * component to be maintained when the look & feel of the application is changed.
   */
  @Override
  public void updateUI() {
    super.updateUI();
    setBorder(CustomBorder.create());
  }

  /**
   * This method is not applicable for a {@code StatusBarButton} as the width is determined by the
   * width of the {@code Icon} that has been set for this button. However, it is possible to change
   * the {@code Icon} for the {@code StatusBarButton} by calling
   * {@code #setStatusBarButtonIcon}.<br>
   * <br>
   * {@inheritDoc}
   *
   * @see #setStatusBarButtonIcon(Icon)
   */
  @Override
  public void setPreferredPixelWidth(final int newWidth) {
    // setPixels does nothing for a StatusBarButton, as it's size is defined
    // by the width of the icon. There is, however, a
    // setStatusBarButtonIcon(Icon) method to allow the icon to change
  }

  /**
   * Change the {@code Icon} for this {@code StatusBarButton}. If the {@code Icon} is changed (no
   * exception thrown), then a call to {@code StatusBar#arrange} needs to be made to update the
   * Status Bar.
   *
   * @param theIcon the {@code Icon} that is to be displayed on the button. Its size should not
   *        exceed 15x25 (height x width) pixels.
   * @throws Exception thrown if the {@code Icon} for the button exceeds the maximum allowed size.
   */
  public void setStatusBarButtonIcon(final Icon theIcon) throws Exception {
    // Check the Icons height & width
    if (theIcon.getIconHeight() > MAX_ICON_HEIGHT) {
      throw new Exception("Icon is greater than " + MAX_ICON_HEIGHT + " pixels in height");
    }
    if (theIcon.getIconWidth() > MAX_ICON_WIDTH) {
      throw new Exception("Icon is greater than " + MAX_ICON_WIDTH + " pixels in width");
    }
    icon = theIcon;
    super.setIcon(icon);
  }

  /**
   * Get the number of pixels in width that this {@code StatusBarButton} is to occupy. This is the
   * width of the icon that is being displayed on the button, plus a small offset.
   *
   * @return the width of the {@code StatusBarButton} in pixels.
   */
  @Override
  public int getPreferredPixelWidth() {
    return icon.getIconWidth() + ICON_OFFSET;
  }

  /**
   * Allow the priority of this {@code StatusBarButton} to be changed.
   *
   * @param thePriority the priority of this component, either {@code StatusBar.HIGH_PRIORITY} or
   *        {@code StatusBar.LOW_PRIORITY}.
   */
  public void setPriority(final int thePriority) {
    if ((thePriority >= StatusBar.LOW_PRIORITY) && (thePriority <= StatusBar.HIGH_PRIORITY)) {
      priority = thePriority;
    }
  }

  /**
   * Get the priority of this {@code StatusBarButton}.
   *
   * @return the priority of this component, either {@code StatusBar.HIGH_PRIORITY} or
   *         {@code StatusBar.LOW_PRIORITY}.
   */
  @Override
  public int getPriority() {
    return priority;
  }

  /**
   * Overridden method from super class. Don't actually want to change the visibility of the
   * component, as the parent container will ignore the component if it's not visible and not draw
   * it. Therefore, when a {@code StatusBarButton} is not visible there is an empty, border-less box
   * on the {@code StatusBar} which ruins the look.
   *
   * @param setVisible the visibility of this {@code StatusBarButton}.
   */
  @Override
  public void setVisible(final boolean setVisible) {
    componentVisible = setVisible;
    repaint();
  }

  /**
   * Overridden method from superclass. If the {@code StatusBarButton} is to be visible, then the
   * icon is painted otherwise an empty rectangle of the correct dimensions are displayed, which
   * still allows for the {@code StatusBarButton} to have the correct border and does not ruin the
   * look of the {@code StatusBar}.
   *
   * @param g the graphics object used to paint this component.
   */
  @Override
  public void paintComponent(final Graphics g) {
    if (componentVisible) {
      super.paintComponent(g);
    } else {
      // Must make sure that we filling in the background in a non-opaque colour.
      g.setColor(UIManager.getColor("Panel.background"));
      g.fillRect(0, 0, (icon.getIconWidth() + ICON_OFFSET), icon.getIconHeight());
    }
  }
}
