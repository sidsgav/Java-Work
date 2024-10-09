package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An implementation of a drop down button, like the back and forward buttons of Firefox/Internet
 * Explorer browsers. This component displays a popup menu (a small window that pops up and displays
 * a series of choices) just under the button's bottom left location, which appears at the user's
 * request.<br>
 * The appearance and behaviour of this component can be specified such that only a single button is
 * displayed encompassing the supplied button image and a drop down arrow symbol. In this
 * configuration clicking the button always posts a popup menu requiring the user to make a menu
 * option selection. The other configuration results in the appearance of two buttons, one housing
 * the supplied button image and the other a drop down arrow symbol. Here pressing the button with
 * the supplied image results in the popup menu's default option being selected (without posting the
 * popup menu). However, pressing the button displaying the down arrow will result in the popup menu
 * being posted requiring a user selection.<br>
 * This component will automatically enable or disable the drop down button dependent on menu items
 * being added or removed from this component's popup menu. To add or remove menu items to the popup
 * menu simply call method {@code getMenu}. This approach allows the interested component to add
 * seperators between menu items if required.
 */
@SuppressWarnings("serial")
public final class DropDownButton extends AbstractButton {

  private static final Insets BUTTON_MARGIN = new Insets(2, 3, 2, 3);

  private static final Insets BUTTON_BORDER = new Insets(3, 2, 3, 2);

  private final JToolBar toolBar = new JToolBar();

  private final JPopupMenu dropDownPopupMenu = new JPopupMenu();

  private JButton primaryButton;

  private Icon primaryButtonIcon;

  private String primaryButtonText;

  private String primaryButtonToolTipText;

  private int preferredPrimaryButtonWidth = 0;

  private int preferredPrimaryButtonHeight = 0;

  private JButton dropDownButton;

  private boolean defaultPopupOptionEnabled = false;

  private boolean rollOverPolicy = false;

  private Container parentContainer;

  /**
   * Creates a drop down button whose presentation and behavioural characteristics are defined by
   * supplied parameters.
   *
   * @param buttonIcon the Icon image to display on the drop down button. If this is supplied as
   *        {@code null} then the button will display text as supplied by parameter
   *        {@code buttonText}. However, without a button image this class does not serve its
   *        intended purpose as generally a drop down button is presented as a button displaying an
   *        image accompanied by a drop down arrow symbol.
   * @param buttonText the text to display on the drop down button. This can be supplied as
   *        {@code null} if the button image is supplied instead.
   * @param enableDefaultPopupMenuOption used to define the behaviour and presentation of the drop
   *        down button. Essentially, if this parameter is specified as {@code false} then selecting
   *        the drop down button always posts a popup menu requiring the user to make a menu option
   *        selection. In addition, the presentation results in the display of a single button
   *        incorporating both the button's image (if supplied) or text and a down arrow symbol.
   *        Otherwise, the presentation results in the appearance of two buttons, one housing the
   *        button's supplied image or text and the other incorporating a down arrow symbol. Here
   *        pressing the primary button results in the popup menu's default option being selected
   *        (without posting the popup menu). However, pressing the button displaying the down arrow
   *        will result in the popup menu being posted requiring a user selection. Additionally, if
   *        this parameter is specified as {@code true} then the tool tip displayed for the drop
   *        down button will reflect the popup menu's default option, unless overridden by parameter
   *        {@code buttonToolTipText}.
   * @param buttonToolTipText the tooltip text to display for the drop down button. If the button
   *        provides support for a default popup menu option (as defined by parameter
   *        {@code enableDefaultPopupMenuOption}) then the button's tooltip will be taken from the
   *        default popup menu option text if this parameter is supplied as {@code null}.
   * @param preferredWidth set the preferred width of the primary drop down button. If
   *        {@code preferredWidth} is 0, this class will determine the preferred width based on the
   *        Icon image supplied and fixed margin and border values. If no Icon image is supplied the
   *        UI will be asked for the preferred width.
   * @param preferredHeight set the preferred height of the primary drop down button. If
   *        {@code preferredHeight} is 0, this class will determine the preferred height based on
   *        the Icon image supplied and fixed margin and border values. If no Icon image is supplied
   *        the UI will be asked for the preferred height.
   * @throws IllegalArgumentException if the specified button icon is {@code null} and the specified
   *         button text is also supplied as {@code null} or empty string. This exception is also
   *         thrown if parameters {@code preferredWidth} or {@code preferredHeight} are supplied as
   *         < 0.
   */
  public DropDownButton(final Icon buttonIcon, final String buttonText, final boolean enableDefaultPopupMenuOption,
      final String buttonToolTipText, final int preferredWidth, final int preferredHeight)
      throws IllegalArgumentException {
    super();
    // Check for illegal arguments supplied.
    if (preferredWidth < 0) {
      throw new IllegalArgumentException("Preferred button width less than zero.");
    }
    if (preferredHeight < 0) {
      throw new IllegalArgumentException("Preferred button height less than zero.");
    }
    primaryButtonIcon = buttonIcon;
    primaryButtonText = buttonText;
    defaultPopupOptionEnabled = enableDefaultPopupMenuOption;
    primaryButtonToolTipText = buttonToolTipText;
    preferredPrimaryButtonWidth = preferredWidth;
    preferredPrimaryButtonHeight = preferredHeight;

    // Check for other illegal arguments supplied.
    if (primaryButtonIcon == null) {
      if ((primaryButtonText == null) || (primaryButtonText.length() == 0)) {
        throw new IllegalArgumentException("No button Icon or text supplied.");
      }
    }
    initialise();
  }

  private void initialise() {
    // Setup tool bar properties.
    toolBar.setFloatable(false);
    toolBar.setBorder(null);
    toolBar.setBorderPainted(false);
    toolBar.setFocusable(false);

    dropDownPopupMenu.addContainerListener(new PopupMenuContainerListener());

    primaryButton = new JButton(new PrimaryButtonAction()) {

      private Dimension prefPrimaryDim = new Dimension(0, 0);

      @Override
      public Dimension getPreferredSize() {
        final Icon icon = getIcon();
        final String buttonText = getText();
        if ((icon != null) && ((buttonText == null) || (buttonText.length() == 0))) {
          int buttonWidth = 0;
          int buttonHeight = 0;
          // Check if a preferred width and height have been specified and assign.
          if (DropDownButton.this.preferredPrimaryButtonWidth > 0) {
            buttonWidth = DropDownButton.this.preferredPrimaryButtonWidth;
          } else {
            // Calculate width required to draw Button with image.
            buttonWidth = icon.getIconWidth() + DropDownButton.BUTTON_MARGIN.left
                + DropDownButton.BUTTON_MARGIN.right + DropDownButton.BUTTON_BORDER.left
                + DropDownButton.BUTTON_BORDER.right;
          }
          if (DropDownButton.this.preferredPrimaryButtonHeight > 0) {
            buttonHeight = DropDownButton.this.preferredPrimaryButtonHeight;
          } else {
            // Calculate height required to draw Button with image.
            buttonHeight = icon.getIconHeight() + DropDownButton.BUTTON_MARGIN.top
                + DropDownButton.BUTTON_MARGIN.bottom + DropDownButton.BUTTON_BORDER.top
                + DropDownButton.BUTTON_BORDER.bottom;
          }
          prefPrimaryDim.setSize(buttonWidth, buttonHeight);
          return prefPrimaryDim;
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
    };
    primaryButton.setHorizontalAlignment(SwingConstants.CENTER);
    primaryButton.setEnabled(false);
    primaryButton.setFocusPainted(false);
    primaryButton.setDefaultCapable(false);
    if (!defaultPopupOptionEnabled) {
      primaryButton.setIcon(new SmallDownArrow(primaryButtonIcon));
    } else if (primaryButtonIcon != null) {
      primaryButton.setIcon(primaryButtonIcon);
    }
    if ((primaryButtonText != null) && (primaryButtonText.length() > 0)) {
      primaryButton.setText(primaryButtonText);
      primaryButton.setHorizontalTextPosition(SwingConstants.LEADING);
    }
    if ((primaryButtonToolTipText != null) && (primaryButtonToolTipText.length() > 0)) {
      primaryButton.setToolTipText(primaryButtonToolTipText);
    }

    dropDownButton = new JButton(new DropDownButtonAction()) {

      private Dimension preferredDim = new Dimension(0, 0);

      @Override
      public Dimension getPreferredSize() {
        if (DropDownButton.this.defaultPopupOptionEnabled) {
          final Icon icon = getIcon();
          if (icon != null) {
            // Ensure that the height of the down arrow button is the same as the
            // primary button and also the width is just large enough to allow the
            // down arrow to be drawn.
            final int iconWidth = icon.getIconWidth() + DropDownButton.BUTTON_MARGIN.left
                + DropDownButton.BUTTON_MARGIN.right + DropDownButton.BUTTON_BORDER.left
                + DropDownButton.BUTTON_BORDER.right;
            preferredDim.setSize(iconWidth, DropDownButton.this.primaryButton.getPreferredSize().height);
            return preferredDim;
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
    };
    dropDownButton.setHorizontalAlignment(SwingConstants.CENTER);
    dropDownButton.setEnabled(false);
    dropDownButton.setIcon(new SmallDownArrow(null));
    dropDownButton.setFocusPainted(false);
    dropDownButton.setDefaultCapable(false);
    if ((primaryButtonToolTipText != null) && (primaryButtonToolTipText.length() > 0)) {
      dropDownButton.setToolTipText(primaryButtonToolTipText);
    }

    toolBar.add(primaryButton);
    if (defaultPopupOptionEnabled) {
      // Add a mouse listener to the primary button to allow the rollover/pressed/armed state
      // to be set for the drop down button.
      primaryButton.getModel().addChangeListener(new PrimaryButtonChangeListener());
      toolBar.add(dropDownButton);
    }
    this.add(toolBar);

    this.setBorder(null);
    this.setContentAreaFilled(false);
  }

  /**
   * Get this component's popup menu allowing the interested component to add or remove menu items.
   *
   * @return the popup menu that displays it's menu options.
   */
  public JPopupMenu getMenu() {
    return dropDownPopupMenu;
  }

  /**
   * Enable (or disable) the drop down button.<br>
   * This component is disabled initially by default until it's popup menu is populated.<br>
   * This component can only be enabled as long as it's popup menu is populated.
   *
   * @param enable {@code true} to enable the button, {@code false} otherwise.
   */
  @Override
  public void setEnabled(final boolean enable) {
    boolean cont = true;

    if (enable && (dropDownPopupMenu.getComponentCount() == 0)) {
      cont = false;
    }
    if (cont) {
      primaryButton.setEnabled(enable);
    }
  }

  /**
   * Determine whether this component is enabled. An enabled component can respond to user input and
   * generate events. This component is disabled initially by default until it's popup menu is
   * populated. This component may be enabled (as long as it's popup menu is populated) or disabled
   * by calling its {@code setEnabled} method.
   *
   * @return {@code true} if the component is enabled, {@code false} otherwise.
   */
  @Override
  public boolean isEnabled() {
    return primaryButton.isEnabled();
  }

  /**
   * Notifies this component that it now has a parent component. When this method is invoked, the
   * chain of parent components is set up with {@code KeyboardAction} event listeners.<br>
   * This method is overridden to ascertain the roll over policy of the parent {@code JToolBar} if
   * applicable.
   */
  @Override
  public void addNotify() {
    super.addNotify();

    // Retrieve the parent of this component and determine if it's a JToolBar
    // component. If so ascertain it's roll over policy and apply to the tool
    // bar component of this class.
    parentContainer = this.getParent();
    if (parentContainer instanceof JToolBar) {
      rollOverPolicy = ((JToolBar) parentContainer).isRollover();
      toolBar.putClientProperty("JToolBar.isRollover", (rollOverPolicy ? Boolean.TRUE : Boolean.FALSE));

      if (defaultPopupOptionEnabled && rollOverPolicy) {
        // Add a mouse listener to the drop down button to allow the roll over
        // state to be set for the primary button.
        dropDownButton.addMouseListener(new DropDownButtonRollOverListener());
      }
    }
  }

  /**
   * Overridden so that the Look and Feel used for this component's popup menu can be updated.
   */
  @Override
  public void updateUI() {
    super.updateUI();

    SwingUtilities.updateComponentTreeUI(dropDownPopupMenu);
  }

  /**
   * Determine if this component's popup menu contains no menu options.
   *
   * @return {@code true} if the popup menu contains no menu options, {@code false} otherwise.
   */
  public boolean isEmpty() {
    return (dropDownPopupMenu.getComponentCount() == 0);
  }

  private void setupButtonTooltipText() {
    if (primaryButtonToolTipText == null) {
      if (defaultPopupOptionEnabled && (!isEmpty())) {
        final Component comp = dropDownPopupMenu.getComponent(0);
        if (comp != null) {
          primaryButton.setToolTipText(comp.getName());
          dropDownButton.setToolTipText(comp.getName());
        }
      }
    }
  }

  private void postPopupMenu() {
    if (!isEmpty()) {
      // Post the popup menu at the required location (i.e. under the
      // button's location).
      dropDownPopupMenu.show(primaryButton, primaryButton.getX(),
          primaryButton.getY() + primaryButton.getHeight());
    }
  }

  /**
   * Inner class allows the drawing of a small downward pointing arrow. The constructor accepts an
   * additional {@code Icon} which is drawn extra to the arrow but which can be supplied as
   * {@code null}.
   */
  private final class SmallDownArrow implements Icon {

    private final Color ARROW_COLOR = Color.black;

    private final int ARROW_WIDTH = 5;

    private final int ARROW_HEIGHT = 3;

    // Declare separation offset between supplied Icon and down arrow.
    private final int SEPARATION_OFFSET = 8;

    private final int ARROW_WIDTH_OFFSET = 2;

    private Icon icon;

    /**
     * Constructor supplying additional Icon to be displayed.
     *
     * @param theIcon additional display icon, could be supplied as {@code null}.
     */
    private SmallDownArrow(final Icon theIcon) {
      super();
      icon = theIcon;
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
      int xP = x;
      int yP = y;

      if (icon != null) {
        // First draw constructor supplied additional icon.
        icon.paintIcon(c, g, x, y);
        // Work out the new x and y values to allow the down arrow
        // to be drawn at the correct position.
        xP += icon.getIconWidth() + SEPARATION_OFFSET;
        final int maxHeight = Math.max(icon.getIconHeight(), ARROW_HEIGHT);
        yP += (maxHeight - ARROW_HEIGHT) / 2;
      } else {
        // The down arrow is required to be displayed on it's own button
        // and therefore calculate the width and height centre rather than
        // relying on the supplied values. It has been noticed that the
        // supplied x and y values cannot be relied on when switching between
        // Look and Feels at application run time.
        if (c instanceof JButton) {
          final JButton button = (JButton) c;
          xP = (button.getWidth() - ARROW_WIDTH) / 2;
          yP = (button.getHeight() - ARROW_HEIGHT) / 2;
        }
      }

      final Color currentColor = g.getColor();

      // Draw the down arrow.
      g.setColor(ARROW_COLOR);
      g.drawLine(xP, yP, xP + 4, yP);
      g.drawLine(xP + 1, yP + 1, xP + 3, yP + 1);
      g.drawLine(xP + 2, yP + 2, xP + 2, yP + 2);

      g.setColor(currentColor);
    }

    @Override
    public int getIconWidth() {
      int iconWidth = ARROW_WIDTH + ARROW_WIDTH_OFFSET;
      if (icon != null) {
        iconWidth += icon.getIconWidth() + SEPARATION_OFFSET;
      }
      return iconWidth;
    }

    @Override
    public int getIconHeight() {
      final int iconHeight = Math.max(ARROW_HEIGHT, ((icon != null) ? icon.getIconHeight() : 0));
      return iconHeight;
    }
  }

  /**
   * Inner class. Action to be performed when the primary button is clicked.
   */
  private final class PrimaryButtonAction extends AbstractAction {

    @Override
    public void actionPerformed(final ActionEvent e) {
      boolean postPopup = true;

      if (DropDownButton.this.defaultPopupOptionEnabled && !DropDownButton.this.isEmpty()) {
        final Component comp = DropDownButton.this.dropDownPopupMenu.getComponent(0);
        if (comp instanceof JMenuItem) {
          postPopup = false;
          ((JMenuItem) comp).doClick();
        }
      }

      if (postPopup) {
        DropDownButton.this.postPopupMenu();
      }
    }
  }

  /**
   * Inner class. Action to be performed when the drop down button is clicked.
   */
  private final class DropDownButtonAction extends AbstractAction {

    @Override
    public void actionPerformed(final ActionEvent e) {
      DropDownButton.this.postPopupMenu();
    }
  }

  /**
   * This inner class watches for insertion/deletion of menu items in the popup menu, and disables
   * the drop down button when the popup menu becomes empty.
   */
  private final class PopupMenuContainerListener implements ContainerListener {

    @Override
    public void componentAdded(final ContainerEvent e) {
      DropDownButton.this.setEnabled(true);
      DropDownButton.this.setupButtonTooltipText();
    }

    @Override
    public void componentRemoved(final ContainerEvent e) {
      DropDownButton.this.setEnabled(!DropDownButton.this.isEmpty());
      DropDownButton.this.setupButtonTooltipText();
    }
  }

  /**
   * This inner class is used to apply roll over/armed/pressed states for the drop down button as
   * taken from the primary button.
   */
  private final class PrimaryButtonChangeListener implements ChangeListener {

    @Override
    public void stateChanged(final ChangeEvent e) {
      // Need to apply state changes of the Primary button across to
      // the Drop Down button.
      final ButtonModel pButtonModel = DropDownButton.this.primaryButton.getModel();
      final ButtonModel dButtonModel = DropDownButton.this.dropDownButton.getModel();
      dButtonModel.setEnabled(pButtonModel.isEnabled());
      dButtonModel.setRollover(pButtonModel.isRollover());
      dButtonModel.setArmed(pButtonModel.isArmed());
      dButtonModel.setPressed(pButtonModel.isPressed());
      dButtonModel.setSelected(pButtonModel.isSelected());
    }
  }

  /**
   * This inner class is used to apply roll over state for the primary button.
   */
  private final class DropDownButtonRollOverListener extends MouseAdapter {

    @Override
    public void mouseEntered(final MouseEvent e) {
      DropDownButton.this.primaryButton.getModel().setRollover(true);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
      DropDownButton.this.primaryButton.getModel().setRollover(false);
    }
  }
}
