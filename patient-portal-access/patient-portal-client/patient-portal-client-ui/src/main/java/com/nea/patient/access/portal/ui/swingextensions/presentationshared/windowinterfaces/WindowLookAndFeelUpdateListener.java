package com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces;

/**
 * An interface used to allow posted windows to be updated when the Look And Feel setting is changed
 * by the user at application run time or through some other means.<br>
 * The components {@code MDialog} and {@code MFrame} currently implement this interface to support
 * {@code JFrame} and {@code JDialog} respectively.
 */
public interface WindowLookAndFeelUpdateListener {

  /**
   * This method needs to be called on all launched {@code Frame} windows once the application has
   * detected that the Look and Feel has changed at run time. This will allow each {@code Frame}
   * window to update the Look and Feel for any secondary child windows opened. The main primary
   * {@code Frame} window could be the class which allows a Look and Feel change and thus is the
   * controller for making this method call on all secondary {@code Frame} windows currently opened
   * including itself.
   */
  void updateUILookAndFeel();
}
