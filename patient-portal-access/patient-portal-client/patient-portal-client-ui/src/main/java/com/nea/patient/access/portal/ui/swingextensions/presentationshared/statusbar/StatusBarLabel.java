package com.nea.patient.access.portal.ui.swingextensions.presentationshared.statusbar;

import javax.swing.JLabel;
import javax.swing.UIManager;

import com.nea.patient.access.portal.ui.swingextensions.presentationshared.statusbar.border.CustomBorder;

/**
 * A specialised {@code JLabel} which is to be used when adding a component to the {@code StatusBar}
 * that displays text. The {@code StatusBarLabel} behaves like a {@code JLabel} in that the text
 * cannot be selected or modified. If a {@code StatusBarLabel} does not have enough room on the
 * {@code StatusBar} (the text will go past the end of the available space), then the {@code JLabel}
 * renderer will provide ellipses (...) to indicate that there is more text available than can be
 * displayed. This is the preferred behaviour.
 */
@SuppressWarnings("serial")
public final class StatusBarLabel extends JLabel implements StatusBarComponent {

  private int pixels = 0;

  private int priority = StatusBar.LOW_PRIORITY;

  /**
   * This constructor will set the priority of this {@code StatusBarLabel} depending on
   * {@code thePixels} value passed in. If {@code thePixels} == {@code StatusBar.REMAINDER} the
   * priority is set to {@code StatusBar.HIGH_PRIORITY} otherwise it is set to
   * {@code StatusBar.LOW_PRIORITY}.
   *
   * @param thePixels the width in pixels that this {@code StatusBarLabel} should occupy. The
   *        {@code StatusBarLabel} will always occupy that width, or {@code StatusBar.REMAINDER} if
   *        this {@code StatusBarLabel} can resize.
   */
  public StatusBarLabel(final int thePixels) {
    super();
    pixels = thePixels;

    // If this StatusBarLabel is set to use the remaining space, and a
    // priority is not provided, set it to be High Priority.
    if (pixels == StatusBar.REMAINDER) {
      setPriority(StatusBar.HIGH_PRIORITY);
    }
  }

  /**
   * Constructor.
   *
   * @param thePixels the width in pixels that this {@code StatusBarLabel} should occupy. The
   *        {@code StatusBarLabel} will always occupy that width, or {@code StatusBar.REMAINDER} if
   *        this {@code StatusBarLabel} can resize.
   * @param thePriority the priority of this component, either {@code StatusBar.HIGH_PRIORITY} or
   *        {@code StatusBar.LOW_PRIORITY}.
   */
  public StatusBarLabel(final int thePixels, final int thePriority) {
    super();
    pixels = thePixels;
    setPriority(thePriority);
  }

  /**
   * Overridden method of super class which allows for the sizing and look & feel of this component
   * to be maintained when the look & feel of the application is changed.
   */
  @Override
  public void updateUI() {
    super.updateUI();
    setBorder(CustomBorder.create());
    setBackground(UIManager.getColor("Panel.background"));
  }

  /**
   * Overrides {@code JLabel.setText} to include a space at the start of the text string. This
   * ensures the text isn't pressed up against the separator on the left hand side.
   *
   * @param text to be displayed in the {@code StatusBarLabel}.
   */
  @Override
  public void setText(final String text) {
    super.setText(" " + text);
  }

  /**
   * Changes the width of this {@code StatusBarLabel}.
   *
   * @param newWidth the width in pixels that this {@code StatusBarLabel} should occupy. The
   *        {@code StatusBarLabel} will always occupy that width, or {@code StatusBar.REMAINDER} if
   *        this {@code StatusBarLabel} can resize.
   */
  @Override
  public void setPreferredPixelWidth(final int newWidth) {
    pixels = newWidth;
  }

  /**
   * Get the number of pixels in width that this {@code StatusBarLabel} is to occupy.
   *
   * @return the width of the {@code StatusBarLabel} in pixels, or {@code StatusBar.REMAINDER} if
   *         this component can resize to occupy the remaining width.
   */
  @Override
  public int getPreferredPixelWidth() {
    return pixels;
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
}
