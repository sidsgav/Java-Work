package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowDisposeListener;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowForcedDisposeListener;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowLookAndFeelUpdateListener;

/**
 * This is an abstract class that extends {@code JFrame}. Any application specific frame window that
 * would normally directly use or subclass {@code JFrame} should instead subclass either this class
 * or {@code LockableFrame} which also extends this class.<br>
 * This class provides a number of extensions to the {@code JFrame} class which are:<br>
 * <ul>
 * <li>The ability to update the LookAndFeel (L&F) of the concrete frame implementation at
 * application runtime after instantiation. In addition, the L&F of all child windows currently
 * opened whose parent is the concrete frame implementation will also be updated.<br>
 * This class will update the L&F directly for any posted secondary {@code Dialog}s owned by this
 * parent {@code Frame}. Secondary {@code Frame} windows that may be launched by this {@code Frame}
 * are required to be updated by the concrete frame implementation through abstract method
 * {@code doAdditionalUILookAndFeelUpdate}. This is because {@code Frame} windows do not allow
 * direct parentage to be supplied to their constructor. In addition, this method can be used to
 * allow any pop-up menus to be updated. To update {@code Frame} windows or popup menus simply do
 * the following calls - {@code SwingUtilities.updateComponentTreeUI(Frame)},
 * {@code SwingUtilities.updateComponentTreeUI(popupMenu)} respectively.</li>
 * <li>This class implements the {@code WindowForcedDisposeListener} interface, which allows this
 * class to forcibly dispose all child windows currently opened whose parent (or owner) is the
 * concrete frame implementation.</li>
 * <li>This class overrides the {@code dispose} method to ensure that if there is a parent window
 * supplied and the parent implements the {@code WindowDisposeListener}, then it is notified first
 * before the concrete frame implementation is disposed of. Note if a reference to parent is
 * supplied it is not forwarded in call to {@code super()} for {@code JFrame} as this class is
 * parentless. This reference is used to exercise the {@code WindowDisposeListener} interface and
 * allow this window to be centered on it's parent when displayed.</li>
 * <li>This class attaches a window component listener to detect window activity in order for the
 * Client to reset any inactivity timeout that may be in operation.</li>
 * <li>This class overrides the {@code pack} method to determine the actual size of the window as
 * realized by the concrete frame implementation's layout managers, and determine whether there is a
 * need to reduce the window size to not exceed the display screen resolution.</li>
 * <li>This class adds a window focus listener to detect window activity in order for the Client to
 * reset any inactivity timeout that may be in operation.</li>
 * <li>This class overrides the {@code setVisible} method to check whether this {@code Frame} is to
 * be made visible with zero size (i.e. omission of {@code pack} method call) or the frame's
 * location has not been specified in relation to it's parent (if one supplied).</li>
 * </ul>
 * In regards to resetting any inactivity timeout this component will inform
 * {@code PresentationUtilities#clientActivityDetected} when this window is resized or gains focus.
 * Therefore, by registering a client activity listener with
 * {@code PresentationUtilities#addApplicationActivityListener} will result in the listener being
 * informed to allow it to reset any inactivity timeout that may be in operation.
 */
@SuppressWarnings("serial")
public abstract class MFrame extends JFrame implements WindowLookAndFeelUpdateListener,
    WindowForcedDisposeListener {

  // Log4j
  private static final Logger logger = LoggerFactory.getLogger(MFrame.class);

  // Instance member used to check if this Frame is to be made visible
  // with zero size.
  private static final Dimension ZERO_DIMENSION = new Dimension(0, 0);

  private Component parent;

  private WindowDisposeListener windowDisposeListener;

  private ComponentAdapter componentAdapter;

  private WindowAdapter windowAdapter;

  // Instance member used to check if this Frame is to be made visible
  // without having it's location.
  private boolean locationSet = false;

  private boolean isInPackOperation = false;

  /**
   * Fully configurable constructor with {@code Frame} as this frame's owner. All constructors with
   * {@code Frame} as frame owner or no parent defer to this one. The parameter {@code frameOwner}
   * is checked to determine if it implements {@code WindowDisposeListener} and if so it's serviced
   * according to its contract.
   *
   * @param frameOwner parent frame owner. It is also checked to determine if it implements
   *        {@code WindowDisposeListener} and if so the reference is stored.
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   * @param gc the {@code GraphicsConfiguration} that is used to construct the new {@code Frame}; if
   *        {@code gc} is {@code null}, the system default {@code GraphicsConfiguration} is assumed.
   */
  public MFrame(final Frame frameOwner, final String frameTitle, final GraphicsConfiguration gc) {
    super(frameTitle, gc);
    parent = frameOwner;
    if (frameOwner instanceof WindowDisposeListener) {
      windowDisposeListener = (WindowDisposeListener) frameOwner;
    }
    initialise();
  }

  /**
   * Constructor providing {@code Dialog} as this frame's owner. All constructors with
   * {@code Dialog} as frame owner defer to this one. The parameter {@code frameOwner} is checked to
   * determine if it implements {@code WindowDisposeListener} and if so it's serviced according to
   * its contract.
   *
   * @param frameOwner parent frame owner. It is also checked to determine if it implements
   *        {@code WindowDisposeListener} and if so the reference is stored.
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   * @param gc the {@code GraphicsConfiguration} that is used to construct the new {@code Frame}; if
   *        {@code gc} is {@code null}, the system default {@code GraphicsConfiguration} is assumed.
   */
  public MFrame(final Dialog frameOwner, final String frameTitle, final GraphicsConfiguration gc) {
    super(frameTitle, gc);
    parent = frameOwner;
    if (frameOwner instanceof WindowDisposeListener) {
      windowDisposeListener = (WindowDisposeListener) frameOwner;
    }
    initialise();
  }

  /**
   * Constructor providing {@code Window} as this frame's owner and specified title and specified
   * {@code GraphicsConfiguration} of a screen device. All constructors with {@code Window} as frame
   * owner defer to this one. The parameter {@code frameOwner} is checked to determine if it
   * implements {@code WindowDisposeListener} and if so it's serviced according to its contract.
   *
   * @param frameOwner parent frame owner. It is also checked to determine if it implements
   *        {@code WindowDisposeListener} and if so the reference is stored.
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   * @param gc the {@code GraphicsConfiguration} that is used to construct the new {@code Frame}; if
   *        {@code gc} is {@code null}, the system default {@code GraphicsConfiguration} is assumed.
   */
  public MFrame(final Window frameOwner, final String frameTitle, final GraphicsConfiguration gc) {
    super(frameTitle, gc);
    parent = frameOwner;
    if (frameOwner instanceof WindowDisposeListener) {
      windowDisposeListener = (WindowDisposeListener) frameOwner;
    }
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Frame} as this frame's owner but with frame title
   * defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param frameOwner parent frame owner.
   */
  public MFrame(final Frame frameOwner) {
    this(frameOwner, "", null);
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this frame's owner but with frame
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param frameOwner parent frame owner.
   */
  public MFrame(final Dialog frameOwner) {
    this(frameOwner, "", null);
  }

  /**
   * As fully configurable constructor with {@code Window} as this frame's owner but with frame
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param frameOwner parent frame owner.
   */
  public MFrame(final Window frameOwner) {
    this(frameOwner, "", null);
  }

  /**
   * This constructor will default parent to a {@code null} {@code Frame} and empty frame title.
   *
   * @param gc the {@code GraphicsConfiguration} that is used to construct the new {@code Frame}; if
   *        {@code gc} is {@code null}, the system default {@code GraphicsConfiguration} is assumed.
   */
  public MFrame(final GraphicsConfiguration gc) {
    this((Frame) null, "", gc);
  }

  /**
   * This constructor will default parent to a {@code null} {@code Frame} and
   * {@code GraphicsConfiguration} to {@code null} also.
   *
   * @param frameTitle frame title to display in the frame's border. A {@code null} value is treated
   *        as an empty string, "".
   */
  public MFrame(final String frameTitle) {
    this((Frame) null, frameTitle, null);
  }

  /**
   * This constructor will default parent to a {@code null} {@code Frame} and empty frame title and
   * {@code null} for {@code GraphicsConfiguration}.
   */
  public MFrame() {
    this((Frame) null, "", null);
  }

  private void initialise() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    // Add component listener to detect window activity in order for the
    // Client to reset any inactivity timeout that may be in operation
    // against the Client application.
    componentAdapter = new ComponentAdapter() {

      @Override
      public void componentResized(final ComponentEvent evt) {
        PresentationUtilities.clientActivityDetected(evt);
      }
    };
    addComponentListener(componentAdapter);

    // Add window focus listener to again detect window activity in order
    // for the Client to reset any inactivity timeout that may be in
    // operation.
    windowAdapter = new WindowAdapter() {

      @Override
      public void windowGainedFocus(final WindowEvent e) {
        PresentationUtilities.clientActivityDetected(e);
      }
    };
    addWindowFocusListener(windowAdapter);
  }

  /**
   * Invoked by this class and it's subclass {@code LockableFrame} to dictate required behaviour if
   * the {@code Frame} window is the main application frame or not.<br>
   * This method needs to be overridden by the concrete frame implementation.
   *
   * @return {@code true} if the concrete frame implementation is the main application
   *         {@code Frame}, {@code false} otherwise.
   */
  public abstract boolean isMainApplicationFrame();

  /**
   * This method is implementation for interface {@code WindowLookAndFeelUpdateListener} and is used
   * to update the Look and Feel (L&F) for this window and any secondary windows launched whose
   * parent is this window.<br>
   * This method needs to be invoked by the main application after the L&F for the application has
   * been changed at run time at user request or the application has detected a L&F change through
   * other means. It will trigger the chain of events that allow the concrete frame window's L&F to
   * be updated, followed by the opportunity to update any secondary frame windows (and their child
   * {@code Dialog} windows) and any popup menus belonging to the concrete frame window and finally
   * the L&F is updated for any child {@code Dialog} windows currently posted by the concrete frame
   * window.<br>
   * <br>
   * Unfortunately, the method {@code JComponent#updateUI} cannot be used here as the super class
   * hierarchy for this component does not include {@code JComponent}.
   */
  @Override
  public final void updateUILookAndFeel() {
    // Update the Look And Feel for this Frame window first.
    SwingUtilities.updateComponentTreeUI(this);

    // Allow secondary Frame windows or popup menus to be updated if required.
    doAdditionalUILookAndFeelUpdate();

    // Update the LookAndFeel for any child windows (Dialogs) currently
    // posted by this parent window.
    PresentationUtilities.updateUILookAndFeelForPostedChildWindows(this);
  }

  /**
   * This method needs to be overridden by the concrete {@code Frame} implementation. Allows
   * secondary {@code Frame} windows (launched by the concrete frame implementation) or popup menus
   * to be updated for Look and Feel changes if required. Example of calls to perform these
   * operations are:<br>
   * {@code SwingUtilities.updateComponentTreeUI(Frame)},
   * {@code SwingUtilities.updateComponentTreeUI(popupMenu)}.
   */
  public abstract void doAdditionalUILookAndFeelUpdate();

  /**
   * This method is implementation for interface {@code WindowForcedDisposeListener} and is used to
   * forcibly dispose any secondary windows launched by the concrete frame implementation before
   * disposing of itself.
   */
  @Override
  public final void doForcedDisposeAction() {
    PresentationUtilities.forceDisposeOfPostedChildWindows(this);
    // The following method allows secondary frame windows owned by this concrete
    // frame implementation to be disposed forcibly.
    doForcedDisposeActionOnSecondaryFrameWindows();
    // Finally dispose ourselves, only if we are not the main application Frame.
    if (!isMainApplicationFrame()) {
      dispose();
    }
  }

  /**
   * This method needs to be overridden by the concrete frame implementation. Allows secondary
   * {@code Frame} windows (which are launched by the concrete frame implementation) to be forcibly
   * disposed.
   */
  public abstract void doForcedDisposeActionOnSecondaryFrameWindows();

  /**
   * Overridden method to determine whether the {@code Frame} is to be made visible with zero size
   * (and thus requires it to be {@code pack}'ed) and also determines whether the location of the
   * {@code Frame} in relation to it's parent (if one supplied) needs to be set before making it
   * visible.
   *
   * @param b if {@code true}, makes the {@code Frame} visible, otherwise hides the {@code Frame}.
   */
  @Override
  public void setVisible(final boolean b) {
    final boolean doPack = (b && getSize().equals(ZERO_DIMENSION));
    if (doPack) {
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Calling pack() method on Frame before making it visible because of zero size.");
      }
      pack();
    }
    if (!locationSet) {
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Setting location of Frame before making it visible because it has not been set previously.");
      }
      PresentationUtilities.centerWindowAndClipIfRequired(parent, this);
      locationSet = true;
    }
    super.setVisible(b);
  }

  // The following four methods are overridden to allow instance member {@code locationSet}
  // to be assigned.

  /**
   * Overridden to allow this class to record the fact that the window bounds have been
   * specified.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void setBounds(final Rectangle r) {
    super.setBounds(r);
    if (!isInPackOperation) {
      locationSet = true;
    }
  }

  /**
   * Overridden to allow this class to record the fact that the window location has been
   * specified.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void setLocation(final Point p) {
    super.setLocation(p);
    if (!isInPackOperation) {
      locationSet = true;
    }
  }

  /**
   * Overridden to allow this class to record the fact that the window bounds have been
   * specified.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void setBounds(final int x, final int y, final int width, final int height) {
    super.setBounds(x, y, width, height);
    if (!isInPackOperation) {
      locationSet = true;
    }
  }

  /**
   * Overridden to allow this class to record the fact that the window location has been
   * specified.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void setLocation(final int x, final int y) {
    super.setLocation(x, y);
    if (!isInPackOperation) {
      locationSet = true;
    }
  }

  /**
   * Overridden method to allow this class to determine the actual size of the window as realized by
   * the concrete frame implementation's layout managers, and determine whether there is a need to
   * reduce the window size to not exceed the display screen resolution.
   *
   * @see Window#pack
   */
  @Override
  public void pack() {
    try {
      isInPackOperation = true;
      super.pack();
    } finally {
      isInPackOperation = false;
    }

    // Ensure that the Frame's size does not exceed the display screen resolution,
    // if so reduce size to fit.
    final int SCREEN_OFFSET = 30;
    boolean reduceWindowSize = false;
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int preferredWidth = getSize().width;
    int preferredHeight = getSize().height;
    if (preferredWidth > screenSize.width) {
      preferredWidth = screenSize.width - SCREEN_OFFSET;
      reduceWindowSize = true;
    }
    if (preferredHeight > screenSize.height) {
      preferredHeight = screenSize.height - SCREEN_OFFSET;
      reduceWindowSize = true;
    }
    if (reduceWindowSize) {
      setSize(preferredWidth, preferredHeight);
    }
  }

  /**
   * This method needs to be overridden by the concrete frame implementation. The subclass needs to
   * return it's unique window identifier as specified in either {@code WindowDisposeListener} or
   * else where.
   *
   * @return unique window identifier as specified by subclass.
   */
  protected abstract int getWindowIdentifier();

  /**
   * Overridden method to allow the parent component of this frame to be notified that this frame is
   * to be disposed through use of interface {@code WindowDisposeListener}.
   *
   * @see Window#dispose
   */
  @Override
  public void dispose() {
    if (windowDisposeListener != null) {
      windowDisposeListener.clearWindowReference(getWindowIdentifier());
    }

    // Remove all listeners.
    removeComponentListener(componentAdapter);
    componentAdapter = null;
    removeWindowFocusListener(windowAdapter);
    windowAdapter = null;

    super.dispose();
  }
}
