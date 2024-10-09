package com.nea.patient.access.portal.ui.swingextensions.presentationshared.statusbar;

/**
 * This Interface provides the means for a component to be added to the {@code StatusBar}. This
 * interface defines methods that are required for displaying the component in the correct manner on
 * the {@code StatusBar}.
 */
public interface StatusBarComponent {

  /**
   * The preferred width, in pixels, that the {@code StatusBarComponent} is to occupy.
   *
   * @return the width, in pixels, of the {@code StatusBarComponent}.
   */
  int getPreferredPixelWidth();

  /**
   * Updates the preferred width, in pixels, of the {@code StatusBarComponent}.
   *
   * @param newWidth the new width, in pixels, of the {@code StatusBarComponent}.
   */
  void setPreferredPixelWidth(int newWidth);

  /**
   * Get the priority that this {@code StatusBarComponent} has been given.
   *
   * @return the priority of this {@code StatusBarComponent} which is either
   *         {@code StatusBar.HIGH_PRIORITY}, or {@code StatusBar.LOW_PRIORITY}.
   */
  int getPriority();
}
