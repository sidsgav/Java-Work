package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowForcedDisposeListener;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowLockControl;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowLookAndFeelUpdateListener;

/**
 * This class contains static methods and members which can be accessed from any code of the client
 * application. Essentially, any code that is required to be called from many different places or
 * becomes generic enough to be supported by many classes, then it should be added here to avoid
 * code duplication. Static class members of interest are Text and Font settings for read and
 * read/write on screen components. Also default values for tokens that can be read from
 * configuration files are held here and accessor methods are defined to overload these if the
 * configuration file was successfully loaded and the values stored as System properties. Static
 * methods of interest are as follows:<br>
 * <ul>
 * <li>{@code getWindowBarIconImage} - This method should be called by all {@code Frame}s to ensure
 * that all windows display the correct application image, otherwise the image displayed will be the
 * default Java Coffee image. To set the image on the {@code Frame} call the following method
 * {@code setIconImage(PresentationUtilities.getWindowBarIconImage())}. {@code Dialog}s do not need
 * to make such a call, because if their parent is a {@code Frame} and the parent is supplied to the
 * {@code super} method during {@code Dialog} instantiation, then the dialog will take the image
 * from it's parent automatically.</li>
 * <li>{@code centerWindowAndClipIfRequired} - This method should be called for every window that is
 * instantiated before calling {@code setVisible(true)}, to allow the window to be centered on its
 * parent window, the window is also clipped if necessary to ensure that it is displayed within the
 * confines on the display screen, this generally occurs if the parent window is positioned
 * partially off screen.</li>
 * <li>{@code isHostAddress} - This method checks the syntax of a text entry that represents an IP
 * Address.</li>
 * <li>{@code isPrintable} - This method checks if printable characters have been entered in text
 * entry components.</li>
 * <li>{@code displayMessageDialogOfRequiredType} - This method should be called if an
 * error/warning/information etc message dialog needs to be posted. This single method will
 * instantiate the required dialog and center it on it's parent before displaying it.</li>
 * <li>{@code displayModalConfirmationDialog} - As above but in this case a confirmation modal
 * dialog is posted.</li>
 * <li>{@code displayInputDialog} - Display a modal input dialog box with OK and CANCEL buttons but
 * where initial, possible selections and all other options can be specified.</li>
 * <li>{@code convertStringToHTMLFormatWithLineBreaks} - Allows the supplied string to be broken
 * into a number of lines. The string is returned within a HTML wrapper. This method can be called
 * if ToolTip support is required where the need to display the contents of a long string within the
 * confines of a small area is required. Additionally this method could be called for displaying
 * multiple lines on a {@code JButton} as it's label or even the string for the {@code JLabel}
 * itself.</li>
 * <li>{@code sortSuppliedContainer} - This method takes a container of {@code Object}s and sorts it
 * in alphabetical order ignoring case.</li>
 * <li>{@code addEntryToSortedComboBox} - This method allows an {@code Object} entry to be added to
 * an already sorted ComboBox container.
 * <li>{@code isApplicationToAdhereToTMNStyleGuide} - This method checks an application property to
 * determine whether the application should conform to the TMN Style Guide. This method needs to be
 * called prior to calling {@code setupTMNColorsAndFont} method in this class.</li>
 * <li>{@code setupTMNColorsAndFont} - This method sets up colours and font resources for all Swing
 * components to ensure the application meets the TMN Style Guide. This method should be called at
 * application startup once all configuration files have been read in and before any UI window is
 * displayed.</li>
 * <li>{@code isHelpOptionRightAligned} - Determines whether Help option should be right aligned
 * when used on a menu bar or as a push button.</li>
 * <li>{@code addApplicationActivityListener} - allow an interface to be registered that is
 * interested in listening for application activity such as window activity and component moved and
 * resized events. It provides the interface implementation the capability to reset any inactivity
 * timeout that may be in operation for the client application.</li>
 * <li>Accessor methods to return the maximum number of characters allowed for generic text entry
 * string fields and the customer editable notes string fields. Other default members are available
 * for IP_ADDRESS length, FILE Name length and default maximum columns to display for text entry
 * fields.</li>
 * </ul>
 */
public final class PresentationUtilities {

  // Define final static members which reflect the TMN Style Guide for Text
  // and Font settings to be used for on-screen components.

  /** TMN Style Guide specified value for default background colour */
  public static final Color TMN_DEFAULT_BACKGROUND_COLOUR = new Color(207, 207, 207); // Grey81

  /** TMN Style Guide specified value for default foreground colour */
  public static final Color TMN_DEFAULT_FOREGROUND_COLOUR = Color.black;

  /** TMN Style Guide specified value for read/write components background colour */
  public static final Color TMN_READ_WRITE_FIELDS_BG_COLOUR = new Color(250, 250, 250); // Off-white

  /** TMN Style Guide specified value for read only components background colour */
  public static final Color TMN_READ_ONLY_FIELDS_BG_COLOUR = TMN_DEFAULT_BACKGROUND_COLOUR;

  /** TMN Style Guide specified value for application font */
  public static final Font TMN_DEFAULT_APPLICATION_FONT = new Font("Arial", Font.PLAIN, 12);

  /** TMN Style Guide specified value for application background colour */
  public static final Color TMN_DEFAULT_APPLICATION_BG_COLOUR = TMN_DEFAULT_BACKGROUND_COLOUR;

  /** TMN Style Guide specified value for application foreground colour */
  public static final Color TMN_DEFAULT_APPLICATION_FG_COLOUR = TMN_DEFAULT_FOREGROUND_COLOUR;

  // Define constants to be used for the layout manager GridBagConstraints Inset fields.

  /** Constant used for GridBagConstraints Inset top field */
  public static final int GBC_INSET_TOP_SPACE = 3;

  /** Constant used for GridBagConstraints Inset left field */
  public static final int GBC_INSET_LEFT_SPACE_WEST = 3;

  /** Constant used for GridBagConstraints Inset left field */
  public static final int GBC_INSET_LEFT_SPACE_EAST = 5;

  /** Constant used for GridBagConstraints Inset right field */
  public static final int GBC_INSET_RIGHT_SPACE = 3;

  /** Constant used for GridBagConstraints Inset bottom field */
  public static final int GBC_INSET_BOTTOM_SPACE = 3;

  /** Constant to store maximum field length for File Names */
  public static final int FILE_NAME_MAXIMUM_LENGTH = 255;

  /** Constant to store maximum display length for File Names */
  public static final int FILE_NAME_MAXIMUM_DISPLAY_LENGTH = 40;

  /** Constant to store maximum field length for IP Address */
  public static final int IP_ADDRESS_LENGTH = 20;

  /** Constant to store maximum row entries to display for a posted {@code JComboBox} list */
  public static final int MAX_DISPLAY_ROW_LIST_FOR_COMBOBOX_LISTS = 8;

  /** Constant to store the number of pixels used to separate grouped buttons. */
  public static final int BUTTON_SPACING = 5;

  /** Constant to store the number of pixels used as edge spacing for buttons */
  public static final int BUTTON_EDGE_SPACING = 4;

  /**
   * Constant to store maximum number of rows to display for a generic Status Panel i.e.
   * {@code JTextArea} component.
   */
  public static final int STATUSPANEL_ROWS = 4;

  /**
   * Constant to store maximum number of columns to display for a generic Status Panel i.e.
   * {@code JTextArea} component.
   */
  public static final int STATUSPANEL_COLUMNS = 40;

  /**
   * Constant to store maximum number of rows to display for a Customer Notes input component i.e.
   * {@code JTextArea} component.
   */
  public static final int CUSTOMERNOTES_ROWS = 3;

  /**
   * Constant to store maximum number of columns to display for a Customer Notes input component
   * i.e. {@code JTextArea} component.
   */
  public static final int CUSTOMERNOTES_COLUMNS = 40;

  /** Class member to allow spacing between button components. */
  public static final Dimension BUTTONFILLER_SIZE = new Dimension(20, 1);

  /** Class member to allow spacing between small button components. */
  public static final Dimension SMALLBUTTONFILLER_SIZE = new Dimension(5, 1);

  /** Class member to store Windows Look and Feel Identifier. */
  public static final String WINDOWS_LOOK_AND_FEEL_ID = "Windows";

  /** Class member to store Motif Look and Feel Identifier. */
  public static final String MOTIF_LOOK_AND_FEEL_ID = "Motif";

  /** Class member to store Metal Look and Feel Identifier. */
  public static final String METAL_LOOK_AND_FEEL_ID = "Metal";

  /** Class member to store Windows OS Name Identifier. */
  public static final String WINDOWS_OS_NAME_ID = "Windows";

  // Log4j
  private static final Logger logger = LoggerFactory.getLogger(PresentationUtilities.class);

  private static final List<ApplicationActivityListener> applicationActivityListeners =
      new ArrayList<ApplicationActivityListener>();

  private static ImageIcon windowBarIcon = new ImageIcon();

  private static boolean windowsLookAndFeelSupported = false;

  private static String timeZoneStr = new String("Europe/London");

  private static String applicationVersion = "1.00";

  private static String applicationName = "";

  private static String productBrandingName = "";

  // Add members and accessor methods for the JavaHelp components.
  // The following two members should be overridden once the Client
  // application has loaded, the values should be retrieved from an application configuration file
  // and stored as System properties.
  private static String javahelpHelpSetName = "";

  private static String defaultJavaHelpPresentationWindowName = "";

  // Field lengths.
  // These are minimum default values and should be overridden from the application configuration
  // file or if retrieved from the Server. Irrespective of where they are retrieved from each value
  // should be set as a System Property value.
  private static int genericStringAttributeLength = 80;

  private static int customerEditableNotesLength = 200;

  private static int ipPortLength = 10;

  private static int userNameLength = 20;

  private static int passwordLength = 20;

  private static int defaultColumnsToDisplayForTextComponents = 32;

  private static int defaultDialogButtonLayoutAlignment = SwingConstants.LEFT;

  // This default constructor should not be used as this is a utility class.
  // It is specified here so that the default constructor is not visible.
  private PresentationUtilities() {}

  /**
   * Get the specified component's top level {@code Frame} or {@code Dialog}.
   *
   * @param childComponent the component whose top level parent needs to be retrieved.
   * @return the {@code Frame} or {@code Dialog} that contains the component, or the default frame
   *         if the component is {@code null}, or does not have a valid {@code Frame} or
   *         {@code Dialog} parent.
   */
  public static Window getWindowForComponent(final Component childComponent) {
    if (childComponent == null) {
      return JOptionPane.getRootFrame();
    }
    if ((childComponent instanceof Frame) || (childComponent instanceof Dialog)) {
      return (Window) childComponent;
    }
    return getWindowForComponent(childComponent.getParent());
  }

  /**
   * Invoke once at application startup to specify the WindowBar Icon to be used for all application
   * {@code Frame}s.
   *
   * @param image to set as the top left corner displayed icon for all {@code Frame} windows.
   */
  public static void setWindowBarIconImage(final Image image) {
    windowBarIcon.setImage(image);
  }

  /**
   * Should be invoked by all {@code Frame}s to ensure that all windows display the correct
   * application image, otherwise the image displayed will be the default Java Coffee image.<br>
   * To set the image on a {@code Frame} invoke the method sequence:<br>
   * {@code setIconImage(PresentationUtilities#getWindowBarIconImage())}.
   *
   * @return the specified image or the default Java Coffee image.
   *
   * @see #setWindowBarIconImage
   */
  public static Image getWindowBarIconImage() {
    return windowBarIcon.getImage();
  }

  /**
   * Determine whether Windows Look and Feel is supported.<br>
   * The return value can be used to add a Windows Look And Feel menu option if the application
   * supports Look And Feel changes at run time.
   *
   * @return {@code true} if supported, {@code false} otherwise.
   *
   * @see #setWindowsLookAndFeelSupport
   */
  public static boolean isWindowsLookAndFeelSupported() {
    return windowsLookAndFeelSupported;
  }

  /**
   * Invoke once at application startup if it's determined that the application has been started on
   * a platform which is able to support the Windows Look and Feel.
   *
   * @param newValue {@code true} to indicate Windows Look and Feel is supported.
   */
  public static void setWindowsLookAndFeelSupport(final boolean newValue) {
    windowsLookAndFeelSupported = newValue;
  }
  

  /**
   * Invoke to allow the supplied child window to be centered on its parent. This method also checks
   * for clipping issues to ensure that the whole child window will be visible. The {@code parent}
   * parameter could be supplied as {@code null}, which will be the case for any application Splash
   * window or the main application window. In these cases the window is centered on the display
   * screen.
   *
   * @param parent parent of the child window on which it is centered, can be {@code null}.
   * @param child child window to center on the supplied parent.
   */
  public static void centerWindowAndClipIfRequired(final Component parent, final Component child) {
    final Point pointLoc = getChildWindowCenterLocation(parent, child);
    child.setLocation(pointLoc.x, pointLoc.y);
  }

  /**
   * This method is identical to {@code centerWindowAndClipIfRequired} with the exception that the
   * point location is returned rather than setting the location on the child window.
   *
   * @param parent parent of the child window on which it is centered, can be {@code null}.
   * @param child child window to center on the supplied parent.
   * @return the point location which allows the window to be centered on its parent or the display.
   */
  public static Point getChildWindowCenterLocation(final Component parent, final Component child) {
    // Determine the Point location.
    final Point childWindowLoc = new Point();
    final Dimension childSize = child.getSize();
    Dimension parentSize = null;
    Point parentLoc = null;

    if (parent == null) {
      parentSize = Toolkit.getDefaultToolkit().getScreenSize();
      childWindowLoc.setLocation((parentSize.width - childSize.width) / 2,
          (parentSize.height - childSize.height) / 2);
    } else {
      parentSize = parent.getSize();
      parentLoc = parent.getLocation();

      int childX = ((parentSize.width - childSize.width) / 2) + parentLoc.x;
      int childY = ((parentSize.height - childSize.height) / 2) + parentLoc.y;

      // Check clipping issues.
      if (childX < 0) {
        childX = 0;
      }
      if (childY < 0) {
        childY = 0;
      }
      // Ensure whole child window is on screen.
      final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      if ((childSize.height + childY) > screenSize.height) {
        final int diff = (childSize.height + childY) - screenSize.height;
        childY -= diff;
      }
      if ((childSize.width + childX) > screenSize.width) {
        final int diff = (childSize.width + childX) - screenSize.width;
        childX -= diff;
      }
      childWindowLoc.setLocation(childX, childY);
    }
    return childWindowLoc;
  }

  /**
   * Invoke to allow entered text to be checked for printable characters.
   *
   * @param textToCheck contents to check for printable characters.
   * @return {@code true} if characters are printable, {@code false} otherwise.
   */
  public static boolean isPrintable(final String textToCheck) {
    // Check supplied text for valid characters.
    final char[] nameChar = textToCheck.toCharArray();
    for (int i = 0; i < nameChar.length; i++) {
      if (Character.isISOControl(nameChar[i]) || Character.getNumericValue(nameChar[i]) > 0xFF) {
        return false;
      }
    }
    return true;
  }

  /**
   * Invoke to allow an entered Host Address to be syntactically checked for correctness.
   *
   * @param textToCheck contents to check for IP address correctness.
   * @return {@code true} if host address is syntactically correct, {@code false} otherwise.
   */
  public static boolean isHostAddress(final String textToCheck) {
    // Check IP address for valid characters.
    // Valid characters are as per RFC 952 <hname> definition (e.g.
    // http://asg.web.cmu.edu/rfc/rfc952.html)
    final char[] addrChar = textToCheck.toCharArray();
    final int addressLength = addrChar.length;
    if (addressLength < 1) {
      // Address too short.
      return false;
    }

    // First and last characters must be alphanumeric.
    if (!Character.isLetterOrDigit(addrChar[0]) ||
        !Character.isLetterOrDigit(addrChar[addressLength - 1])) {
      return false;
    }

    // Check for legal characters.
    if (Character.isDigit(addrChar[0])) {
      // If first character is a digit then it must be an IP address, not a hostname.
      int periods = 0;
      int octetValue = Character.getNumericValue(addrChar[0]);
      for (int i = 1; i < addressLength; ++i) {
        if (addrChar[i] == '.') {
          if (++periods > 3) {
            // IP address must have 3 periods.
            return false;
          }
          if (addrChar[i - 1] == '.') {
            // Two dots in succession.
            return false;
          }
          octetValue = 0;
        } else {
          if (!Character.isDigit(addrChar[i])) {
            // Characters can only be digits or periods.
            return false;
          }
          octetValue *= 10;
          octetValue += Character.getNumericValue(addrChar[i]);
          if (octetValue > 255) {
            // Octet value too high.
            return false;
          }
        }
      }
      if (periods != 3) {
        // Must be four octets.
        return false;
      }
    } else {
      // Must be a hostname (hostnames can't start with digits).
      for (int i = 1; i < addressLength; ++i) {
        if (!Character.isLetterOrDigit(addrChar[i]) && addrChar[i] != '.' &&
            addrChar[i] != '-' && addrChar[i] != '_') {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Display a Modal/Non-Modal Message dialog box of required message type.
   *
   * @param parentComponent the window on which the dialog is to be centered on, can be
   *        {@code null}.
   * @param message could either be a single instance of {@code Object} or an array of
   *        {@code Object}s.
   * @param messageType takes the form {@code JOptionPane.INFORMATION_MESSAGE},
   *        {@code JOptionPane.ERROR_MESSAGE}, {@code JOptionPane.WARNING_MESSAGE} or
   *        {@code JOptionPane.PLAIN_MESSAGE}.
   * @param modal {@code true} if modality is required, {@code false} otherwise.
   */
  public static void displayMessageDialogOfRequiredType(final Component parentComponent,
      final Object message, final int messageType, final boolean modal) {
    String dialogTitle = null;
    if (messageType == JOptionPane.INFORMATION_MESSAGE) {
      dialogTitle = "Information";
    } else if (messageType == JOptionPane.WARNING_MESSAGE) {
      dialogTitle = "Warning";
    } else if (messageType == JOptionPane.ERROR_MESSAGE) {
      dialogTitle = "Error";
    } else {
      dialogTitle = "Information";
    }
    final CustomisedOptionPane dialogPane = new CustomisedOptionPane(message, messageType);
    final JDialog dialog = dialogPane.createDialog(parentComponent, dialogTitle);
    dialog.setModal(modal);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setResizable(false);
    // Center the information dialog on parent window.
    PresentationUtilities.centerWindowAndClipIfRequired(parentComponent, dialog);
    dialog.setVisible(true);
    if (modal) {
      dialog.dispose();
    }
  }

  /**
   * Display a modal Confirmation dialog box.
   *
   * @param parentComponent the window on which the Confirmation dialog is to be centered on, can be
   *        {@code null}.
   * @param message could either be a single instance of {@code Object} or an array of
   *        {@code Object}s.
   * @return it's either {@code JOptionPane.YES_OPTION} or {@code JOptionPane.NO_OPTION}.
   */
  public static int displayModalConfirmationDialog(final Component parentComponent, final Object message) {
    final CustomisedOptionPane dialogPane = new CustomisedOptionPane(message, JOptionPane.QUESTION_MESSAGE,
        JOptionPane.YES_NO_OPTION);
    final JDialog dialog = dialogPane.createDialog(parentComponent, "Question");
    dialog.setModal(true);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setResizable(false);
    // Center the confirmation dialog on parent window.
    PresentationUtilities.centerWindowAndClipIfRequired(parentComponent, dialog);
    dialog.setVisible(true);
    dialog.dispose();
    final Object selectedValue = dialogPane.getValue();
    if (selectedValue == null) {
      return JOptionPane.NO_OPTION;
    }
    if (selectedValue instanceof Integer) {
      return ((Integer) selectedValue).intValue();
    }
    return JOptionPane.NO_OPTION;
  }

  /**
   * Display a modal Input dialog box with OK and CANCEL buttons and where initial selection,
   * possible selection and all other options can be specified.
   *
   * @param parentComponent the window on which the Input dialog is to be centered on, can be
   *        {@code null}.
   * @param dialogTitle title for displayed dialog, in general this should simply be 'Input'.
   * @param message could either be a single instance of {@code Object} or an array of
   *        {@code Object}s.
   * @param messageType takes the form {@code JOptionPane.QUESTION_MESSAGE},
   *        {@code INFORMATION_MESSAGE}, {@code ERROR_MESSAGE}, {@code WARNING_MESSAGE} or
   *        {@code PLAIN_MESSAGE}, should ideally be {@code QUESTION_MESSAGE}.
   * @param icon if supplied will override the default (as determined by the @messageType value)
   *        decorative icon placed in the displayed dialog.
   * @param selectionValues an array of {@code Object}s that gives the possible selections.
   * @param initialSelectionValue the value used to initialise the input field.
   * @return user's input, or {@code null} meaning the user cancelled the input.
   */
  public static Object displayInputDialog(final Component parentComponent, final String dialogTitle,
      final Object message,
      final int messageType, final Icon icon, final Object[] selectionValues, final Object initialSelectionValue) {
    // Display a Model input dialog box.
    final CustomisedOptionPane dialogPane = new CustomisedOptionPane(message, messageType,
        JOptionPane.OK_CANCEL_OPTION, icon);
    dialogPane.setSelectionValues(selectionValues);
    dialogPane.setInitialSelectionValue(initialSelectionValue);
    final JDialog dialog = dialogPane.createDialog(parentComponent, dialogTitle);
    dialog.setModal(true);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setResizable(false);
    // Center the input dialog on parent window.
    PresentationUtilities.centerWindowAndClipIfRequired(parentComponent, dialog);
    dialog.setVisible(true);
    dialog.dispose();
    final Object selectedValue = dialogPane.getValue();
    if (selectedValue == null) {
      return null;
    }
    if (selectedValue instanceof Integer) {
      final int value = ((Integer) selectedValue).intValue();
      if ((value == JOptionPane.CANCEL_OPTION) || (value == JOptionPane.CLOSED_OPTION)) {
        return null;
      }
      // Return actual input selected.
      return dialogPane.getInputValue();
    }
    return selectedValue;
  }

  /**
   * Invoke to allow child windows associated with supplied {@code parentWindow} to have their 'Look
   * and Feel' updated.<br>
   * This method is currently called by classes {@code MFrame} and {@code MDialog}.<br>
   * If parameter {@code parentWindow} is supplied as {@code null} then no action is taken by this
   * method.
   *
   * @param parentWindow whose displayed child windows need to be updated for 'Look and Feel'
   *        change.
   */
  public static void updateUILookAndFeelForPostedChildWindows(final Window parentWindow) {
    if (parentWindow == null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Method taking no action as parameter parentWindow is supplied as null.");
      }
      return;
    }
    // Update Look&Feel for any child windows opened by the supplied parent.
    final Window[] ownedWindows = parentWindow.getOwnedWindows();
    if (ownedWindows != null) {
      for (Window childWindow : ownedWindows) {
        // The check for isDisplayable() ensures that we only consider
        // windows that have not been targeted for Garbage Collection.
        if ((childWindow != null) && (childWindow.isDisplayable())) {
          // Check if the child window implements the LookAndFeel interface and
          // if so call the interface method instead which will update the LookAndFeel
          // for the child window and then update the LookAndFeel for any posted secondary
          // windows owned by the child window.
          if (childWindow instanceof WindowLookAndFeelUpdateListener) {
            ((WindowLookAndFeelUpdateListener) childWindow).updateUILookAndFeel();
            childWindow.pack();
          } else {
            // Update the child window directly.
            SwingUtilities.updateComponentTreeUI(childWindow);
            childWindow.pack();
          }
        }
      }
    }
  }

  /**
   * Invoke to allow child windows associated with supplied {@code parentWindow} to be forcibly
   * disposed.<br>
   * This method is currently called by classes {@code MFrame} and {@code MDialog}.<br>
   * If parameter {@code parentWindow} is supplied as {@code null} then no action is taken by this
   * method.
   *
   * @param parentWindow whose displayed child windows need to be forcibly disposed.
   */
  public static void forceDisposeOfPostedChildWindows(final Window parentWindow) {
    if (parentWindow == null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Method taking no action as paremter parentWindow is supplied as null.");
      }
      return;
    }
    // Need to dispose of any secondary windows being displayed, whose parent is that supplied.
    final Window[] ownedWindows = parentWindow.getOwnedWindows();
    if (ownedWindows != null) {
      for (Window childWindow : ownedWindows) {
        // The check for isDisplayable() ensures that we only consider
        // windows that have not been targeted for Garbage Collection.
        if ((childWindow != null) && (childWindow.isDisplayable())) {
          // Check if the child window implements the WindowForcedDisposeListener interface
          // and if so call the interface method instead which will allow the child window
          // to force dispose for any posted secondary windows owned by the child window.
          if (childWindow instanceof WindowForcedDisposeListener) {
            ((WindowForcedDisposeListener) childWindow).doForcedDisposeAction();
          } else {
            childWindow.dispose();
          }
        }
      }
    }
  }

  /**
   * Invoke to allow child windows associated with supplied {@code parentWindow} to be locked. The
   * window lock state can only be applied to child windows that implement interface
   * {@code WindowLockControl}.<br>
   * This method is currently called by classes {@code LockableFrame} and
   * {@code LockableDialog}.<br>
   * If parameter {@code parentWindow} is supplied as {@code null} then no action is taken by this
   * method.
   *
   * @param parentWindow whose displayed child windows need to be locked.
   *
   * @see WindowLockControl#enableWindowLockedState
   */
  public static void lockPostedChildWindows(final Window parentWindow) {
    if (parentWindow == null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Method taking no action as parameter parentWindow is supplied as null.");
      }
      return;
    }
    // Need to lock any secondary windows being displayed, whose parent is that supplied.
    final Window[] ownedWindows = parentWindow.getOwnedWindows();
    if (ownedWindows != null) {
      for (Window childWindow : ownedWindows) {
        // The check for isDisplayable() ensures that we only consider
        // windows that have not been targeted for Garbage Collection.
        if ((childWindow != null) && (childWindow.isDisplayable())) {
          if (childWindow instanceof WindowLockControl) {
            ((WindowLockControl) childWindow).enableWindowLockedState();
          }
        }
      }
    }
  }

  /**
   * Invoke to allow child windows associated with supplied {@code parentWindow} to be unlocked. The
   * window unlock state can only be applied to child windows that implement interface
   * {@code WindowLockControl}.<br>
   * This method is currently called by classes {@code LockableFrame} and
   * {@code LockableDialog}.<br>
   * If parameter {@code parentWindow} is supplied as {@code null} then no action is taken by this
   * method.
   *
   * @param parentWindow whose displayed child windows need to be unlocked.
   *
   * @see WindowLockControl#disableWindowLockedState
   */
  public static void unlockPostedChildWindows(final Window parentWindow) {
    if (parentWindow == null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Method taking no action as parameter parentWindow is supplied as null.");
      }
      return;
    }
    // Need to unlock any secondary windows being displayed, whose parent is that supplied.
    final Window[] ownedWindows = parentWindow.getOwnedWindows();
    if (ownedWindows != null) {
      for (Window childWindow : ownedWindows) {
        // The check for isDisplayable() ensures that we only consider
        // windows that have not been targeted for Garbage Collection.
        if ((childWindow != null) && (childWindow.isDisplayable())) {
          if (childWindow instanceof WindowLockControl) {
            ((WindowLockControl) childWindow).disableWindowLockedState();
          }
        }
      }
    }
  }

  /**
   * This method allows a long string to be potentially broken so that it's displayed on multiple
   * lines. This is done by converting the string within a HTML wrapper where each line is
   * potentially up to the maximum number of characters allowed per line. The string will only be
   * broken if a space character is found after the maximum characters per line is checked. If no
   * space characters are encountered in the entire supplied string then it is simply returned in
   * its original form.<br>
   * This method could be invoked when displaying ToolTips on those components that can potentially
   * display long strings within a small confined area.<br>
   * This method is currently called by component {@code TableView} amongst others to provide
   * ToolTip support on those Table Cells that have their string contents truncated. In addition,
   * it's also called by classes {@code FixedLengthTextField} and {@code ReadOnlyTextField}.
   *
   * @param originalStr string to convert.
   * @param maximumCharactersPerLine maximum number of characters per line for the returned
   *        contents.
   * @return conversion string or original if conversion has not taken place.
   */
  public static String convertStringToHTMLFormatWithLineBreaks(final String originalStr,
      final int maximumCharactersPerLine) {
    final String newLineTag = "&nbsp;<br>";

    // Remove any carriage returns from the original string and replace them
    // with spaces. This will help with formatting the string to our best
    // efforts.
    String newString = originalStr.replaceAll("\n", " ");

    // Remove sequences of two or more spaces from the original string, and
    // replace them with a single space.
    newString = newString.replaceAll("  *", " ");

    // To convert string to display on multiple lines, need to split the supplied string
    // into line blocks of maximumCharactersPerLine by inserting HTML <br> after a white space
    // is found and then have the string include the HTML start and end tags.
    int spaceIndex;
    int charactersCounted = 0;
    while ((charactersCounted + maximumCharactersPerLine) < newString.length()) {
      spaceIndex = newString.indexOf(" ", charactersCounted + maximumCharactersPerLine);
      if (spaceIndex != -1) {
        final String startStr = newString.substring(0, spaceIndex);
        final String endStr = newString.substring(spaceIndex + 1);
        final String tempStr = startStr.concat(newLineTag);
        newString = tempStr + "&nbsp;" + endStr;
        charactersCounted = spaceIndex + newLineTag.length();
      } else {
        break;
      }
    }
    return new String("<html>&nbsp;" + newString + "&nbsp;</html>");
  }

  /**
   * Convenient method to check whether key released event should be processed. Suppression checks
   * are for control keys being pressed (with some exceptions), function keys and up/down/left/right
   * keys.<br>
   * The control key exceptions are (for which this method will return {@code true}):
   * <ul>
   * <li>Back Space Key</li>
   * <li>Delete Key</li>
   * <li>Paste Keys</li>
   * <li>Cut Keys</li>
   * <li>Home Key</li>
   * <li>Enter Key</li>
   * </ul>
   *
   * @param e the key event to check to determine whether it should be processed.
   * @return {@code true} if key event should be processed, {@code false} otherwise.
   */
  public static boolean processKeyReleasedEvent(final KeyEvent e) {
    // Ignore left/right arrow keys, function keys, page up/down keys.
    if (e.isActionKey()) {
      return false;
    }

    final int charType = Character.getType(e.getKeyChar());

    // Ignore empty events caused by pressing and releasing the Shift/Alt/Ctrl key.
    if (charType == Character.UNASSIGNED) {
      return false;
    }

    // Ignore escape character and control characters (except backspace, enter,
    // delete and paste controls).
    if (charType == Character.CONTROL) {
      final int keyCode = e.getKeyCode();
      if ((keyCode != KeyEvent.VK_BACK_SPACE) &&
          (keyCode != KeyEvent.VK_DELETE) &&
          (keyCode != KeyEvent.VK_H) &&
          (keyCode != KeyEvent.VK_PASTE) &&
          (keyCode != KeyEvent.VK_V) &&
          (keyCode != KeyEvent.VK_CUT) &&
          (keyCode != KeyEvent.VK_X) &&
          (keyCode != KeyEvent.VK_ENTER)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Get configured application {@code TimeZone} setting in {@code String} form. This method can be
   * called and used to map date and time values to required time zone. An attempt is made to
   * determine if this value has been set as a System property through specified key 'TIMEZONE'. If
   * not the default value 'Europe/London' is returned.
   *
   * @return retrieve timezone setting.
   */
  public static String getTimeZoneStr() {
    final String value = System.getProperty("TIMEZONE");
    if (value != null) {
      // Before setting the value ensure that the TimeZone ID supplied is valid.
      // The following method will return a default GMT TimeZone if no valid ID
      // was supplied.
      final TimeZone timeZone = TimeZone.getTimeZone(value);
      if (timeZone != null) {
        boolean timeZoneValid = true;
        if ((timeZone.getID().compareTo("GMT") == 0) && (value.compareTo("GMT") != 0)) {
          timeZoneValid = false;
        }
        if (timeZoneValid) {
          timeZoneStr = value;
        }
      }
    }
    return timeZoneStr;
  }

  /**
   * Set the application version. This method should be called at application startup.
   *
   * @param value allow the application version to be stored.
   */
  public static void setApplicationVersion(final String value) {
    applicationVersion = value;
  }

  /**
   * Get application version. If the default value has not be overwritten at application startup
   * then the value '1.00' is returned.
   *
   * @return retrieve the application version.
   *
   * @see #setApplicationVersion
   */
  public static String getApplicationVersion() {
    return applicationVersion;
  }

  /**
   * Set the default application name. This method should be called at application startup.
   *
   * @param value allow the application name to be stored.
   */
  public static void setApplicationName(final String value) {
    applicationName = value;
  }

  /**
   * Get the application name. If the default value has not be overwritten at application startup
   * then an empty string is returned.
   *
   * @return retrieve the application name.
   *
   * @see #setApplicationName
   */
  public static String getApplicationName() {
    return applicationName;
  }

  /**
   * Set the default application product branding name. This method should be called at application
   * startup.
   *
   * @param value allow the application product branding to be stored.
   */
  public static void setProductBrandingName(final String value) {
    productBrandingName = value;
  }

  /**
   * Get the application product branding name. If the default value has not be overwritten at
   * application startup then an empty string is returned.
   *
   * @return retrieve the application product branding.
   *
   * @see #setProductBrandingName
   */
  public static String getProductBrandingName() {
    return productBrandingName;
  }

  /**
   * Get JavaHelp HelpSet name. An attempt is made to determine if this value has been set as a
   * System property through specified key 'JAVAHELP_HELPSET_NAME_AND_LOCATION'. If not the default
   * empty string is returned.
   *
   * @return retrieve the application JsvaHelp HelpSet value.
   */
  public static String getJavahelpHelpSetName() {
    final String value = System.getProperty("JAVAHELP_HELPSET_NAME_AND_LOCATION");
    if (value != null) {
      javahelpHelpSetName = value;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("JavaHelp HelpSet file name is : " + javahelpHelpSetName);
    }
    return javahelpHelpSetName;
  }

  /**
   * Get JavaHelp presentation window name. An attempt is made to determine if this value has been
   * set as a System property through specified key 'JAVA_HELP_PRESENTATION_WINDOW_NAME'. If not the
   * default empty string is returned.
   *
   * @return retrieve the application JsvaHelp presentation window name.
   */
  public static String getJavaHelpPresentationWindowName() {
    final String value = System.getProperty("JAVA_HELP_PRESENTATION_WINDOW_NAME");
    if (value != null) {
      defaultJavaHelpPresentationWindowName = value;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("JavaHelp presentation window name is : " + defaultJavaHelpPresentationWindowName);
    }
    return defaultJavaHelpPresentationWindowName;
  }

  /**
   * Get field length for generic strings. An attempt is made to determine if this value has been
   * set as a System property through specified key 'GENERIC_STRING_ATTRIBUTE_LENGTH'. If not the
   * default value of 80 is returned.
   *
   * @return retrieve the application wide field length for generic strings.
   */
  public static int getGenericStringAttributeLength() {
    final Integer value = Integer.getInteger("GENERIC_STRING_ATTRIBUTE_LENGTH");
    if (value != null) {
      genericStringAttributeLength = value.intValue();
    }
    return genericStringAttributeLength;
  }

  /**
   * Get field length for customer notes. An attempt is made to determine if this value has been set
   * as a System property through specified key 'CUSTOMER_EDITABLE_NOTES_LENGTH'. If not the default
   * value of 200 is returned.
   *
   * @return retrieve the application wide field length for customer notes.
   */
  public static int getCustomerEditableNotesLength() {
    final Integer value = Integer.getInteger("CUSTOMER_EDITABLE_NOTES_LENGTH");
    if (value != null) {
      customerEditableNotesLength = value.intValue();
    }
    return customerEditableNotesLength;
  }

  /**
   * Get field length for IP Port. An attempt is made to determine if this value has been set as a
   * System property through specified key 'IP_PORT_LENGTH'. If not the default value of 10 is
   * returned.
   *
   * @return retrieve the application wide field length for IP port.
   */
  public static int getIPPortLength() {
    final Integer value = Integer.getInteger("IP_PORT_LENGTH");
    if (value != null) {
      ipPortLength = value.intValue();
    }
    return ipPortLength;
  }

  /**
   * Get field length for User Name. An attempt is made to determine if this value has been set as a
   * System property through specified key 'USER_NAME_LENGTH'. If not the default value of 20 is
   * returned.
   *
   * @return retrieve the application wide field length for user name.
   */
  public static int getUserNameLength() {
    final Integer value = Integer.getInteger("USER_NAME_LENGTH");
    if (value != null) {
      userNameLength = value.intValue();
    }
    return userNameLength;
  }

  /**
   * Get field length for Password. An attempt is made to determine if this value has been set as a
   * System property through specified key 'PASSWORD_LENGTH'. If not the default value of 20 is
   * returned.
   *
   * @return retrieve the application wide field length for password.
   */
  public static int getPasswordLength() {
    final Integer value = Integer.getInteger("PASSWORD_LENGTH");
    if (value != null) {
      passwordLength = value.intValue();
    }
    return passwordLength;
  }

  /**
   * Get the default number of columns to display for text entry UI components such as
   * {@code JTextField} and editable {@code JComboBox}. An attempt is made to determine if this
   * value has been set as a System property through specified key
   * 'DEFAULT_COLUMNS_TO_DISPLAY_FOR_TEXT_COMPONENTS'. If not the default value of 32 is returned.
   *
   * @return retrieve the application wide text entry displayable columns.
   */
  public static int getDefaultColumnsToDisplayForTextComponents() {
    final Integer value = Integer.getInteger("DEFAULT_COLUMNS_TO_DISPLAY_FOR_TEXT_COMPONENTS");
    if (value != null) {
      defaultColumnsToDisplayForTextComponents = value.intValue();
    }
    return defaultColumnsToDisplayForTextComponents;
  }

  /**
   * Get the required button alignment for {@code Dialog}s that are required to display a row of
   * button(s) at the bottom of their window. An attempt is made to determine if this value has been
   * set as a System property through specified key 'DIALOG_BUTTON_LAYOUT_ALIGNMENT'. If not the
   * default value default value of {@code SwingConstants#LEFT} is returned.
   *
   * @return a value representing either {@code SwingConstants#LEFT}, {@code SwingConstants#RIGHT}
   *         or {@code SwingConstants#CENTER}.
   */
  public static int getDefaultDialogButtonLayoutAlignment() {
    final String value = System.getProperty("DIALOG_BUTTON_LAYOUT_ALIGNMENT");
    if (value != null) {
      // Set default to Left.
      defaultDialogButtonLayoutAlignment = SwingConstants.LEFT;

      if (value.compareToIgnoreCase("CENTER") == 0) {
        defaultDialogButtonLayoutAlignment = SwingConstants.CENTER;
      } else if (value.compareToIgnoreCase("RIGHT") == 0) {
        defaultDialogButtonLayoutAlignment = SwingConstants.RIGHT;
      }
    }
    return defaultDialogButtonLayoutAlignment;
  }

  /**
   * Convenient method that can be used to return a {@code JPanel} instance with required layout
   * manager configured to support the specified application button layout alignment policy.
   *
   * @return panel with layout manager assigned and configured to support button layout policy.
   *
   * @see #getDefaultDialogButtonLayoutAlignment
   */
  public static JPanel getButtonPanelWithRequiredButtonAlignment() {
    // Determine Dialog button alignment.
    final int defaultButtonAlignment = PresentationUtilities.getDefaultDialogButtonLayoutAlignment();
    int alignmentToSet = FlowLayout.LEFT;
    if (defaultButtonAlignment == SwingConstants.CENTER) {
      alignmentToSet = FlowLayout.CENTER;
    } else if (defaultButtonAlignment == SwingConstants.RIGHT) {
      alignmentToSet = FlowLayout.RIGHT;
    }
    final JPanel buttonDetailsPanel = new JPanel(new FlowLayout(alignmentToSet));
    buttonDetailsPanel.setBorder(BorderFactory.createEmptyBorder(PresentationUtilities.BUTTON_EDGE_SPACING,
        PresentationUtilities.BUTTON_EDGE_SPACING, PresentationUtilities.BUTTON_EDGE_SPACING,
        PresentationUtilities.BUTTON_EDGE_SPACING));
    return buttonDetailsPanel;
  }

  /**
   * Determine whether the application should adhere to the TMN Style Guide. The value is read from
   * System property through specified key 'tmn.styleguide.bootstrap'. If not specified the value
   * {@code false} is returned.
   *
   * @return {@code true} to indicate conformance, {@code false} otherwise.
   */
  public static boolean isApplicationToAdhereToTMNStyleGuide() {
    final boolean adhereTo = Boolean.getBoolean("tmn.styleguide.bootstrap");
    if (logger.isTraceEnabled()) {
      logger.trace("TMN Stlye Guide Conformance is set to : " + adhereTo);
    }
    return adhereTo;
  }

  /**
   * Determine whether a Help option either on a window menu bar or used as a push button should be
   * right aligned. Generally, this method will check if the application is running on a windows
   * platform and return {@code false}, for all other cases the value {@code true} is returned.
   *
   * @return {@code true} if Help option should be right aligned, {@code false} otherwise.
   */
  public static boolean isHelpOptionRightAligned() {
    boolean helpRightAligned = true;

    // Check if application has been started on Windows platform in which
    // case the Help option should be left aligned.
    try {
      final String osName = System.getProperty("os.name");

      if ((osName != null) && (osName.length() > 0)) {
        if (osName.startsWith("Windows")) {
          helpRightAligned = false;
        }
      }
    } catch (Exception e) {
      logger.error("Unable to retrieve Property os.name - " + e.getMessage());
    }
    return helpRightAligned;
  }

  /**
   * Alphabetically order (ignoring case) the supplied items in the {@code Enumeration} and return
   * result in a {@code List} to the calling method.<br>
   * <br>
   * Note: Need to ensure that the {@code toString} method of each {@code Object} contained in the
   * enumeration is overridden to return the actual {@code Object}'s string value.
   *
   * @param container supplied container which requires its entries to be sorted.
   * @return container with entries in alphabetical sort order ignoring case.
   * @throws NullPointerException if parameter {@code container} is supplied as {@code null}.
   */
  public static List<?> sortSuppliedContainer(final Enumeration<?> container) {
    if (container == null) {
      throw new NullPointerException("Method parameter container is incorrectly supplied as null.");
    }
    final List<Object> sortedContainer = new ArrayList<Object>();

    // Step round the enumeration putting each entry (Object) into the
    // sortedContainer in alphabetical order ignoring case.

    while (container.hasMoreElements()) {
      final Object element = container.nextElement();
      if (element != null) {
        final String elementStrValue = element.toString();
        int i;

        // Loop round the sortedContainer.
        for (i = 0; i < sortedContainer.size(); i++) {
          // Obtain the 'current' entry from the sortedContainer.
          final Object currentElement = sortedContainer.get(i);
          if (currentElement != null) {
            final int compareRes = elementStrValue.compareToIgnoreCase(
                currentElement.toString());
            // If supplied argument (i.e. sorted List element) less than supplied
            // container element then continue.
            if (compareRes > 0) {
              continue;
            }
            // The supplied argument is either greater than the container
            // element or equal to it when ignoring case. Therefore, the
            // container element generally needs to be inserted
            // before the current entry, except when the two are equal and
            // it needs to be determined whether we need to insert before or after.
            int indexToInsert = i;
            // Check if comparison result returned equality ignoring case.
            if (compareRes == 0) {
              final int equalityRes = elementStrValue.compareTo(
                  currentElement.toString());
              // Determine whether we need to insert after.
              if (equalityRes < 0) {
                indexToInsert += 1;
              }
            }
            sortedContainer.add(indexToInsert, element);
            // Copy over the inserted index value.
            i = indexToInsert;
            break;
          }
        }
        if (i == sortedContainer.size()) {
          // Not been added - Add to the start/end of the list.
          sortedContainer.add(element);
        }
      }
    }
    return sortedContainer;
  }

  /**
   * Alphabetically order (ignoring case) the supplied items in the {@code Object} array and return
   * result in a {@code List} to the calling method.<br>
   * <br>
   * Note: Need to ensure that the {@code toString} method of {@code Object} is overridden to return
   * the actual {@code Object}'s string value.
   *
   * @param container supplied array which requires its entries to be sorted.
   * @return container with entries in alphabetical sort order ignoring case.
   * @throws NullPointerException if parameter {@code container} is supplied as {@code null}.
   */
  public static List<Object> sortSuppliedContainer(final Object[] container) {
    if (container == null) {
      throw new NullPointerException("Method parameter container is incorrectly supplied as null.");
    }
    final List<Object> sortedContainer = new ArrayList<Object>();

    // Step round the array putting each entry (Object) into the
    // sortedContainer in alphabetical order ignoring case.
    for (int i = 0; i < container.length; i++) {
      final Object element = container[i];
      if (element != null) {
        final String elementStrValue = element.toString();
        int j;

        // Loop round the sortedContainer.
        for (j = 0; j < sortedContainer.size(); j++) {
          // Obtain the 'current' entry from the sortedContainer.
          final Object currentElement = sortedContainer.get(j);
          if (currentElement != null) {
            final int compareRes = elementStrValue.compareToIgnoreCase(
                currentElement.toString());
            // If supplied argument (i.e. sorted List element) less than
            // supplied container element then continue.
            if (compareRes > 0) {
              continue;
            }
            // The supplied argument is either greater than the container
            // element or equal to it when ignoring case. Therefore, the
            // container element generally needs to be inserted
            // before the current entry, except when the two are equal and
            // it needs to be determined whether we need to insert before or after.
            int indexToInsert = j;
            // Check if comparison result returned equality ignoring case.
            if (compareRes == 0) {
              final int equalityRes = elementStrValue.compareTo(
                  currentElement.toString());
              // Determine whether we need to insert after.
              if (equalityRes < 0) {
                indexToInsert += 1;
              }
            }
            sortedContainer.add(indexToInsert, element);
            // Copy over the inserted index value.
            j = indexToInsert;
            break;
          }
        }
        if (j == sortedContainer.size()) {
          // Not been added - Add to the start/end of the list.
          sortedContainer.add(element);
        }
      }
    }
    return sortedContainer;
  }

  /**
   * Invoke to add an {@code entry} to supplied ComboBox Model in the correct alphabetical location
   * ignoring case. This method assumes the ComboBox Model passed in is already sorted (using method
   * {@code sortSuppliedContainer} of this class or another sorting method). In addition, this
   * method does not check whether the supplied entry parameter already exists in the supplied
   * ComboBox Model and therefore this method will add it regardless.<br>
   * <br>
   * Note: You need to ensure that the {@code toString} method of {@code Object} is overridden to
   * return the actual {@code Object}'s string value.
   *
   * @param model ComboBox Model into which supplied entry is to be inserted.
   * @param entry to insert into ComboBox Model at correct location ignoring case. If supplied as
   *        {@code null} then no action is taken.
   * @return the index value at which the entry was inserted into the ComboBox Model, -1 is returned
   *         if no action was taken.
   * @throws NullPointerException if parameter {@code model} is supplied as {@code null}.
   */
  public static int addEntryToSortedComboBox(final DefaultComboBoxModel model, final Object entry) {
    if (model == null) {
      throw new NullPointerException("Method parameter model is incorrectly supplied as null.");
    }

    int index = -1;

    if (entry != null) {
      final String entryStrValue = entry.toString();

      for (index = 0; index < model.getSize(); index++) {
        // Obtain the 'current' entry from the ComboBox Model.
        final Object currentElement = model.getElementAt(index);
        if (currentElement != null) {
          final int compareRes = entryStrValue.compareToIgnoreCase(
              currentElement.toString());
          // If supplied argument (i.e. DefaultComboBoxModel element) less than
          // entry parameter then continue.
          if (compareRes > 0) {
            continue;
          }
          // The supplied argument is either greater than the entry parameter or equal
          // to it when ignoring case. Therefore, the entry parameter generally needs to be
          // inserted before the current entry, except when the two are equal and it needs to
          // be determined whether we need to insert before or after.
          int indexToInsert = index;
          // Check if comparison result returned equality ignoring case.
          if (compareRes == 0) {
            final int equalityRes = entryStrValue.compareTo(
                currentElement.toString());
            // Determine whether we need to insert after.
            if (equalityRes < 0) {
              indexToInsert += 1;
            }
          }
          model.insertElementAt(entry, indexToInsert);
          // Copy over the inserted index value.
          index = indexToInsert;
          break;
        }
      }
      if (index == model.getSize()) {
        // Not been added - Add to the start/end of the list.
        model.addElement(entry);
      }
    }
    return index;
  }

  /**
   * Allow interface {@code ApplicationActivityListener} to be registered for notifications of
   * future application activity.<br>
   * If listener is {@code null}, no exception is thrown and no action is performed.
   *
   * @param listener the {@code ApplicationActivityListener} to be added.
   *
   * @see #clientActivityDetected(ComponentEvent)
   * @see #removeApplicationActivityListener(ApplicationActivityListener)
   */
  public static void addApplicationActivityListener(final ApplicationActivityListener listener) {
    if (listener == null) {
      return;
    }
    synchronized (applicationActivityListeners) {
      if (!applicationActivityListeners.contains(listener)) {
        applicationActivityListeners.add(listener);
      }
    }
  }

  /**
   * De-register interface {@code ApplicationActivityListener}.<br>
   * This method should be used to remove {@code ApplicationActivityListener} that was registered.
   * If listener is {@code null}, no exception is thrown and no action is performed.<br>
   *
   * @param listener the {@code ApplicationActivityListener} to be removed.
   *
   * @see #addApplicationActivityListener(ApplicationActivityListener)
   */
  public static void removeApplicationActivityListener(final ApplicationActivityListener listener) {
    if (listener == null) {
      return;
    }
    synchronized (applicationActivityListeners) {
      applicationActivityListeners.remove(listener);
    }
  }

  /**
   * Invoked by a number of components that detect window or component activity.<br>
   * This method could be used to reset any inactivity timeout that may be in operation for the
   * client application.<br>
   * This method is currently called by {@code MDialog} and {@code MFrame} both of which detect
   * window activity and component moved and resized events. This method provides scope for other
   * components to call it in future if detection of other types of events such as focus or input
   * (mouse or keyboard) is required.<br>
   * To determine what the actual trigger event is the interested listener can call method
   * {@code ComponentEvent#getID}. The event type returned can be checked against the event masks
   * defined in super class {@code AWTEvent} and its subclasses.
   *
   * @param evt the triggered event and will in general be either {@code ComponentEvent} itself or a
   *        subclass of it such as {@code WindowEvent}. The event will be forwarded to any
   *        registered application activity listener (i.e. {@code ApplicationActivityListener}).
   */
  public static void clientActivityDetected(final ComponentEvent evt) {
    synchronized (applicationActivityListeners) {
      if (!applicationActivityListeners.isEmpty() &&
          logger.isDebugEnabled()) {
        logger.debug("Registered listener(s) being notified for client " +
            " application activity.");
      }
      for (ApplicationActivityListener listener : applicationActivityListeners) {
        if (listener != null) {
          listener.applicationActivityDetected(evt);
        }
      }
    }
  }
}
