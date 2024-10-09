package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.PlainDocument;

/**
 * This class extends {@code JPanel} and represents a wrapper class to house both an editable
 * {@code JComboBox} and {@code JToolBar} with a Search Button that is used as an explicit user
 * pressed 'Search' against the entered text. This class attaches to the required listeners to
 * manage all trigger events that could occur for an editable ComboBox, which include:<br>
 * <ul>
 * <li>Enter key pressed whilst entering characters.</li>
 * <li>Focus lost whilst editing/entering characters.</li>
 * <li>A ComboBox list selection made by the user.</li>
 * <li>The Search button pressed by the user.</li>
 * </ul>
 * The trigger events of interest are specified by the window which is going to use this component.
 * In addition, it can supply an interface {@code EditableComboBoxTriggerNotifyListener} which
 * represents the callback methods to be called once a trigger event has occurred. The Search Button
 * is only shown if the required {@code SEARCH_BUTTON_TRIGGER_EVENT} is set in the supplied trigger
 * mask. By default this button has a default label and tooltip text set, which can be overridden if
 * required through the accessor methods provided. This class also uses class
 * {@code FixedLengthDocument} to specify restrictions on the number of characters entered for
 * editable {@code JComboBox}. The displayed width of the editable {@code JComboBox} will be
 * restricted if display column restriction is specified through the required constructor attribute.
 * This class also provides the facility to offer intelligent ToolTip support for selected ComboBox
 * entry and displayed drop down list. The ToolTip will only be displayed if the text contents are
 * partially hidden.
 */
@SuppressWarnings("serial")
public final class EditableComboBoxWithTriggerNotify extends JPanel implements ToolTipValidation {

  // Define bit-mask values to define Trigger Events of interest.

  /** Enter key pressed during character entry trigger event */
  public static final int ENTER_DURING_CHARACTER_ENTRY_TRIGGER_EVENT = 1;

  /** List selection made from ComboBox drop down list trigger event */
  public static final int LIST_SELECTION_TRIGGER_EVENT = 2;

  /** Displayed search button pressed trigger event */
  public static final int SEARCH_BUTTON_TRIGGER_EVENT = 4;

  /** Focus lost trigger event */
  public static final int FOCUS_LOST_TRIGGER_EVENT = 8;

  // Enable all trigger events by default, this value is overwritten at class construction.
  private final int comboBoxEventTriggerMask;

  private boolean comboBoxTriggerEventsEnabled = true;

  // Member field to hold the maximum number of characters allowed for text entry on editable
  // JComboBox. If this value remains at zero after this class is instantiated
  // then there is no restriction on number of characters allowed to be entered.
  private int maxCharactersAllowed = 0;

  // Member field to hold maximum number of columns to display for the editable JComboBox.
  // If this value remains at zero after this class is instantiated then preferred width of the
  // editable ComboBox will be whatever naturally results from the component implementation.
  private int maxColumns = 0;

  // Declare the editable ComboBox component.
  private JComboBox editableComboBox;

  // Declare the model that provides the displayed list of items.
  private final DefaultComboBoxModel comboBoxModel;

  // The following member field is required to avoid the
  // ComboBox action events being processed when initialise()
  // method is called or when the model is being overridden.
  private boolean ignoreComboBoxListSelectionEvent = false;

  // The following member field is used to track list selection to avoid
  // processing the same action on the user selecting the same
  // ComboBox list entry.
  private int previouslySelectedComboBoxListIndex = -1;

  // Declare the component for adding a Search button next to the ComboBox.
  private JButton searchButton;

  // This member field is assigned to the supplied interested party for receiving
  // notifications when a trigger event has occurred.
  private final EditableComboBoxTriggerNotifyListener interesetedListener;

  // The following member field is used for restricting the JComboBox width.
  private Dimension preferredWidthDim;

  // Setup the required listeners to service the different trigger events.

  private final CustomKeyAdaptor keyAdaptor = new CustomKeyAdaptor();

  private final CRActionListener crActionListener = new CRActionListener();

  private final CustomFocusAdaptor focusAdaptor = new CustomFocusAdaptor();

  private final ComboBoxListSelectionListener listSelectionListener = new ComboBoxListSelectionListener();

  /**
   * Construct an editable ComboBox with {@code DefaultComboBoxModel} container and allows display
   * column restriction to be set including maximum number of characters allowed to be entered. A
   * Search button can also be displayed if the {@code eventTriggerMask} value supplied is correctly
   * configured.
   *
   * @param interestedListenerComponent interface to notify when trigger event is processed. A value
   *        of {@code null} can be supplied if there is no interested listener.
   * @param eventTriggerMask should be set to an 'OR' of the trigger bit mask values required as
   *        declared at the top of this class. If the value 0 is specified then all trigger events
   *        are registered.
   * @param columns the number of columns to use to calculate the preferred width >= 0; if columns
   *        is set to 0, the preferred width will be whatever naturally results from the component
   *        implementation.
   * @param maximumCharacters maximum number of characters allowed for entry; if specified as 0 then
   *        no restrictions are placed on the number of characters entered.
   * @exception IllegalArgumentException if {@code maximumCharacters} or {@code columns} < 0.
   */
  public EditableComboBoxWithTriggerNotify(final EditableComboBoxTriggerNotifyListener interestedListenerComponent,
      final int eventTriggerMask, final int columns, final int maximumCharacters) {
    super();
    interesetedListener = interestedListenerComponent;
    comboBoxEventTriggerMask = eventTriggerMask;
    maxCharactersAllowed = maximumCharacters;
    maxColumns = columns;
    comboBoxModel = new DefaultComboBoxModel();
    initialise();
  }

  /**
   * Construct an editable ComboBox with {@code DefaultComboBoxModel} container (which is populated
   * with data supplied through parameter {@code items}) and allows display column restriction to be
   * set including maximum number of characters allowed to be entered. A Search button can also be
   * displayed if the {@code eventTriggerMask} value supplied is correctly configured.
   *
   * @param items the list of data that is required to be inserted into the editable ComboBox's
   *        model.
   * @param interestedListenerComponent interface to notify when trigger event is processed. A value
   *        of {@code null} can be supplied if there is no interested listener.
   * @param eventTriggerMask should be set to an 'OR' of the trigger bit mask values required as
   *        declared at the top of this class. If the value 0 is specified then all trigger events
   *        are registered.
   * @param columns the number of columns to use to calculate the preferred width >= 0; if columns
   *        is set to 0, the preferred width will be whatever naturally results from the component
   *        implementation.
   * @param maximumCharacters maximum number of characters allowed for entry; if specified as 0 then
   *        no restrictions are placed on the number of characters entered.
   * @exception IllegalArgumentException if {@code maximumCharacters} or {@code columns} < 0.
   */
  public EditableComboBoxWithTriggerNotify(final Vector<?> items,
      final EditableComboBoxTriggerNotifyListener interestedListenerComponent,
      final int eventTriggerMask, final int columns, final int maximumCharacters) {
    super();
    interesetedListener = interestedListenerComponent;
    comboBoxEventTriggerMask = eventTriggerMask;
    maxCharactersAllowed = maximumCharacters;
    maxColumns = columns;
    comboBoxModel = new DefaultComboBoxModel(items);
    initialise();
  }

  /**
   * Construct an editable ComboBox with {@code DefaultComboBoxModel} container (which is populated
   * with data supplied through parameter {@code items}) and allows display column restriction to be
   * set including maximum number of characters allowed to be entered. A Search button can also be
   * displayed if the {@code eventTriggerMask} value supplied is correctly configured.
   *
   * @param items the list of data that is required to be inserted into the editable ComboBox's
   *        model.
   * @param interestedListenerComponent interface to notify when trigger event is processed. A value
   *        of {@code null} can be supplied if there is no interested listener.
   * @param eventTriggerMask should be set to an 'OR' of the trigger bit mask values required as
   *        declared at the top of this class. If the value 0 is specified then all trigger events
   *        are registered.
   * @param columns the number of columns to use to calculate the preferred width >= 0; if columns
   *        is set to 0, the preferred width will be whatever naturally results from the component
   *        implementation.
   * @param maximumCharacters maximum number of characters allowed for entry; if specified as 0 then
   *        no restrictions are placed on the number of characters entered.
   * @exception IllegalArgumentException if {@code maximumCharacters} or {@code columns} < 0.
   */
  public EditableComboBoxWithTriggerNotify(final Object[] items,
      final EditableComboBoxTriggerNotifyListener interestedListenerComponent,
      final int eventTriggerMask, final int columns, final int maximumCharacters) {
    super();
    interesetedListener = interestedListenerComponent;
    comboBoxEventTriggerMask = eventTriggerMask;
    maxCharactersAllowed = maximumCharacters;
    maxColumns = columns;
    comboBoxModel = new DefaultComboBoxModel(items);
    initialise();
  }

  private void initialise() {
    if (maxCharactersAllowed < 0) {
      throw new IllegalArgumentException(
          "Maximum allowed characters for entry less than zero.");
    }
    if (maxColumns < 0) {
      throw new IllegalArgumentException("Columns less than zero.");
    }

    ignoreComboBoxListSelectionEvent = true;

    preferredWidthDim = new Dimension(0, 0);

    setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

    editableComboBox = new JComboBox(comboBoxModel) {

      // Override the following three methods to allow us to restrict the
      // width of the displayed ComboBox if required column restriction
      // has been specified.
      @Override
      public Dimension getPreferredSize() {
        if ((getItemCount() == 0) || (EditableComboBoxWithTriggerNotify.this.maxColumns == 0)) {
          return super.getPreferredSize();
        }
        if (EditableComboBoxWithTriggerNotify.this.maxColumns > 0) {
          final int preferredWidth = EditableComboBoxWithTriggerNotify.this.maxColumns
              * EditableComboBoxWithTriggerNotify.this.getColumnWidth();
          final int largestListEntryWidth = EditableComboBoxWithTriggerNotify.this
              .determineLargestListEntryWidth();
          if (largestListEntryWidth > preferredWidth) {
            // Add ComboBox arrow button offset to width.
            EditableComboBoxWithTriggerNotify.this.preferredWidthDim.setSize(preferredWidth
                + EditableComboBoxWithTriggerNotify.this.getArrowButtonWidth(), super.getPreferredSize().height);
            return EditableComboBoxWithTriggerNotify.this.preferredWidthDim;
          }
        }
        return super.getPreferredSize();
      }

      @Override
      public Dimension getMinimumSize() {
        return getPreferredSize();
      }

      @Override
      public Dimension getMaximumSize() {
        return getPreferredSize();
      }

      // Override following method to allow default ToolTip value to
      // be specified.
      @Override
      public void setSelectedIndex(final int anIndex) {
        super.setSelectedIndex(anIndex);

        setToolTipText((anIndex == -1) ? null
            : EditableComboBoxWithTriggerNotify.this.validateToolTipToDisplay(
                EditableComboBoxWithTriggerNotify.this.getComboboxEditorValue(), true));
      }
    };
    // Set ComboBox to editable, otherwise we will not get the JTextField Editor
    // component created.
    editableComboBox.setEditable(true);
    final int listSize = comboBoxModel.getSize();
    editableComboBox.setMaximumRowCount(Math.min(listSize,
        PresentationUtilities.MAX_DISPLAY_ROW_LIST_FOR_COMBOBOX_LISTS));
    // Ensure that the index is set before we attach an selection listener otherwise we will
    // get an selection event.
    editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
    if ((comboBoxEventTriggerMask & LIST_SELECTION_TRIGGER_EVENT) != 0) {
      // Add an Itemlistener to notify interested party when a new ComboBox list
      // selection is made.
      editableComboBox.addItemListener(listSelectionListener);
    }
    // Override Combobox renderer to allow ToolTip support to be provided
    // on posted drop down list.
    editableComboBox.setRenderer(new ComboBoxListCellRendererWithToolTipSupport(this));

    // Call following method to register listeners against the ComboBox editor component.
    setupEditorComponentListeners();

    // Declare the Search Button components
    final ImageIcon searchIcon = new ImageIcon(getClass().getResource(
        "/com/mystuff/swingextensions/presentationshared/images/Search.gif"));
    // Declare an action to notify interested party that the user has pressed the Search button.
    searchButton = new JButton("Go");
    searchButton.setIcon(searchIcon);
    searchButton.setToolTipText("Search Entered Resource");
    searchButton.addActionListener(new SearchActionListener());
    // Disable by default.
    searchButton.setEnabled(false);

    final JToolBar toolBar = new JToolBar();
    // The following effect is only applied if Windows/Metal L&F is set.
    toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    // Do not allow the toolBar to be floatable.
    toolBar.setFloatable(false);
    toolBar.add(searchButton);

    // Add the ComboBox to the panel.
    add(editableComboBox);
    // Add the Search Button if required.
    if ((comboBoxEventTriggerMask & SEARCH_BUTTON_TRIGGER_EVENT) != 0) {
      // Add space between ComboBox and Search Button.
      final Box.Filler buttonFiller = new Box.Filler(PresentationUtilities.SMALLBUTTONFILLER_SIZE,
          PresentationUtilities.SMALLBUTTONFILLER_SIZE, PresentationUtilities.SMALLBUTTONFILLER_SIZE);
      add(buttonFiller);
      // Add the Search button.
      add(toolBar);
    }

    // Add a property change listener to the JComboBox to be informed if the UI Look&Feel
    // has changed after instantiation to allow the listeners for the ComboBox editor component
    // to be re-registered.
    editableComboBox.addPropertyChangeListener("UI", new UIPropertyChangeListener());

    ignoreComboBoxListSelectionEvent = false;
  }

  // This method is similar to that of {@code JTextField}, which takes the width
  // of character <em>m</em> for the font used to determine the width of a single column.
  // This method is used in order to restrict the displayed width of the ComboBox.
  private int getColumnWidth() {
    final FontUIResource fontUIResource = (FontUIResource) UIManager.get("ComboBox.font");
    final FontMetrics fontMetrics = getFontMetrics(fontUIResource);
    return fontMetrics.charWidth('m');
  }

  // It is not possible to determine the size of the Arrow Down button (used when a
  // {@code JComboBox} component is rendered) unless we provide our own UI component
  // to render the {@code JComboBox} by extending class {@code BasicComboBoxUI}.
  // However, in doing this we have to provide extended versions for the different Look&Feel
  // representations. Therefore, by default assume the button width occupies two columns of
  // the displayed ComboBox.
  // This method is used for the ToolTip calculation and also for restricting the JComboBox width.
  private int getArrowButtonWidth() {
    return (getColumnWidth() * 2);
  }

  // This method is used in order to restrict the displayed width of the ComboBox.
  private int determineLargestListEntryWidth() {
    int largestEntryWidth = 0;

    final FontUIResource fontUIResource = (FontUIResource) UIManager.get("ComboBox.font");
    final FontMetrics fontMetrics = getFontMetrics(fontUIResource);

    final ComboBoxModel model = editableComboBox.getModel();
    for (int i = 0; i < model.getSize(); i++) {
      final Object item = model.getElementAt(i);
      if (item != null) {
        final int textWidth = fontMetrics.stringWidth(item.toString());
        if (textWidth > largestEntryWidth) {
          largestEntryWidth = textWidth;
        }
      }
    }
    return largestEntryWidth;
  }

  private void setupEditorComponentListeners() {
    // Retrieve the JComboBox's editor text field.
    // NOTE: Do not hold a reference to the JTextField editor component as the editor
    // component is changed if the Look&Feel is changed after component instantiation.
    final Component editorComponent = editableComboBox.getEditor().getEditorComponent();
    if ((editorComponent != null) && (editorComponent instanceof JTextField)) {
      final JTextField comboBoxEditorTextField = (JTextField) editorComponent;
      // Set number of columns to display on the ComboBox's editor text field.
      comboBoxEditorTextField.setColumns(maxColumns);
      // Set number of enterable characters allowed.
      comboBoxEditorTextField.setDocument(
          (maxCharactersAllowed > 0) ? new FixedLengthDocument(comboBoxEditorTextField, maxCharactersAllowed)
              : new PlainDocument());
      if (comboBoxTriggerEventsEnabled) {
        // Add a key listener to notify interested party when characters are
        // entered/removed during editing.
        comboBoxEditorTextField.addKeyListener(keyAdaptor);
        if ((comboBoxEventTriggerMask & ENTER_DURING_CHARACTER_ENTRY_TRIGGER_EVENT) != 0) {
          // Add an ActionListener to notify interested party when the user
          // presses the Enter key for an editable ComboBox.
          comboBoxEditorTextField.addActionListener(crActionListener);
        }
        // Determine if we need to add a Focus Lost Trigger event against
        // the ComboBox editor.
        if ((comboBoxEventTriggerMask & FOCUS_LOST_TRIGGER_EVENT) != 0) {
          comboBoxEditorTextField.addFocusListener(focusAdaptor);
        }
      }
    }
  }

  /**
   * Allow the editable ComboBox to be enabled or disabled.
   *
   * @param v {@code true} if ComboBox is to be enabled.
   */
  public void setEditableComboBoxEnabled(final boolean v) {
    editableComboBox.setEnabled(v);
    if (PresentationUtilities.isApplicationToAdhereToTMNStyleGuide()) {
      // Retrieve the JComboBox's editor text field and set it's background colour.
      final Component editorComponent = editableComboBox.getEditor().getEditorComponent();
      if ((editorComponent != null) && (editorComponent instanceof JTextField)) {
        final JTextField comboBoxEditorTextField = (JTextField) editorComponent;
        comboBoxEditorTextField.setBackground(v ? PresentationUtilities.TMN_READ_WRITE_FIELDS_BG_COLOUR
            : PresentationUtilities.TMN_READ_ONLY_FIELDS_BG_COLOUR);
      }
    }
  }

  /**
   * Get whether editable ComboBox is enabled or disabled.
   *
   * @return {@code true} if the component is enabled, {@code false} otherwise.
   */
  public boolean isEditableComboBoxEnabled() {
    return editableComboBox.isEnabled();
  }

  /**
   * Allow any setup triggers to be subsequently disabled or re-enabled.
   *
   * @param v {@code true} if triggers are to be re-enabled, {@code false} otherwise.
   */
  public void setEditableComboBoxTriggersEnabled(final boolean v) {
    // Determine if triggers need to be disabled or enabled.
    if (!v) {
      if (comboBoxTriggerEventsEnabled) {
        // Disable required triggers.
        final Component editorComponent = editableComboBox.getEditor().getEditorComponent();
        if ((editorComponent != null) && (editorComponent instanceof JTextField)) {
          final JTextField comboBoxEditorTextField = (JTextField) editorComponent;
          // Remove KeyListener.
          comboBoxEditorTextField.removeKeyListener(keyAdaptor);
          if ((comboBoxEventTriggerMask & ENTER_DURING_CHARACTER_ENTRY_TRIGGER_EVENT) != 0) {
            // Remove the ActionListener.
            comboBoxEditorTextField.removeActionListener(crActionListener);
          }
          if ((comboBoxEventTriggerMask & FOCUS_LOST_TRIGGER_EVENT) != 0) {
            // Remove the FocusListener.
            comboBoxEditorTextField.removeFocusListener(focusAdaptor);
          }
        }
        if ((comboBoxEventTriggerMask & LIST_SELECTION_TRIGGER_EVENT) != 0) {
          // Remove ItemListener.
          editableComboBox.removeItemListener(listSelectionListener);
        }
        if ((comboBoxEventTriggerMask & SEARCH_BUTTON_TRIGGER_EVENT) != 0) {
          searchButton.setEnabled(false);
        }
        comboBoxTriggerEventsEnabled = false;
      }
    } else {
      if (!comboBoxTriggerEventsEnabled) {
        // Enable required triggers.
        final Component editorComponent = editableComboBox.getEditor().getEditorComponent();
        if ((editorComponent != null) && (editorComponent instanceof JTextField)) {
          final JTextField comboBoxEditorTextField = (JTextField) editorComponent;
          // Add KeyListener.
          comboBoxEditorTextField.addKeyListener(keyAdaptor);
          if ((comboBoxEventTriggerMask & ENTER_DURING_CHARACTER_ENTRY_TRIGGER_EVENT) != 0) {
            // Add the ActionListener.
            comboBoxEditorTextField.addActionListener(crActionListener);
          }
          if ((comboBoxEventTriggerMask & FOCUS_LOST_TRIGGER_EVENT) != 0) {
            // Add the FocusListener.
            comboBoxEditorTextField.addFocusListener(focusAdaptor);
          }
        }
        if ((comboBoxEventTriggerMask & LIST_SELECTION_TRIGGER_EVENT) != 0) {
          // Add ItemListener.
          editableComboBox.addItemListener(listSelectionListener);
        }
        // Determine if we need to re-enable to Search Button based on already
        // entered or selected text.
        if ((comboBoxEventTriggerMask & SEARCH_BUTTON_TRIGGER_EVENT) != 0) {
          if (editableComboBox.isEnabled()) {
            final ComboBoxEditor editor = editableComboBox.getEditor();
            final Object item = editor.getItem();
            if (item != null) {
              if (item.toString().length() > 0) {
                searchButton.setEnabled(true);
              }
            }
          }
        }
        comboBoxTriggerEventsEnabled = true;
      }
    }
  }

  /**
   * Allow the default 'Go' text displayed on the Search button to be overridden.
   *
   * @param newText ensure that the string supplied is internationalised if required.
   */
  public void setSearchButtonText(final String newText) {
    searchButton.setText(newText);
  }

  /**
   * Allow the default 'Search Entered Resource' Tooltip to be overridden.
   *
   * @param newToolTip ensure that the string supplied is internationalised if required.
   */
  public void setSearchButtonToolTipText(final String newToolTip) {
    searchButton.setToolTipText(newToolTip);
  }

  /**
   * Get maximum number of enterable characters allowed for editable ComboBox.
   *
   * @return maximum number of characters allowed to be entered >= 0.
   */
  public int getMaximumNumberOfCharactersAllowed() {
    return maxCharactersAllowed;
  }

  /**
   * Set the maximum enterable characters allowed for editable ComboBox.
   *
   * @param maximumCharacters maximum number of characters allowed for entry; if specified as 0 then
   *        no restrictions are placed on the number of characters entered.
   * @exception IllegalArgumentException if {@code maximumCharacters} is less than 0.
   */
  public void setMaximumNumberOfCharactersAllowed(final int maximumCharacters) {
    if (maximumCharacters < 0) {
      throw new IllegalArgumentException(
          "Maximum allowed characters for entry less than zero.");
    }
    maxCharactersAllowed = maximumCharacters;
    // Retrieve the JComboBox's editor text field.
    final Component editorComponent = editableComboBox.getEditor().getEditorComponent();
    if ((editorComponent != null) && (editorComponent instanceof JTextField)) {
      final JTextField comboBoxEditorTextField = (JTextField) editorComponent;
      comboBoxEditorTextField.setDocument(
          (maxCharactersAllowed > 0) ? new FixedLengthDocument(comboBoxEditorTextField, maxCharactersAllowed)
              : new PlainDocument());
    }
  }

  /**
   * Get the specified display column restriction for editable ComboBox.
   *
   * @return the number of columns >= 0.
   */
  public int getColumns() {
    return maxColumns;
  }

  /**
   * Set the specified display column restriction for editable ComboBox.
   *
   * @param columns the number of columns >= 0.
   * @exception IllegalArgumentException if {@code columns} is less than 0.
   */
  public void setColumns(final int columns) {
    if (columns < 0) {
      throw new IllegalArgumentException("Columns less than zero.");
    }
    maxColumns = columns;
    // Retrieve the JComboBox's editor text field.
    final Component editorComponent = editableComboBox.getEditor().getEditorComponent();
    if ((editorComponent != null) && (editorComponent instanceof JTextField)) {
      final JTextField comboBoxEditorTextField = (JTextField) editorComponent;
      comboBoxEditorTextField.setColumns(maxColumns);
    }
  }

  /**
   * Get the string form of ComboBox's {@code Editor} edited item.
   *
   * @return edited item.
   */
  public String getComboboxEditorValue() {
    final ComboBoxEditor editor = editableComboBox.getEditor();
    final Object item = editor.getItem();
    if (item != null) {
      return item.toString();
    }
    return "";
  }

  /**
   * Set the ComboBox's {@code Editor} contents to that supplied. This will cancel any editing if
   * necessary. Note the value supplied can be {@code null}.
   *
   * @param valueToSet editor contents to set.
   */
  public void setComboBoxEditorValue(final Object valueToSet) {
    editableComboBox.getEditor().setItem(valueToSet);
    searchButton.setEnabled(((valueToSet != null) &&
        (valueToSet.toString().length() > 0)) ? true : false);
  }

  /**
   * Allow the ComboBox's drop down list popup to be forcibly made visible or not.
   *
   * @param v {@code true} if the ComboBox's drop down list is to be made visible, {@code false}
   *        otherwise.
   */
  public void setComboBoxPopupVisbile(final boolean v) {
    editableComboBox.setPopupVisible(v);
  }

  /**
   * Get the ComboBox's {@code Editor} component {@code Object} value, which may be a non
   * {@code String} {@code Object} and thus allow the calling class to cast the {@code Object} to
   * expected type.
   *
   * @return editor's edited item in {@code Object} form.
   */
  public Object getComboboxEditorItem() {
    final ComboBoxEditor editor = editableComboBox.getEditor();
    return editor.getItem();
  }

  /**
   * Set the selected item in the ComboBox display area to the object in the argument. If
   * {@code itemToSelect} is in the list, the display area shows selected item.
   *
   * @param itemToSelect the list object to select; use {@code null} to clear the selection.
   */
  public void setComboBoxSelectedItem(final Object itemToSelect) {
    editableComboBox.setSelectedItem(itemToSelect);
  }

  /**
   * Select the item in the ComboBox at index {@code indexToSelect}.
   *
   * @param indexToSelect an integer specifying the list item to select, where 0 specifies the first
   *        item in the list and -1 indicates no selection.
   */
  public void setComboBoxSelectedIndex(final int indexToSelect) {
    // If the index is set to -1 then the interested party is not informed
    // of a trigger event, therefore set the tracking index to -1 also.
    if (indexToSelect == -1) {
      previouslySelectedComboBoxListIndex = indexToSelect;
    }
    editableComboBox.setSelectedIndex(indexToSelect);
  }

  /**
   * Get index value specifying the currently selected list item.
   *
   * @return an integer specifying the currently selected list item index, where 0 specifies the
   *         first item in the list; or -1 if no item is selected.
   */
  public int getComboBoxSelectedIndex() {
    return editableComboBox.getSelectedIndex();
  }

  /**
   * Get the data model used by the editable {@code JComboBox}.
   *
   * @return the model that provides the displayed list of items.
   */
  public DefaultComboBoxModel getComboBoxModel() {
    return comboBoxModel;
  }

  /**
   * Get the number of entries in the data model used by the editable {@code JComboBox}.
   *
   * @return number of entries in data model.
   */
  public int getComboBoxModelSize() {
    return comboBoxModel.getSize();
  }

  /**
   * Clear all entries stored in the data model used by editable {@code JComboBox}.
   */
  public void clearComboBoxModel() {
    ignoreComboBoxListSelectionEvent = true;
    // Remove all existing entries from ComboBox Model.
    comboBoxModel.removeAllElements();
    editableComboBox.setMaximumRowCount(1);
    previouslySelectedComboBoxListIndex = -1;
    editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
    ignoreComboBoxListSelectionEvent = false;
  }

  /**
   * Override entries of the ComboBox model used for editable {@code JComboBox} with those entries
   * supplied in {@code model} parameter. Ensure that the supplied {@code model} entries are sorted
   * if required.
   *
   * @param model entries to override the editable {@code JComboBox} model with.
   */
  public void setComboBoxModel(final ComboBoxModel model) {
    ignoreComboBoxListSelectionEvent = true;

    // Remove all existing entries from ComboBox Model.
    comboBoxModel.removeAllElements();
    // Next add each entry in supplied container.
    for (int i = 0; i < model.getSize(); i++) {
      comboBoxModel.addElement(model.getElementAt(i));
    }
    final int newListSize = comboBoxModel.getSize();
    editableComboBox.setMaximumRowCount(Math.min(newListSize,
        PresentationUtilities.MAX_DISPLAY_ROW_LIST_FOR_COMBOBOX_LISTS));
    if (newListSize > 1) {
      // Multiple entries returned, therefore make default selection nothing, thus
      // forcing the user to make required selection.
      previouslySelectedComboBoxListIndex = -1;
      editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
      searchButton.setEnabled(false);
    } else {
      // Single entry returned therefore select it by default.
      previouslySelectedComboBoxListIndex = 0;
      editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
      final Object selectedItem = editableComboBox.getSelectedItem();
      if (selectedItem != null) {
        if (selectedItem.toString().length() > 0) {
          // Inform Interface Listener that a List selection has been made.
          comboBoxEditingTriggerOcurred(selectedItem.toString(), LIST_SELECTION_TRIGGER_EVENT);
        }
      }
      searchButton.setEnabled(true);
    }
    ignoreComboBoxListSelectionEvent = false;
  }

  /**
   * Override entries of the ComboBox model used for editable {@code JComboBox} with those entries
   * supplied in {@code model} parameter. Ensure that the supplied {@code model} entries are sorted
   * if required.
   *
   * @param model entries to override the editable {@code JComboBox} model with.
   */
  public void setComboBoxModel(final Vector<?> model) {
    ignoreComboBoxListSelectionEvent = true;

    // Remove all existing entries from ComboBox Model.
    comboBoxModel.removeAllElements();
    // Next add each entry in supplied container.
    for (int i = 0; i < model.size(); i++) {
      comboBoxModel.addElement(model.elementAt(i));
    }
    final int newListSize = comboBoxModel.getSize();
    editableComboBox.setMaximumRowCount(Math.min(newListSize,
        PresentationUtilities.MAX_DISPLAY_ROW_LIST_FOR_COMBOBOX_LISTS));
    if (newListSize > 1) {
      // Multiple entries returned, therefore make default selection nothing, thus
      // forcing the user to make required selection.
      previouslySelectedComboBoxListIndex = -1;
      editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
      searchButton.setEnabled(false);
    } else {
      // Single entry returned therefore select it by default.
      previouslySelectedComboBoxListIndex = 0;
      editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
      final Object selectedItem = editableComboBox.getSelectedItem();
      if (selectedItem != null) {
        if (selectedItem.toString().length() > 0) {
          // Inform Interface Listener that a List selection has been made.
          comboBoxEditingTriggerOcurred(selectedItem.toString(), LIST_SELECTION_TRIGGER_EVENT);
        }
      }
      searchButton.setEnabled(true);
    }
    ignoreComboBoxListSelectionEvent = false;
  }

  /**
   * Allow a new entry to be added to the editable {@code JComboBox} model.<br>
   * The entry is checked against the existing model entries to ensure that it does not already
   * exist. If not the new entry is inserted in alphabetical order.
   *
   * @param value entry to add into the editable {@code JComboBox} model. No action taken if
   *        supplied as {@code null}.
   */
  public void addNewEntryToComboBoxModel(final Object value) {
    boolean entryAlreadyInModel = false;

    for (int i = 0; i < comboBoxModel.getSize(); i++) {
      final Object listEntry = comboBoxModel.getElementAt(i);
      if (listEntry != null) {
        if (listEntry.toString().equals(value)) {
          entryAlreadyInModel = true;
          ignoreComboBoxListSelectionEvent = true;
          previouslySelectedComboBoxListIndex = i;
          editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
          ignoreComboBoxListSelectionEvent = false;
          break;
        }
      }
    }
    if (!entryAlreadyInModel) {
      ignoreComboBoxListSelectionEvent = true;
      previouslySelectedComboBoxListIndex = PresentationUtilities.addEntryToSortedComboBox(
          comboBoxModel, value);
      editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
      final int listSize = comboBoxModel.getSize();
      editableComboBox.setMaximumRowCount(Math.min(listSize,
          PresentationUtilities.MAX_DISPLAY_ROW_LIST_FOR_COMBOBOX_LISTS));
      ignoreComboBoxListSelectionEvent = false;
    }
  }

  /**
   * Determine if supplied {@code value} already exists in the editable {@code JComboBox} model and
   * allows the entry to be selected if it exists.
   *
   * @param value entry to check if it exists in the editable {@code JComboBox} model.
   * @param selectEntryIfExists {@code true} to allow entry to be selected if it exists,
   *        {@code false} otherwise.
   * @return {@code true} if entry exists, {@code false} otherwise.
   */
  public boolean doesEntryExistInComboBoxModel(final Object value, final boolean selectEntryIfExists) {
    boolean entryAlreadyInModel = false;

    for (int i = 0; i < comboBoxModel.getSize(); i++) {
      final Object listEntry = comboBoxModel.getElementAt(i);
      if (listEntry != null) {
        if (listEntry.toString().equals(value)) {
          entryAlreadyInModel = true;
          if (selectEntryIfExists) {
            ignoreComboBoxListSelectionEvent = true;
            previouslySelectedComboBoxListIndex = i;
            editableComboBox.setSelectedIndex(previouslySelectedComboBoxListIndex);
            ignoreComboBoxListSelectionEvent = false;
          }
          break;
        }
      }
    }
    return entryAlreadyInModel;
  }

  /**
   * This method implements the only method of {@code ToolTipValidation} interface and will only
   * return valid ToolTip if text field contents are partially hidden.<br>
   * This method is used to validate Tooltip support for a {@code JComboBox} popup list items and
   * also for the {@code JComboBox} selected item, in which case the parameter
   * {@code listScrollBarDisplayed} supplied is set to {@code true} also as generally the width of
   * the {@code JComboBox} button is the same as the list scrollbar.
   *
   * @param value contents to convert to Tooltip.
   * @param listScrollBarDisplayed {@code true} if the {@code JComboBox} popup list's vertical
   *        scroll bar is displayed, {@code false} otherwise.
   * @return the Tooltip to display or {@code null}.
   */
  @Override
  public String validateToolTipToDisplay(final Object value, final boolean listScrollBarDisplayed) {
    String toolTipToSet = null;
    if (value != null) {
      final FontUIResource fontUIResource = (FontUIResource) UIManager.get("ComboBox.font");
      final FontMetrics fontMetrics = getFontMetrics(fontUIResource);
      final int textWidth = fontMetrics.stringWidth(value.toString());
      if (textWidth > 0) {
        int componentWidth = editableComboBox.getWidth();
        if (listScrollBarDisplayed) {
          // It is assumed here the ComboBox button width is the same as the list scroll
          // bar width and the ComboBox button width is approximated.
          componentWidth -= getArrowButtonWidth();
        }
        if (textWidth > componentWidth) {
          toolTipToSet = PresentationUtilities.convertStringToHTMLFormatWithLineBreaks(value.toString(),
              PresentationUtilities.getDefaultColumnsToDisplayForTextComponents());
        }
      }
    }
    return toolTipToSet;
  }

  // This method is called for the following trigger events:
  // ENTER_DURING_CHARACTER_ENTRY_TRIGGER_EVENT
  // LIST_SELECTION_TRIGGER_EVENT
  // SEARCH_BUTTON_TRIGGER_EVENT
  // FOCUS_LOST_TRIGGER_EVENT
  private void comboBoxEditingTriggerOcurred(final String enteredOrSelectedText, final int eventTrigger) {
    if (interesetedListener != null) {
      interesetedListener.comboBoxEditingComplete(enteredOrSelectedText, eventTrigger);
    }
  }

  /**
   * Handle UI property change listener for the editable ComboBox.
   */
  private final class UIPropertyChangeListener implements PropertyChangeListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(final PropertyChangeEvent e) {
      // Re-register the newly loaded ComboBox editor with the required listeners.
      EditableComboBoxWithTriggerNotify.this.setupEditorComponentListeners();

      // As a new ComboBox editor component is loaded, any contents displayed by
      // the editor are cleared, thus disable the Search button and reset the
      // selected index.
      EditableComboBoxWithTriggerNotify.this.searchButton.setEnabled(false);
      EditableComboBoxWithTriggerNotify.this.ignoreComboBoxListSelectionEvent = true;
      EditableComboBoxWithTriggerNotify.this.previouslySelectedComboBoxListIndex = -1;
      EditableComboBoxWithTriggerNotify.this.editableComboBox.setSelectedIndex(
          EditableComboBoxWithTriggerNotify.this.previouslySelectedComboBoxListIndex);
      EditableComboBoxWithTriggerNotify.this.ignoreComboBoxListSelectionEvent = false;
      // Inform Interface Listener that current contents have been cleared.
      if (EditableComboBoxWithTriggerNotify.this.interesetedListener != null) {
        EditableComboBoxWithTriggerNotify.this.interesetedListener.comboBoxEditingFieldEmpty();
      }
    }
  }

  /**
   * Handle ComboBox list selection event.
   */
  private final class ComboBoxListSelectionListener implements ItemListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void itemStateChanged(final ItemEvent e) {
      // Determine whether we need to set the ToolTip text.
      EditableComboBoxWithTriggerNotify.this.editableComboBox.setToolTipText(
          EditableComboBoxWithTriggerNotify.this.validateToolTipToDisplay(e.getItem(), true));

      // Determine if we need to grey out the Search Button.
      final ComboBoxEditor editor = EditableComboBoxWithTriggerNotify.this.editableComboBox.getEditor();
      final Object item = editor.getItem();
      if (item != null) {
        EditableComboBoxWithTriggerNotify.this.searchButton.setEnabled((item.toString().length() > 0) ? true : false);
      }

      // Process combobox list selection.
      if (e.getStateChange() == ItemEvent.SELECTED) {
        if ((!EditableComboBoxWithTriggerNotify.this.ignoreComboBoxListSelectionEvent)
            && (EditableComboBoxWithTriggerNotify.this.previouslySelectedComboBoxListIndex != EditableComboBoxWithTriggerNotify.this.editableComboBox
                .getSelectedIndex())) {
          EditableComboBoxWithTriggerNotify.this.previouslySelectedComboBoxListIndex =
              EditableComboBoxWithTriggerNotify.this.editableComboBox.getSelectedIndex();
          final Object selectedItem = EditableComboBoxWithTriggerNotify.this.editableComboBox.getSelectedItem();
          if (selectedItem != null) {
            if (selectedItem.toString().length() > 0) {
              // Inform Interface Listener that a new List selection has been made.
              EditableComboBoxWithTriggerNotify.this.comboBoxEditingTriggerOcurred(selectedItem.toString(),
                  EditableComboBoxWithTriggerNotify.LIST_SELECTION_TRIGGER_EVENT);
            }
          }
        }
      }
    }
  }

  /**
   * Handle specific focus event for editable text entry component.
   */
  private final class CustomFocusAdaptor extends FocusAdapter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void focusLost(final FocusEvent e) {
      final ComboBoxEditor editor = EditableComboBoxWithTriggerNotify.this.editableComboBox.getEditor();
      final Object item = editor.getItem();
      if (item != null) {
        if (item.toString().length() > 0) {
          // Inform Interface Listener that focus was lost during editing.
          EditableComboBoxWithTriggerNotify.this.comboBoxEditingTriggerOcurred(item.toString(),
              EditableComboBoxWithTriggerNotify.FOCUS_LOST_TRIGGER_EVENT);
        }
      }
    }
  }

  /**
   * Handle action listener event for Search button.
   */
  private final class SearchActionListener implements ActionListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
      final ComboBoxEditor editor = EditableComboBoxWithTriggerNotify.this.editableComboBox.getEditor();
      final Object item = editor.getItem();
      if (item != null) {
        if (item.toString().length() > 0) {
          // Inform Interface Listener that editing has finished.
          EditableComboBoxWithTriggerNotify.this.comboBoxEditingTriggerOcurred(item.toString(),
              EditableComboBoxWithTriggerNotify.SEARCH_BUTTON_TRIGGER_EVENT);
        }
      }
    }
  }

  /**
   * Handle carriage return (CR) key being pressed for the editable text entry component.
   */
  private final class CRActionListener implements ActionListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
      // Retrieve the JComboBox's editor and determine if CR
      // key needs to be processed.
      final ComboBoxEditor editor = EditableComboBoxWithTriggerNotify.this.editableComboBox.getEditor();
      final Object item = editor.getItem();
      if (item != null) {
        // Process carriage return as long as the editing text is not empty.
        if (item.toString().length() > 0) {
          // Inform Interface Listener that editing has finished.
          EditableComboBoxWithTriggerNotify.this.comboBoxEditingTriggerOcurred(item.toString(),
              EditableComboBoxWithTriggerNotify.ENTER_DURING_CHARACTER_ENTRY_TRIGGER_EVENT);
        }
      }
    }
  }

  /**
   * Handle specific key events for editable text field component. This handler allows associated
   * components to be enabled/disabled based on data entry.
   */
  private final class CustomKeyAdaptor extends KeyAdapter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(final KeyEvent e) {
      final int keyCode = e.getKeyCode();
      // Ensure that the key event is one that we wish to process and
      // also that it is not the CR key being released (as that is handled
      // by a separate action listener).
      if (PresentationUtilities.processKeyReleasedEvent(e) &&
          (keyCode != KeyEvent.VK_ENTER)) {
        // Retrieve the JComboBox's editor text field.
        final Component editorComponent =
            EditableComboBoxWithTriggerNotify.this.editableComboBox.getEditor().getEditorComponent();
        if ((editorComponent != null) && (editorComponent instanceof JTextField)) {
          final JTextField comboBoxEditorTextField = (JTextField) editorComponent;
          final boolean charactersEntered = !comboBoxEditorTextField.getText().isEmpty();
          EditableComboBoxWithTriggerNotify.this.editableComboBox.setToolTipText(charactersEntered
              ? EditableComboBoxWithTriggerNotify.this.validateToolTipToDisplay(comboBoxEditorTextField.getText(), true)
              : null);
          EditableComboBoxWithTriggerNotify.this.searchButton.setEnabled(charactersEntered);
          if (EditableComboBoxWithTriggerNotify.this.interesetedListener != null) {
            if (charactersEntered) {
              // Inform Interface Listener that key was pressed during editing.
              EditableComboBoxWithTriggerNotify.this.interesetedListener.comboBoxEditingCharactersEntered();
            } else {
              EditableComboBoxWithTriggerNotify.this.interesetedListener.comboBoxEditingFieldEmpty();
            }
          }
        }
      }
    }
  }
}
