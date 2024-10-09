package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.MouseAdapter;

import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowLockControl;

/**
 * This class extends {@code MFrame} and implements the {@code WindowLockControl}. This class should
 * be used and subclassed if the concrete frame implementation is capable of sending a request over
 * a network interface, which requires the window to be locked until the response is returned. The
 * window is locked in order to prevent the user performing any other action on the window.<br>
 * To lock the frame window a reference to this class can be passed to the request message which can
 * then call method {@code enableWindowLockedState} at message construction. Conversely, the method
 * {@code enableWindowLockedState} could be called directly prior to making a synchronous request.
 * The method {@code disableWindowLockedState} is invoked once the reply is received, or any failure
 * condition is received or even after return of the blocking synchronous request call to clear the
 * window locked state.<br>
 * This class also provides methods to allow the frame to be locked temporarily as a result of a
 * long running task not related to sending requests over a network interface.<br>
 */
@SuppressWarnings("serial")
public abstract class LockableFrame extends MFrame implements WindowLockControl {

  // Declare MouseAdapter to absorb all mouse events.
  private MouseAdapter glassPaneMouseAdapter;

  private boolean windowLockedForRequest = false;

  /**
   * This instance member provides a means for this parent {@code Frame} to lock any posted
   * secondary windows whose parent is this {@code Frame}. By default this functionality is disabled
   * as secondary windows may be capable of making requests and thus are capable of being locked in
   * their own right. If this instance member is enabled through method {@link #lockChildWindows}
   * then have a capability to lock this {@code Frame} window and all it's secondary windows on
   * request or busy activity. If there is only one {@code Frame} window and all secondary windows
   * are dialogs then have a means to lock the entire application on request or busy activity.
   */
  private boolean lockPostedChildWindows = false;

  // Declare Object to provide synchronisation on Locking/Unlocking windows.
  private final Object WINDOW_CURSOR_MUTEX = new Object();

  /**
   * Fully configurable constructor with {@code Frame} as this frame's owner and specified title and
   * specified {@code GraphicsConfiguration} of a screen device. All constructors with {@code Frame}
   * as frame owner or no parent defer to this one.
   *
   * @param frameOwner parent frame owner.
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   * @param gc used to construct the new {@code Frame} with; if supplied as {@code null}, the system
   *        default {@code GraphicsConfiguration} is assumed.
   */
  public LockableFrame(final Frame frameOwner, final String frameTitle, final GraphicsConfiguration gc) {
    super(frameOwner, frameTitle, gc);
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Frame} as this frame's owner but with frame title
   * defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param frameOwner parent frame owner.
   */
  public LockableFrame(final Frame frameOwner) {
    this(frameOwner, "", null);
  }

  /**
   * This constructor will default parent to a {@code null} {@code Frame} and empty frame title.
   *
   * @param gc used to construct the new {@code Frame} with; if supplied as {@code null}, the system
   *        default {@code GraphicsConfiguration} is assumed.
   */
  public LockableFrame(final GraphicsConfiguration gc) {
    this((Frame) null, "", gc);
  }

  /**
   * This constructor will default parent to a {@code null} {@code Frame} and
   * {@code GraphicsConfiguration} to {@code null} also.
   *
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   */
  public LockableFrame(final String frameTitle) {
    this((Frame) null, frameTitle, null);
  }

  /**
   * This constructor will default parent to a {@code null} {@code Frame} and empty frame title and
   * {@code null} for {@code GraphicsConfiguration}.
   */
  public LockableFrame() {
    this((Frame) null, "", null);
  }

  /**
   * Constructor providing {@code Dialog} as this frame's owner and specified title and specified
   * {@code GraphicsConfiguration} of a screen device. All constructors with {@code Dialog} as frame
   * owner defer to this one.
   *
   * @param frameOwner parent frame owner.
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   * @param gc used to construct the new {@code Frame} with; if supplied as {@code null}, the system
   *        default {@code GraphicsConfiguration} is assumed.
   */
  public LockableFrame(final Dialog frameOwner, final String frameTitle, final GraphicsConfiguration gc) {
    super(frameOwner, frameTitle, gc);
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this frame's owner but with frame
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param frameOwner parent frame owner.
   */
  public LockableFrame(final Dialog frameOwner) {
    this(frameOwner, "", null);
  }

  /**
   * Constructor providing {@code Window} as this frame's owner and specified title and specified
   * {@code GraphicsConfiguration} of a screen device. All constructors with {@code Window} as frame
   * owner defer to this one.
   *
   * @param frameOwner parent frame owner.
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   * @param gc used to construct the new {@code Frame} with; if supplied as {@code null}, the system
   *        default {@code GraphicsConfiguration} is assumed.
   */
  public LockableFrame(final Window frameOwner, final String frameTitle, final GraphicsConfiguration gc) {
    super(frameOwner, frameTitle, gc);
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Window} as this frame's owner but with frame
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param frameOwner parent frame owner.
   */
  public LockableFrame(final Window frameOwner) {
    this(frameOwner, "", null);
  }

  private void initialise() {
    glassPaneMouseAdapter = new MouseAdapter() {
      // Intentionally empty.
    };
  }

  /**
   * Invoke if any launched secondary windows whose parent is this {@code Frame} are required to be
   * locked when this frame is locked.<br>
   * Ideally, this method should be invoked once this specialised {@code Frame} window has been
   * instantiated as the application policy should be known up front.<br>
   * By default the ability to lock posted secondary windows is disabled, therefore this method only
   * needs to be invoked if it needs to be enabled.
   *
   * @param value {@code true} to allow secondary windows whose parent is this frame to be locked
   *        when this frame window has been locked.
   */
  public final void lockChildWindows(final boolean value) {
    lockPostedChildWindows = value;
  }

  /**
   * This method implements one method of interface {@code WindowLockControl}. Needs to be invoked
   * by a request object to be sent over a network interface at request instantiation to allow the
   * window to be locked until the reply is received.
   */
  @Override
  public final void enableWindowLockedState() {
    setWindowBusyCursor();
    windowLockedForRequest = true;

    // Do not allow the window to be closed whilst processing an interface request. The
    // only exception to this rule is the main application window which should be allowed
    // to close at any time.
    if (!isMainApplicationFrame()) {
      disableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }

    // Need to lock any child windows whose parent is this Frame window.
    // The following method will only work if the secondary window (which is a Dialog) that
    // is posted has this parent owner supplied in the call to Dialog's super() method.
    // In addition, this call will only lock those secondary windows that implement the
    // WindowLockControl interface. In other words a window, which is a direct instantiation
    // of JDialog/Dialog will not be locked. Therefore the user is free to access the UI
    // components of such windows.
    if (lockPostedChildWindows) {
      PresentationUtilities.lockPostedChildWindows(this);
    }
  }

  /**
   * This method implements one method of interface {@code WindowLockControl}. Needs to be invoked
   * by the reply object or error condition received over network interface for outgoing request.
   * This method needs to be called after any returned data has updated the concrete dialog
   * implementation.
   */
  @Override
  public final void disableWindowLockedState() {
    windowLockedForRequest = false;
    setWindowDefaultCursor();

    // Re-allow the window close event.
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    // Need to unlock any child windows whose parent is the Frame window and which
    // were previously locked through the enableWindowLockedState() method call.
    if (lockPostedChildWindows) {
      PresentationUtilities.unlockPostedChildWindows(this);
    }
  }

  /**
   * This method implements one method of interface {@code WindowLockControl}. The actual locking of
   * the window is done in a separate method because the application may wish to temporarily lock
   * the window as a result of some time consuming task rather than as a result of sending a request
   * message. Therefore call this method if you wish to lock the window temporarily as a result of a
   * long running action that is not related to sending requests over a network interface.
   */
  @Override
  public final void setWindowBusyCursor() {
    synchronized (WINDOW_CURSOR_MUTEX) {
      // Only lock window if not already locked by a request message.
      if (!windowLockedForRequest) {
        final Component glassPane = getGlassPane();
        if (!glassPane.isVisible()) {
          glassPane.setVisible(true);
          glassPane.addMouseListener(glassPaneMouseAdapter);
          glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          glassPane.requestFocus();
          // The following allows key events to be trapped.
          setFocusable(false);
        }
      }
    }
  }

  /**
   * This method implements one method of interface {@code WindowLockControl}. Invoke to unlock the
   * window if the {@code setWindowBusyCursor} method was called to lock it.
   */
  @Override
  public final void setWindowDefaultCursor() {
    synchronized (WINDOW_CURSOR_MUTEX) {
      // Only clear locked state if no window lock was applied as a result
      // of a request response message.
      if (!windowLockedForRequest) {
        final Component glassPane = getGlassPane();
        if (glassPane.isVisible()) {
          setFocusable(true);
          glassPane.removeMouseListener(glassPaneMouseAdapter);
          glassPane.setCursor(Cursor.getDefaultCursor());
          glassPane.setVisible(false);
        }
      }
    }
  }

  /**
   * This method implements one method of interface {@code WindowLockControl}. Determine if the
   * window is busy for an existing request or some other time consuming activity.
   *
   * @return {@code true} if window is currently in a locked state, {@code false} otherwise.
   */
  @Override
  public final boolean isWindowBusy() {
    final Component glassPane = getGlassPane();
    return glassPane.isVisible();
  }
}
