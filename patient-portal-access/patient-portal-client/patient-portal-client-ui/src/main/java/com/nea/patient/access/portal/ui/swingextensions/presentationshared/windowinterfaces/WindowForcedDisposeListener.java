package com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces;

/**
 * An interface which allows posted windows to be disposed at request. This action may be required
 * when the user logs off, the server connection is lost or the server forcefully logs the client
 * off.<br>
 * The components {@code MDialog} and {@code MFrame} currently implement this interface to support
 * {@code JFrame} and {@code JDialog} respectively.
 */
public interface WindowForcedDisposeListener {

  /**
   * Needs to be invoked on all launched {@code Frame} windows once the application has detected an
   * event which requires all secondary application windows to be disposed. This will allow each
   * {@code Frame} window to dispose any secondary child windows open before disposing of
   * themselves. The main primary {@code Frame} window should not dispose itself until it has
   * disposed all secondary windows.<br>
   * Alternatively, this method can simply be called when the window needs to be disposed (i.e.
   * through a Cancel/Close operation) including any of its launched child windows.
   */
  void doForcedDisposeAction();
}
