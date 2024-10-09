package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
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

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowDisposeListener;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowForcedDisposeListener;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowLookAndFeelUpdateListener;

/**
 * This is an abstract class that extends {@code JDialog}. An application specific dialog window
 * that would normally directly use or subclass {@code JDialog} should instead subclass either this
 * class or {@code LockableDialog} or {@code ConfirmationDialog} (both of which extend this
 * class).<br>
 * This class provides a number of extensions to the {@code JDialog} class which are:<br>
 * <ul>
 * <li>The ability to update the Look and Feel (L&F) of the concrete dialog implementation at
 * application runtime after instantiation. In addition, the L&F of all child windows currently open
 * whose parent is the concrete dialog implementation will also be updated.<br>
 * This class will update the L&F directly for any posted secondary {@code Dialog}s owned by this
 * parent {@code Dialog}. Secondary {@code Frame} windows that may be launched by this
 * {@code Dialog} are required to be updated by the concrete dialog implementation of abstract
 * method {@code doAdditionalUILookAndFeelUpdate}. This is because {@code Frame} windows do not
 * allow direct parentage to be supplied to their constructor. In addition, this method can be used
 * to allow any pop-up menus to be updated. To update {@code Frame} windows or popup menus, simply
 * do the following calls - {@code SwingUtilities.updateComponentTreeUI(Frame)},
 * {@code SwingUtilities.updateComponentTreeUI(popupMenu)} respectively.</li>
 * <li>This class implements the {@code WindowForcedDisposeListener} interface, which allows this
 * class to forcibly close all child windows currently opened whose parent (or owner) is the
 * concrete dialog implementation.</li>
 * <li>This class overrides the {@code dispose} method to ensure that if the concrete dialog
 * implementation is non-modal, and the owning parent component of this dialog implements interface
 * {@code WindowDisposeListener}, then it is notified first before it is disposed of.</li>
 * <li>This class attaches a window component listener so that the concrete dialog implementation
 * cannot be resized to be smaller than its preferred drawn size because {@code Dialog}s are meant
 * to be transient windows, which should be closed once the required activity has been performed.
 * This avoids {@code Dialog}s being launched and reduced in size thus giving them the opportunity
 * to be left on screen. The component listener also serves the purpose of detecting window activity
 * in order for the Client to reset any inactivity timeout that may be in operation.</li>
 * <li>This class overrides the {@code pack} method to determine the actual size of the window as
 * realized by the concrete dialog implementation's layout managers, and determine whether the
 * window size needs to be reduced to not exceed the display screen resolution. If this is the case
 * a {@code JScrollPane} is introduced to allow visibility for the ContentPane's centre view
 * component.</li>
 * <li>This class adds a window focus listener to detect window activity in order for the Client to
 * reset any inactivity timeout that may be in operation.</li>
 * <li>This class overrides the {@code setVisible} method to check whether this {@code Dialog} is to
 * be made visible with zero size (i.e. omission of {@code pack} method call) or the dialog's
 * location has not been specified in relation to it's parent.</li>
 * </ul>
 * In regards to resetting any inactivity timeout this component will inform
 * {@code PresentationUtilities#clientActivityDetected} when this window is resized or gains focus.
 * Therefore, by registering a client activity listener with
 * {@code PresentationUtilities#addApplicationActivityListener} will result in the listener being
 * informed to allow it to reset any inactivity timeout that may be in operation.
 * 
 */
@SuppressWarnings("serial")
public abstract class MDialog extends JDialog implements WindowLookAndFeelUpdateListener,
    WindowForcedDisposeListener {

  // Log4j
  private static final Logger logger = LoggerFactory.getLogger(MDialog.class);

  // Instance member used to check if this dialog is to be made visible
  // with zero size.
  private static final Dimension ZERO_DIMENSION = new Dimension(0, 0);

  /**
   * Convenient Window identifier that can be used for those {@code Dialog}s that are modal and thus
   * can be returned in method call {@code getWindowIdentifier}.
   */
  protected final int MODAL_DIALOG_TYPE = -1;

  private Dimension actualDisplayedDimension;

  private WindowDisposeListener windowDisposeListener;

  private ComponentAdapter componentAdapter;

  private WindowAdapter windowAdapter;

  // This component may need to reduce the size of the drawn window,
  // if it's detected that the window is too large to fit within the
  // confines of the display screen resolution. If the window is
  // reduced in size then this ScrollPane is introduced to which the
  // ContentPane's centre view component is added, and then the
  // ScrollPane is added to the ContentPane's centre view component.
  private JScrollPane centrePieceScrollPane;

  // Instance member used to check if this dialog is to be made visible
  // without having it's location set in relation to it's parent.
  private boolean locationSet = false;

  private boolean isInPackOperation = false;

  /**
   * Fully configurable constructor with {@code Dialog} as this dialog's owner.<br>
   * The parameter {@code dialogOwner} is checked to determine if it implements
   * {@code WindowDisposeListener} and if so it's serviced according to its contract.<br>
   * All constructors with {@code Dialog} as dialog owner defer to this one.
   *
   * @param dialogOwner parent dialog owner. It is also checked to determine if it implements
   *        {@code WindowDisposeListener} and if so the reference is stored.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Dialog} is
   *        used.
   */
  public MDialog(final Dialog dialogOwner, final String dialogTitle, final boolean modal,
      final GraphicsConfiguration gc) {
    super(dialogOwner, dialogTitle, modal, gc);
    if (dialogOwner instanceof WindowDisposeListener) {
      windowDisposeListener = (WindowDisposeListener) dialogOwner;
    }
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public MDialog(final Dialog dialogOwner, final String dialogTitle, final boolean modal) {
    this(dialogOwner, dialogTitle, modal, null);
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but with modality
   * defaulted to {@code false} and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   */
  public MDialog(final Dialog dialogOwner, final String dialogTitle) {
    this(dialogOwner, dialogTitle, false, null);
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but with dialog
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public MDialog(final Dialog dialogOwner, final boolean modal) {
    this(dialogOwner, "", modal, null);
  }

  /**
   * As fully configurable constructor with {@code Dialog} as this dialog's owner but with dialog
   * title defaulted to empty string and modality defaulted to {@code false} and
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   */
  public MDialog(final Dialog dialogOwner) {
    this(dialogOwner, "", false, null);
  }

  /**
   * Fully configurable constructor with {@code Frame} as this dialog's owner.<br>
   * The parameter {@code dialogOwner} is checked to determine if it implements
   * {@code WindowDisposeListener} and if so it's serviced according to its contract.<br>
   * If {@code dialogOwner} is supplied as {@code null} then super class {@code JDialog} will set a
   * shared hidden frame as the owner of this dialog.<br>
   * All constructors with {@code Frame} as dialog owner defer to this one.
   *
   * @param dialogOwner parent dialog owner. It is also checked to determine if it implements
   *        {@code WindowDisposeListener} and if so the reference is stored.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Frame} is
   *        used.
   */
  public MDialog(final Frame dialogOwner, final String dialogTitle, final boolean modal,
      final GraphicsConfiguration gc) {
    super(dialogOwner, dialogTitle, modal, gc);
    if (dialogOwner instanceof WindowDisposeListener) {
      windowDisposeListener = (WindowDisposeListener) dialogOwner;
    }
    initialise();
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public MDialog(final Frame dialogOwner, final String dialogTitle, final boolean modal) {
    this(dialogOwner, dialogTitle, modal, null);
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but with modality
   * defaulted to {@code false} and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   * @param dialogTitle title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   */
  public MDialog(final Frame dialogOwner, final String dialogTitle) {
    this(dialogOwner, dialogTitle, false, null);
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but with dialog
   * title defaulted to empty string and {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   * @param modal set {@code true} for dialog modality, {@code false} otherwise.
   */
  public MDialog(final Frame dialogOwner, final boolean modal) {
    this(dialogOwner, "", modal, null);
  }

  /**
   * As fully configurable constructor with {@code Frame} as this dialog's owner but with dialog
   * title defaulted to empty string and modality defaulted to {@code false} and
   * {@code GraphicsConfiguration} set to {@code null}.
   *
   * @param dialogOwner parent dialog owner.
   */
  public MDialog(final Frame dialogOwner) {
    this(dialogOwner, "", false, null);
  }

  /**
   * Create a dialog with the specified title, owner {@code Window} and modality and
   * {@code GraphicsConfiguration}. All constructors with {@code Window} as dialog owner defer to
   * this one.<br>
   * The parameter {@code owner} is checked to determine if it implements
   * {@code WindowDisposeListener} and if so it's serviced according to its contract.<br>
   *
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner. It is also
   *        checked to determine if it implements {@code WindowDisposeListener} and if so the
   *        reference is stored.
   * @param title dialog title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modalityType specifies whether dialog blocks input to other windows when shown. Value
   *        {@code null} and unsupported modality types are equivalent to {@code MODELESS}.
   * @param gc the {@code GraphicsConfiguration} of the target screen device. If supplied as
   *        {@code null}, the same {@code GraphicsConfiguration} as the owning {@code Window} is
   *        used; if {@code owner} is also {@code null}, the system default
   *        {@code GraphicsConfiguration} is assumed.
   *
   * @since 1.6
   */
  public MDialog(final Window dialogOwner, final String title, final Dialog.ModalityType modalityType,
      final GraphicsConfiguration gc) {
    super(dialogOwner, title, modalityType, gc);
    if (dialogOwner instanceof WindowDisposeListener) {
      windowDisposeListener = (WindowDisposeListener) dialogOwner;
    }
    initialise();
  }

  /**
   * Create a dialog with the specified title, owner {@code Window} and modality and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title dialog title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   * @param modalityType specifies whether dialog blocks input to other windows when shown. Value
   *        {@code null} and unsupported modality types are equivalent to {@code MODELESS}.
   *
   * @since 1.6
   */
  public MDialog(final Window dialogOwner, final String title, final Dialog.ModalityType modalityType) {
    this(dialogOwner, title, modalityType, null);
  }

  /**
   * Create a modeless dialog with the specified title and owner {@code Window} and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param title dialog title to display in dialog's title bar or {@code null} if the dialog has no
   *        title.
   *
   * @since 1.6
   */
  public MDialog(final Window dialogOwner, final String title) {
    this(dialogOwner, title, Dialog.ModalityType.MODELESS, null);
  }

  /**
   * Create a dialog with the specified owner {@code Window}, modality and an empty title and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   * @param modalityType specifies whether dialog blocks input to other windows when shown. Value
   *        {@code null} and unsupported modality types are equivalent to {@code MODELESS}.
   *
   * @since 1.6
   */
  public MDialog(final Window dialogOwner, final ModalityType modalityType) {
    this(dialogOwner, "", modalityType, null);
  }

  /**
   * Create a modeless dialog with the specified owner {@code Window} and an empty title and
   * {@code GraphicsConfiguration} set to {@code null}.<br>
   * This constructor sets the component's locale property to the value returned by
   * {@code JComponent.getDefaultLocale}.
   *
   * @param dialogOwner parent dialog owner or {@code null} if this dialog has no owner.
   *
   * @since 1.6
   */
  public MDialog(final Window dialogOwner) {
    this(dialogOwner, "", Dialog.ModalityType.MODELESS, null);
  }

  private void initialise() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    // Add component listener to determine if we need to cap the window size, if it's
    // resized to be smaller than it's actual drawn size.
    // In addition the component listener allows us to detect window activity in order
    // for any inactivity timeout that may be in operation against the Client application
    // to be reset.
    componentAdapter = new ComponentAdapter() {

      @Override
      public void componentResized(final ComponentEvent evt) {
        formComponentResized(evt);
        PresentationUtilities.clientActivityDetected(evt);
      }
    };
    addComponentListener(componentAdapter);

    // Add window focus listener to again detect window activity in order for the Client
    // application to reset any inactivity timeout that may be in operation.
    windowAdapter = new WindowAdapter() {

      @Override
      public void windowGainedFocus(final WindowEvent e) {
        PresentationUtilities.clientActivityDetected(e);
      }
    };
    addWindowFocusListener(windowAdapter);
  }

  /**
   * Set the component to notify when this dialog is disposed. The new {@code WindowDisposeListener}
   * replaces any stored reference to {@code WindowDisposeListener} that may have been set either
   * through this method or via one of the constructors of this class through dialog owner
   * parameter.<br>
   * The other reason for this method's inclusion is that it may not always be the case that the
   * parent window of this dialog is interested in the disposed state of this dialog but instead
   * another component that resides in the parent window that needs to be informed. For example the
   * components {@code TableView} and {@code TreeView} are capable of launching child dialogs (i.e.
   * Find/Filter) and need to be notified when the child dialogs are disposed of. However, the
   * aforementioned components are not descendants of class {@code Window} and thus they cannot
   * register themselves through the constructors of this class, hence the need for this method.
   *
   * @param ownerToNotify the owning component of this child window to notify when it's disposed.
   */
  protected final void setWindowDisposeListener(final WindowDisposeListener ownerToNotify) {
    windowDisposeListener = ownerToNotify;
  }

  /**
   * This method is implementation for interface {@code WindowLookAndFeelUpdateListener} and is used
   * to update the Look and Feel (L&F) for this window and any secondary windows launched whose
   * parent is this window.<br>
   * Instead of calling this method directly for each displayed {@code Dialog} window it is better
   * for the primary frame window to detect that L&F has been changed at user request or other means
   * and cascade the update for each {@code Dialog} through {@code MFrame#updateUILookAndFeel}.<br>
   * <br>
   * Unfortunately, the method {@code JComponent#updateUI} cannot be used here as the super class
   * hierarchy for this component does not include {@code JComponent}.
   * 
   */
  @Override
  public final void updateUILookAndFeel() {
    // Update the Look And Feel for this Dialog window first.
    SwingUtilities.updateComponentTreeUI(this);

    // Allow secondary Frame windows or popup menus to be updated if required.
    doAdditionalUILookAndFeelUpdate();

    // Update the LookAndFeel for any child windows (Dialogs) currently
    // posted by this parent window.
    PresentationUtilities.updateUILookAndFeelForPostedChildWindows(this);
  }

  /**
   * This method needs to be overridden by the concrete {@code Dialog} implementation. Allows
   * secondary {@code Frame} windows (launched by the concrete dialog implementation) or popup menus
   * to be updated for Look and Feel changes if required. Example of calls to perform these
   * operations are:<br>
   * {@code SwingUtilities.updateComponentTreeUI(Frame)},
   * {@code SwingUtilities.updateComponentTreeUI(popupMenu)}.
   */
  public abstract void doAdditionalUILookAndFeelUpdate();

  /**
   * This method is implementation for interface {@code WindowForcedDisposeListener} and is used to
   * forcibly dispose any secondary windows launched by the concrete dialog implementation before
   * disposing of itself.
   */
  @Override
  public final void doForcedDisposeAction() {
    PresentationUtilities.forceDisposeOfPostedChildWindows(this);
    // The following method allows secondary frame windows owned by this concrete dialog
    // implementation to be disposed forcibly.
    doForcedDisposeActionOnSecondaryFrameWindows();
    // Finally dispose ourselves.
    dispose();
  }

  /**
   * This method needs to be overridden by the concrete dialog implementation. Allows secondary
   * {@code Frame} windows (which are launched by the concrete dialog implementation) to be forcibly
   * disposed.
   */
  public abstract void doForcedDisposeActionOnSecondaryFrameWindows();

  /**
   * This method needs to be overridden by the concrete {@code Dialog} implementation.<br>
   * It will be invoked by this class if it's determined that the window contents are unable to be
   * displayed within the confines of the display screen resolution (be it width or height) and thus
   * needs to be capped in size to fit. If it's capped in size then this class will need to
   * introduce a {@code JScrollPane} to allow the dialog's ContentPane centre component to be
   * visible. However, there is no means of extracting the centre component from the ContentPane in
   * order to remove it and then add it to the {@code JScrollPane} thus the need for this method to
   * return it.
   *
   * @return this dialog's centre display component.
   */
  protected abstract Component getContentPaneCentreViewComponent();

  /**
   * Overridden method to determine whether the {@code Dialog} is to be made visible with zero size
   * (and thus requires it to be {@code pack}'ed) and also determines whether the location of the
   * {@code Dialog} in relation to its parent needs to be set before making it visible.
   *
   * @param b if {@code true}, makes the {@code Dialog} visible, otherwise hides the {@code Dialog}.
   */
  @Override
  public void setVisible(final boolean b) {
    final boolean doPack = (b && getSize().equals(ZERO_DIMENSION));
    if (doPack) {
      if (logger.isDebugEnabled()) {
        logger.debug("Calling pack() method on Dialog before making it visible because of zero size.");
      }
      pack();
    }
    if (!locationSet) {
      if (logger.isDebugEnabled()) {
        logger.debug("Setting location of Dialog before making it visible because it has not been set previously.");
      }
      PresentationUtilities.centerWindowAndClipIfRequired(getParent(), this);
      locationSet = true;
    }
    super.setVisible(b);
  }

  // The following four methods are overridden to allow {@code locationSet} instance member
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
   * Overridden {@code pack} method to allow this class to determine the actual size of the window
   * as realized by the concrete dialog implementation's layout managers, and determine whether
   * there is a need to reduce the window size to not exceed the display screen resolution, which
   * will require the introduction of a {@code JScrollPane} to allow visibility for the
   * ContentPane's centre view component.
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

    // Ensure that the Dialog's size does not exceed the display screen resolution,
    // if so reduce size to fit.
    boolean reduceWindowSize = false;
    boolean widthToBig = false;
    boolean heightToBig = false;

    final int SCREEN_OFFSET = 30;
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int preferredWidth = getSize().width;
    int preferredHeight = getSize().height;
    if (preferredWidth > screenSize.width) {
      preferredWidth = screenSize.width - SCREEN_OFFSET;
      reduceWindowSize = true;
      widthToBig = true;
    }
    if (preferredHeight > screenSize.height) {
      preferredHeight = screenSize.height - SCREEN_OFFSET;
      reduceWindowSize = true;
      heightToBig = true;
    }
    actualDisplayedDimension = new Dimension(preferredWidth, preferredHeight);

    if (reduceWindowSize) {
      // Need to introduce a JScrollPane to allow the centre view components
      // to be visible because the screen width or height or both will be capped.
      // Ensure that it is not already instantiated and added.
      if (centrePieceScrollPane == null) {
        // Retrieve the ContentPane's centre view component through abstract method call.
        final Component centrePiece = getContentPaneCentreViewComponent();

        if (centrePiece != null) {
          // Remove the centre view component from the ContentPane, in order for us to
          // replace
          // it with the JScrollPane.
          getContentPane().remove(centrePiece);

          // Determine which Scroll Bar should be displayed.
          final int horizontalPolicy = (widthToBig ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
              : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
          final int verticalPolicy = (heightToBig ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
              : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

          // Create the JScrollPane with required Scroll Bar policies and add the
          // centre view component to it.
          centrePieceScrollPane = new JScrollPane(centrePiece, verticalPolicy, horizontalPolicy) {

            // The following two methods are overridden because do not want
            // the JScrollPane's borders to be drawn.
            @Override
            public Border getViewportBorder() {
              return null;
            }

            @Override
            public Border getBorder() {
              return null;
            }
          };

          // Now add the JScrollPane as a centre view component of the ContentPane.
          getContentPane().add(centrePieceScrollPane, BorderLayout.CENTER);

          // Reduce the window size as it's to big to fit.
          setSize(actualDisplayedDimension);

          // Re-validate the ContentPane's components, which will re-draw the window.
          validate();
        }
      } else {
        // Re-evaluate the Scroll Bar policies.
        final int horizontalPolicy = (widthToBig ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
            : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        final int verticalPolicy = (heightToBig ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        centrePieceScrollPane.setHorizontalScrollBarPolicy(horizontalPolicy);
        centrePieceScrollPane.setVerticalScrollBarPolicy(verticalPolicy);

        setSize(actualDisplayedDimension);
      }
    }
  }

  // The following method allows the window to be resized to it's preferred actual drawn
  // size, if the window has been sized to be smaller. This ensures that the window is not
  // made smaller in size than the preferred drawn size because Dialog's are meant to be
  // transient windows where once the required activity has been performed they are closed.
  // This avoids Dialogs being launched and reduced in size thus giving them the opportunity
  // to be left on screen.
  private void formComponentResized(@SuppressWarnings("unused") final ComponentEvent evt) {
    if (actualDisplayedDimension != null) {
      final Dimension newSize = getSize();
      if (newSize.height < actualDisplayedDimension.height) {
        newSize.height = actualDisplayedDimension.height;
      }
      if (newSize.width < actualDisplayedDimension.width) {
        newSize.width = actualDisplayedDimension.width;
      }
      setSize(newSize);
    }
  }

  /**
   * This method needs to be overridden by the concrete dialog implementation. The subclass needs to
   * return its unique window identifier as specified in either {@code WindowDisposeListener} or
   * else where. If the specialised dialog is displayed in modal form then it can simply return the
   * value {@code MODAL_DIALOG_TYPE} for completeness even though this method will not be called.
   *
   * @return unique window identifier as specified by subclass.
   */
  protected abstract int getWindowIdentifier();

  /**
   * Overridden {@code dispose} method to allow the parent component of this dialog to be notified
   * that this dialog is to be disposed through use of interface {@code WindowDisposeListener}
   *
   * @see Window#dispose
   */
  @Override
  public void dispose() {
    if (!isModal()) {
      if (windowDisposeListener != null) {
        windowDisposeListener.clearWindowReference(getWindowIdentifier());
      }
    }
    // Remove all listeners.
    removeComponentListener(componentAdapter);
    componentAdapter = null;
    removeWindowFocusListener(windowAdapter);
    windowAdapter = null;

    // Call dispose() method on base class Dialog which calls it on extended class Window.
    super.dispose();

    // Finally, get ourselves removed from this Dialog's parent by calling finalize()
    // method on super class Window.
    // Note - It should not be necessary to call the following finalize() method as
    // the Garbage Collector will call it. Instead, the method isDisplayable()
    // (on class Component) should be used to determine if a window has been targeted
    // for Garbage collection and thus can be called when examining window instances
    // from calls to getOwnedWindows() etc. However, on the Unix environment we are
    // experiencing undesirable behaviour from the call to isDisplayable(). It seems
    // to be a timing issue. Therefore, the call to finalize() is put back in, until
    // a better solution is implemented.
    try {
      super.finalize();
    } catch (Throwable t) {
      logger.error("Caught Throwable when making method call finalize() on extended class Window");
    }
  }
}
