package com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces;

/**
 * An interface which can be implemented by a subclass of {@code JFrame} or {@code JDialog} to allow
 * a busy cursor to be displayed/cleared, effectively locking/unlocking any window activity.<br>
 * This interface is implemented by two components : {@code LockableFrame} and
 * {@code LockableDialog} which provides support for {@code JFrame} and {@code JDialog}
 * respectively.
 */
public interface WindowLockControl {

  /**
   * Invoke if the application is required to talk to a network interface, which requires the window
   * to be locked (preventing further user activity) until the response from the network is
   * returned. An example is an asynchronous request made over an interface medium or the
   * application making a database request, both of which may take an indeterminate time.<br>
   * This method could be used to lock any opened child windows of the parent window (as long as
   * each child window implements this interface) to provide a cascade application lock.
   */
  void enableWindowLockedState();

  /**
   * Invoke once the response from the network interface request is returned and the window locked
   * state needs to be cleared to allow user activity to be resumed. If the
   * {@code #enableWindowLockedState} method adds functionality to provide a cascade lock for all
   * opened child windows, then this method must add functionality to allow the lock state to be
   * cleared for each opened child window.
   */
  void disableWindowLockedState();

  /**
   * Invoke if you wish to temporarily lock the window as a result of a time consuming task rather
   * than as a result of talking to a remote network interface. This method needs to lock the
   * immediate window only.
   */
  void setWindowBusyCursor();

  /**
   * Invoke to unlock the window if the {@code #setWindowBusyCursor} method was called to lock it.
   */
  void setWindowDefaultCursor();

  /**
   * Determine if the window is busy for an network interface request or some other time consuming
   * activity.
   *
   * @return {@code true} if window is currently in a locked state, {@code false} otherwise.
   */
  boolean isWindowBusy();
}
