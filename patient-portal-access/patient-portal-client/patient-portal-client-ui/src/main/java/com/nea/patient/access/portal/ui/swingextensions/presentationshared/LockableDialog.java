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
 * This class extends {@code MDialog} and implements the {@code WindowLockControl}. This class
 * should be used and subclassed if the concrete dialog implementation is capable of sending a
 * request over a network interface, which requires the window to be locked until the response is
 * returned. The window is locked in order to prevent the user performing any other action on the
 * window.<br>
 * To lock the dialog window a reference to this class can be passed to the request message which
 * can then call method {@code enableWindowLockedState} at message construction. Conversely, the
 * method {@code enableWindowLockedState} could be called directly prior to making a synchronous
 * request. The method {@code disableWindowLockedState} is invoked once the reply is received, or
 * any failure condition is received or even after return of the blocking synchronous request call
 * to clear the window locked state.<br>
 * This class also provides methods to allow the dialog to be locked temporarily as a result of a
 * long running task not related to sending requests over a network interface.<br>
 */
@SuppressWarnings("serial")
public abstract class LockableDialog extends MDialog implements WindowLockControl {

  // Declare MouseAdapter to absorb all mouse events.
  private MouseAdapter glassPaneMouseAdapter;

  private boolean windowLockedForRequest = false;

  // Declare Object to provide synchronisation on Locking/Unlocking windows.
  private final Object WINDOW_CURSOR_MUTEX = new Object();

  /**
   * Fully configurable constructor with {@code Dialog} as this dialog's owner. All constructors
   * with {@code Dialog} as dialog owner defer to this one.
   *
   * @param owner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Dialog} is
   *        used.
   */
  public LockableDialog(final Dialog owner, final String dialogTitle, final boolean modal,
      final GraphicsConfiguration gc) {
    super(owner, dialogTitle, modal, gc);
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public LockableDialog(final Dialog owner, final String dialogTitle, final boolean modal) {
    this(owner, dialogTitle, modal, null);
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but with modality
   * defaulted to {@code false} and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   */
  public LockableDialog(final Dialog owner, final String dialogTitle) {
    this(owner, dialogTitle, false, null);
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but with dialog
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public LockableDialog(final Dialog owner, final boolean modal) {
    this(owner, "", modal, null);
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but with dialog
   * title defaulted to empty string and modality defaulted to {@code false} and
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   */
  public LockableDialog(final Dialog owner) {
    this(owner, "", false, null);
  }

  /**
   * Fully configurable constructor with {@code Frame} as this dialog's owner. All constructors with
   * {@code Frame} as dialog owner defer to this one.
   *
   * @param owner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Frame} is
   *        used.
   */
  public LockableDialog(final Frame owner, final String dialogTitle, final boolean modal,
      final GraphicsConfiguration gc) {
    super(owner, dialogTitle, modal, gc);
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public LockableDialog(final Frame owner, final String dialogTitle, final boolean modal) {
    this(owner, dialogTitle, modal, null);
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but with modality
   * defaulted to {@code false} and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   */
  public LockableDialog(final Frame owner, final String dialogTitle) {
    this(owner, dialogTitle, false, null);
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but with dialog
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public LockableDialog(final Frame owner, final boolean modal) {
    this(owner, "", modal, null);
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but with dialog
   * title defaulted to empty string and modality defaulted to {@code false} and
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param owner parent dialog owner.
   */
  public LockableDialog(final Frame owner) {
    this(owner, "", false, null);
  }

  /**
   * Create a dialog with the specified title, owner {@code Window} and modality and
   * {@code GraphicsConfiguration}. All constructors with {@code Window} as dialog owner defer to
   * this one.<br>
   *
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param owner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   * @param modalityType specifies whether dialog blocks input to other windows when shown. Value
   *        {@code null} and unsupported modality types are equivalent to {@code MODELESS}.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Window} is
   *        used; if {@code owner} is also {@code null}, the system default
   *        {@code GraphicsConfiguration} is assumed.
   *
   * @since 1.6
   */
  public LockableDialog(final Window owner, final String title, final Dialog.ModalityType modalityType,
      final GraphicsConfiguration gc) {
    super(owner, title, modalityType, gc);
    initialise();
  }

  /**
   * Create a dialog with the specified title, owner {@code Window} and modality and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   *
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param owner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   * @param modalityType - specifies whether dialog blocks input to other windows when shown. Value
   *        {@code null} and unsupported modality types are equivalent to {@code MODELESS}.
   *
   * @since 1.6
   */
  public LockableDialog(final Window owner, final String title, final Dialog.ModalityType modalityType) {
    this(owner, title, modalityType, null);
  }

  /**
   * Create a modeless dialog with the specified title and owner {@code Window} and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   *
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param owner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title to display in dialog's title bar or {@code null} if the dialog has no title.
   *
   * @since 1.6
   */
  public LockableDialog(final Window owner, final String title) {
    this(owner, title, Dialog.ModalityType.MODELESS, null);
  }

  /**
   * Create a dialog with the specified owner {@code Window}, modality and an empty title and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   *
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param owner parent dialog owner or {@code null} if this dialog has no owner.
   * @param modalityType specifies whether dialog blocks input to other windows when shown. Value
   *        {@code null} and unsupported modality types are equivalent to {@code MODELESS}.
   *
   * @since 1.6
   */
  public LockableDialog(final Window owner, final ModalityType modalityType) {
    this(owner, "", modalityType, null);
  }

  /**
   * Create a modeless dialog with the specified owner {@code Window} and an empty title and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   *
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param owner parent dialog owner or {@code null} if this dialog has no owner.
   *
   * @since 1.6
   */
  public LockableDialog(final Window owner) {
    this(owner, "", Dialog.ModalityType.MODELESS, null);
  }

  /**
   * This constructor will default parent to a {@code null} {@code Frame} and dialog title defaulted
   * to empty string and modality defaulted to {@code false} and {@code GraphicsConfiguration} set
   * to {@code null}.
   */
  public LockableDialog() {
    this((Frame) null, "", false, null);
  }

  private void initialise() {
    glassPaneMouseAdapter = new MouseAdapter() {
      // Intentionally empty.
    };
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

    // Do not allow the window to be closed whilst processing an interface request.
    disableEvents(AWTEvent.WINDOW_EVENT_MASK);

    // Need to lock any child windows whose parent is this Dialog window.
    // The following method will only work if the secondary window (which is a Dialog)
    // that is posted has this parent owner supplied in the call to Dialog's super()
    // method. In addition, this call will only lock those secondary windows that implement
    // the WindowLockControl interface. In other words a window, which is a direct
    // instantiation of JDialog/Dialog will not be locked. Therefore the user is free to
    // access the UI components of such windows.
    PresentationUtilities.lockPostedChildWindows(this);
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

    // Need to unlock any child windows whose parent is this Dialog window and which
    // were previously locked through the enableWindowLockedState() method call.
    PresentationUtilities.unlockPostedChildWindows(this);
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
