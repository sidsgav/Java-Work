package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 * This class extends {@code JTabbedPane} and provides the facility to instantiate a Tab with the
 * ability to close the Tab by displaying a close 'X' icon on the tab. The close icon can be
 * displayed in addition to any {@code Icon} image that is required to be displayed. The Tab is
 * closed by clicking the 'X' icon on the tab.
 */
@SuppressWarnings("serial")
public final class TabbedPaneWithCloseTabSupport extends JTabbedPane {

  private Rectangle rv;

  private Color closeIconLightColor;

  private Color closeIconShadowColor;

  /**
   * Create an empty TabbedPane providing Close tab support and with a default tab placement of
   * {@code JTabbedPane.TOP}.
   */
  public TabbedPaneWithCloseTabSupport() {
    super();
    initialise();
  }

  /**
   * Create an empty TabbedPane providing Close tab support and with the specified tab placement of
   * either: {@code JTabbedPane.TOP}, {@code JTabbedPane.BOTTOM}, {@code JTabbedPane.LEFT}, or
   * {@code JTabbedPane.RIGHT}.
   *
   * @param tabPlacement the placement for the tabs relative to the content.
   */
  public TabbedPaneWithCloseTabSupport(final int tabPlacement) {
    super(tabPlacement);
    initialise();
  }

  /**
   * Create an empty TabbedPane providing Close tab support and with the specified tab placement and
   * tab layout policy. Tab placement may be either: {@code JTabbedPane.TOP},
   * {@code JTabbedPane.BOTTOM}, {@code JTabbedPane.LEFT}, or {@code JTabbedPane.RIGHT}. Tab layout
   * policy may be either: {@code JTabbedPane.WRAP_TAB_LAYOUT} or
   * {@code JTabbedPane.SCROLL_TAB_LAYOUT}.
   *
   * @param tabPlacement the placement for the tabs relative to the content.
   * @param tabLayoutPolicy the policy for laying out tabs when all tabs will not fit on one run.
   */
  public TabbedPaneWithCloseTabSupport(final int tabPlacement, final int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);
    initialise();
  }

  private void initialise() {
    rv = new Rectangle();

    closeIconLightColor = UIManager.getColor("Button.light");
    if (closeIconLightColor == null) {
      closeIconLightColor = Color.lightGray;
    }
    closeIconShadowColor = UIManager.getColor("Button.shadow");
    if (closeIconShadowColor == null) {
      closeIconShadowColor = Color.gray;
    }
  }

  /**
   * Overridden method of super class to allow a tab displaying the Close icon to be closed if the
   * close icon is clicked.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  protected void processMouseEvent(final MouseEvent evt) {
    try {
      super.processMouseEvent(evt);
      if ((evt.getID() == MouseEvent.MOUSE_CLICKED) &&
          (evt.getButton() == MouseEvent.BUTTON1)) {
        final int tabNumber = indexAtLocation(evt.getX(), evt.getY());
        if (tabNumber < 0) {
          return;
        }

        final Icon icon = getIconAt(tabNumber);
        if (icon instanceof CloseTabIcon) { // Checks for null also.
          final CloseTabIcon tabIcon = (CloseTabIcon) icon;
          final Rectangle iconBounds = tabIcon.getCrossBounds(rv);
          if (iconBounds.contains(evt.getPoint())) {
            // The tab is to be closed.
            removeTabAt(tabNumber);
          }
        }
      }
    } catch (IndexOutOfBoundsException iex) {
      return;
    }
  }

  /**
   * Invoke to allow a tab to be created with Close icon thus supporting tab closure functionality.
   *
   * @param title the title to be displayed in this tab.
   * @param component the component to be displayed when this tab is clicked.
   */
  public void addTabWithCloseFacility(final String title, final Component component) {
    this.addTabWithCloseFacility(title, component, (Icon) null);
  }

  /**
   * Invoke to allow a tab to be created with Close icon thus supporting tab closure functionality.
   *
   * @param title the title to be displayed in this tab.
   * @param component the component to be displayed when this tab is clicked.
   * @param tip the tooltip to be displayed for this tab.
   */
  public void addTabWithCloseFacility(final String title, final Component component, final String tip) {
    this.addTabWithCloseFacility(title, component, null, tip);
  }

  /**
   * Invoke to allow a tab to be created with Close icon thus supporting tab closure functionality.
   * In addition an {@code icon} is also displayed beside the Close icon.
   *
   * @param title the title to be displayed in this tab.
   * @param component the component to be displayed when this tab is clicked.
   * @param icon the additional icon to be displayed in this tab, this can be {@code null} in which
   *        case no icon is displayed.
   */
  public void addTabWithCloseFacility(final String title, final Component component, final Icon icon) {
    super.addTab(title, new CloseTabIcon(icon), component);
  }

  /**
   * Invoke to allow a tab to be created with Close icon thus supporting tab closure functionality.
   * In addition an {@code icon} is also displayed beside the Close icon.
   *
   * @param title the title to be displayed in this tab.
   * @param component the component to be displayed when this tab is clicked.
   * @param icon the icon to be displayed in this tab, this can be {@code null} in which case no
   *        icon is displayed.
   * @param tip the tooltip to be displayed for this tab.
   */
  public void addTabWithCloseFacility(final String title, final Component component, final Icon icon,
      final String tip) {
    super.addTab(title, new CloseTabIcon(icon), component, tip);
  }

  /**
   * Overridden method of super class to pick up new Look and Feel colours used to decorate the
   * Close icon. This is public as a consequence of the implementation.
   */
  @Override
  public void updateUI() {
    super.updateUI();

    closeIconLightColor = UIManager.getColor("Button.light");
    if (closeIconLightColor == null) {
      closeIconLightColor = Color.lightGray;
    }
    closeIconShadowColor = UIManager.getColor("Button.shadow");
    if (closeIconShadowColor == null) {
      closeIconShadowColor = Color.gray;
    }
  }

  /**
   * Inner class which generates the 'X' icon for the tabs. The constructor accepts an icon which is
   * extra to the 'X' icon but which can be supplied as {@code null}.
   */
  private final class CloseTabIcon implements Icon {

    private int x_pos = 0;

    private int y_pos = 0;

    private final int maxCloseIconWidthHeight = 16;

    private final int closeIconWidthHeight = 12;

    // Declare draw offset for the 'X' icon.
    private final int drawOffset = 2;

    // Declare some offset to draw the 'X' symbol.
    private final int x_offset1 = 3;

    private final int x_offset2 = 4;

    private final int x_offset3 = 9;

    private final int x_offset4 = 8;

    private Icon icon;

    /**
     * Constructor supplying additional Icon to be displayed.
     *
     * @param theIcon additional display icon, could be supplied as {@code null}.
     */
    private CloseTabIcon(final Icon theIcon) {
      super();
      icon = theIcon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
      x_pos = x;
      y_pos = y;

      final Color currentColor = g.getColor();

      // Declare some offsets for drawing the close icon.
      final int x_p = x + drawOffset;
      final int y_p = y + drawOffset;

      // Draw cross border with pushed out 3D button effect.
      g.setColor(TabbedPaneWithCloseTabSupport.this.closeIconLightColor);
      g.drawLine(x_p, y_p, x_p + closeIconWidthHeight, y_p); // horizontal top line
      g.drawLine(x_p, y_p, x_p, y_p + closeIconWidthHeight); // vertical left line
      g.setColor(TabbedPaneWithCloseTabSupport.this.closeIconShadowColor);
      g.drawLine(x_p + 1, y_p + closeIconWidthHeight, x_p + closeIconWidthHeight, y_p + closeIconWidthHeight); // horizontal
                                                                                                               // bottom
                                                                                                               // line
      g.drawLine(x_p + closeIconWidthHeight, y_p + 1, x_p + closeIconWidthHeight, y_p + closeIconWidthHeight); // vertical
                                                                                                               // right
                                                                                                               // line

      // Draw cross, three diagonal lines each way.
      g.setColor(Color.black);
      g.drawLine(x_p + x_offset1, y_p + x_offset1, x_p + x_offset3, y_p + x_offset3);
      g.drawLine(x_p + x_offset1, y_p + x_offset2, x_p + x_offset4, y_p + x_offset3);
      g.drawLine(x_p + x_offset2, y_p + x_offset1, x_p + x_offset3, y_p + x_offset4);
      g.drawLine(x_p + x_offset3, y_p + x_offset1, x_p + x_offset1, y_p + x_offset3);
      g.drawLine(x_p + x_offset3, y_p + x_offset2, x_p + x_offset2, y_p + x_offset3);
      g.drawLine(x_p + x_offset4, y_p + x_offset1, x_p + x_offset1, y_p + x_offset4);

      g.setColor(currentColor);

      // Draw constructor supplied additional icon.
      if (icon != null) {
        icon.paintIcon(c, g, x_p + maxCloseIconWidthHeight, y_p);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIconWidth() {
      int iconWidth = maxCloseIconWidthHeight + (icon != null ? icon.getIconWidth() : 0);
      if (icon != null) {
        iconWidth += drawOffset;
      }
      return iconWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIconHeight() {
      int iconHeight = Math.max(maxCloseIconWidthHeight, ((icon != null) ? icon.getIconHeight() : 0));
      if (icon != null) {
        if (icon.getIconHeight() > maxCloseIconWidthHeight) {
          iconHeight += drawOffset;
        }
      }
      return iconHeight;
    }

    private Rectangle getCrossBounds(final Rectangle retVar) {
      if (retVar == null) {
        return new Rectangle(x_pos, y_pos, maxCloseIconWidthHeight, maxCloseIconWidthHeight);
      }
      retVar.setBounds(x_pos, y_pos, maxCloseIconWidthHeight, maxCloseIconWidthHeight);
      return retVar;
    }
  }
}
