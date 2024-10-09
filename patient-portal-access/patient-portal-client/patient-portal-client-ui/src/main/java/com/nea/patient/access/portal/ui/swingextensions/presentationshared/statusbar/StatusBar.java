package com.nea.patient.access.portal.ui.swingextensions.presentationshared.statusbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * {@code StatusBar} is a specialised {@code JPanel} which provides a single line panel which is
 * intended to be used as a Status Bar across the bottom of a component (normally a {@code JFrame}
 * or similar with a {@code BorderLayout}, where the {@code StatusBar} is added at the south).<br>
 * <br>
 * The {@code StatusBar} allows for Status Bar components to be added, and will render them
 * according to the current UI Look & Feel.<br>
 * <br>
 * As the {@code StatusBar} is a single line, there are restrictions on the size of the components
 * that can be added. At present, only {@code StatusBarButton}s and {@code StatusBarLabel}s are
 * supported.<br>
 * <br>
 * It is possible to add components to the {@code StatusBar} using the {@code JPanel.add} methods,
 * but this can produce unexpected behaviour in the {@code StatusBar}, and as such, these methods
 * should not be used.<br>
 * <br>
 * Each Status Bar component that is added can have it's width defined in pixels, or can be set to
 * use up the remaining space. At least one component should use the remaining space, otherwise the
 * Status Bar may not use the full width of the window. If a component is set to use a large width
 * (e.g.: 2000 pixels), the entire window may resize to cater for this, so care should be taken when
 * sizing components. In practice, the smaller components are sized, and the component which will be
 * the largest size is set to use the remaining space. More than one component can use the remaining
 * space, in which case the remaining space will be shared equally between the components, and they
 * will resize accordingly.<br>
 * <br>
 * The {@code StatusBar} can be resized (as part of the its parent being resized), and attempts to
 * keep the {@code StatusBarComponent}s to a decent size. If the {@code StatusBar} is reduced too
 * much, then some components may be removed to allow for the remaining components to occupy a
 * sensible width. When the {@code StatusBar} is grown, the previously removed components will be
 * added back on, if there is room. For this to work sensibly, each component can be given a
 * priority of either High or Low. Only low priority components will be removed from the
 * {@code StatusBar} when it's shrunk. Components don't need to be given a priority, and if this is
 * the case, they will use their default priorities. Components that are set to have a fixed width
 * will default to low priority, and components which take up the remaining space will have a high
 * priority.
 *
 * @see StatusBarButton
 * @see StatusBarLabel
 */
@SuppressWarnings("serial")
public final class StatusBar extends JPanel {

  /**
   * Use this member instance when indicating that a {@code StatusBarComponent} can use the
   * remaining space on the {@code StatusBar} (i.e. it is not to have a fixed size, but can
   * shrink/expand with the window).
   */
  public static final int REMAINDER = -1;

  /**
   * Used to set the priority of a {@code StatusBarComponent} to High. A component with high
   * priority will not be removed from the {@code StatusBar} when it's shrunk.
   */
  public static final int HIGH_PRIORITY = 1;

  /**
   * Used to set the priority of a {@code StatusBarComponent} to Low. A component with low priority
   * can be removed from the {@code StatusBar} if the width of the {@code StatusBar} is not enough
   * to sustain all the components on it.
   */
  public static final int LOW_PRIORITY = 0;

  // The minimum space that should be left for variable sized components.
  private static final int MIN_REMAINING_SPACE = 150; // pixels

  // The last size of the Status Bar. This is used to determine if the Status
  // Bar is shrinking or growing.
  private Dimension lastSize;

  // The type of layout the StatusBar will use
  private final SpringLayout statusBarLayout = new SpringLayout();

  // A list of StatusBarComponents that are currently visible on the Status Bar.
  private final List<StatusBarObject> statusBarObjects = new ArrayList<StatusBarObject>();

  // A list of StatusBarComponents that have been removed from the Status Bar
  // due to it shrinking.
  private final List<StatusBarObject> removedStatusBarObjects = new ArrayList<StatusBarObject>();

  // Adapter to listen for component resize events.
  private StatusBarComponentAdapter componentAdapter;

  // A rolling count of the position of each component on the Status Bar.
  private int componentPosition = 0;

  // The number of components on this status bar that can be resized.
  private int numberOfVariableSizedComponents = 0;

  /**
   * Create an instance of a {@code StatusBar}.
   */
  public StatusBar() {
    super();
    setLayout(statusBarLayout);
    componentAdapter = new StatusBarComponentAdapter();
    addComponentListener(componentAdapter);
  }

  /**
   * Overridden method of super class. This method has been overridden so that a width of 0 can be
   * returned for the minimum size. If the {@code StatusBar} is being used by a component that forms
   * part of a {@code JSplitPane}, then the minimum size returned will be the width of the
   * {@code StatusBar}, which means that the {@code JSplitPane} can never be reduced in size.
   * Overriding this method solves that problem.
   *
   * @return the minimum size of the {@code StatusBar}. This will have a width of 0, and a height as
   *         defined by the layout manager.
   */
  @Override
  public Dimension getMinimumSize() {
    return new Dimension(0, super.getMinimumSize().height);
  }

  /**
   * Add a component to the {@code StatusBar}. The component must implement
   * {@code StatusBarComponent} such as {@code StatusBarButton} or {@code StatusBarLabel}.
   *
   * @param comp the component to be added.
   * @throws NullPointerException if parameter {@code comp} is supplied as {@code null}.
   *
   * @see StatusBarButton
   * @see StatusBarLabel
   */
  public void addStatusBarComponent(final StatusBarComponent comp) {
    if (comp == null) {
      throw new NullPointerException("Method parameter comp is incorrectly supplied as null.");
    }
    final StatusBarObject sbc = new StatusBarObject(comp, componentPosition);
    statusBarObjects.add(componentPosition, sbc);
    componentPosition++;
  }

  /**
   * Invoke to determine the amount of space that is available for each resizeable Status Bar
   * component. The available space is divided by the number of components that can be resized, and
   * each resizeable component is given a percentage of the available space. This ensures that all
   * the resizeable Status Bar components take up the same amount of space. This method then goes on
   * to give each resizeable component their allocated space.<br>
   *
   * This method does not affect fixed sized components.
   */
  private void sizeVariableComponents() {
    if ((numberOfVariableSizedComponents > 0) && (lastSize != null)) {
      // Determine how much room each variable component can have
      int varSize = 0;
      try {
        // Calculation:
        // (StatusBar width - width of all fixed components)
        // - number of components on the status bar
        // / the number of variable sized components
        // Need to subtract the number of components on the status bar
        // otherwise the right most component may lose some space. This is
        // most noticeable when the right most component is StatusBarButton.
        varSize = (((int) lastSize.getWidth() - getStatusBarFixedComponentsWidth()) -
            statusBarObjects.size()) / numberOfVariableSizedComponents;
      } catch (Exception e) {
        // Divide by zero Exception
        varSize = 0;
      }

      // Step round each Status Bar component and, if it's size is the
      // remainder (resizeable component) give it a percentage of the available
      // space for resizeable components.

      for (StatusBarObject obj : statusBarObjects) {
        final StatusBarComponent sbComp = obj.sbComp;
        final int width = sbComp.getPreferredPixelWidth();
        if (width == REMAINDER) {
          statusBarLayout.getConstraints((Component) sbComp).setWidth(Spring.constant(varSize));
        }
      }
    }
  }

  /**
   * Arrange all the StatusBar components on the {@code StatusBar} and must be called once all the
   * components have been added. This method can be called at any time without harm. If a fixed
   * sized component is to have it's width in pixels changed (due to a Look & Feel change for
   * example), the {@code StatusBarComponent} can be modified, then {@code #arrange} can be called
   * to update this {@code StatusBar}.
   */
  public void arrange() {
    numberOfVariableSizedComponents = 0;

    // Remove all the components from the Status Bar. We can then step through
    // our list of components and add them back on. This ensures that each
    // resizeable component takes up a share of the remaining width. It also
    // allows for this method to be called time and time again (such as when
    // the width of StatusBarComponent is changed by the holding class).
    this.removeAll();

    for (StatusBarObject obj : statusBarObjects) {
      final StatusBarComponent sbComp = obj.sbComp;
      final int width = sbComp.getPreferredPixelWidth();
      if (width != REMAINDER) {
        statusBarLayout.getConstraints((Component) sbComp).setWidth(Spring.constant(width));
      } else {
        numberOfVariableSizedComponents++;
      }
      this.add((Component) sbComp);
    }

    sizeVariableComponents();

    arrangeStatusBarComponents(this, 1, this.getComponentCount(), 0, 0, 1, 1);
    this.validate();
  }

  /**
   * Arrange the components on the {@code StatusBar} so that they are correctly positioned on the
   * {@code SpringLayout} with respect to X, Y coordinates, and sizing. Ensures that each component
   * takes up the same height as well.
   *
   * @param parent the parent that each {@code StatusBarComponent} belongs to, which is the
   *        {@code StatusBar} itself.
   * @param rows the number of rows being displayed on the {@code StatusBar}.
   * @param cols the number of columns on each row.
   * @param initialX the initial X coordinate of the first {@code StatusBarComponent}.
   * @param initialY the initial Y coordinate of the first {@code StatusBarComponent}.
   * @param xPad the padding between each column (component) on a {@code StatusBar} row, specified
   *        in pixels.
   * @param yPad the padding between each row on the {@code StatusBar}, specified in pixels.
   */
  private void arrangeStatusBarComponents(final Container parent, final int rows, final int cols, final int initialX,
      final int initialY, final int xPad, final int yPad) {

    Spring x = Spring.constant(initialX);
    for (int c = 0; c < cols; c++) {
      Spring width = Spring.constant(0);
      for (int r = 0; r < rows; r++) {
        final SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
        constraints.setX(x);
        width = getConstraintsForCell(r, c, parent, cols).getWidth();
      }
      x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    }

    // Align all cells in each row and make them the same height.
    Spring y = Spring.constant(initialY);
    for (int r = 0; r < rows; r++) {
      Spring height = Spring.constant(0);
      for (int c = 0; c < cols; c++) {
        height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
      }
      for (int c = 0; c < cols; c++) {
        final SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
        constraints.setY(y);
        constraints.setHeight(height);
      }
      y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    }

    // Set the parent's size.
    final SpringLayout.Constraints pCons = statusBarLayout.getConstraints(parent);
    pCons.setConstraint(SpringLayout.SOUTH, y);
    pCons.setConstraint(SpringLayout.EAST, x);
  }

  /**
   * Get the constraints for a particular {@code StatusBarComponent}, which is identified as a cell
   * on the {@code SpringLayout} grid.
   *
   * @param row the row to which the cell belongs.
   * @param col the column to which the cell belongs.
   * @param parent the parent of the {@code SpringLayout}.
   * @param cols the maximum number of columns in the {@code SpringLayout}.
   * @return the constraints for the required {@code StatusBarComponent}.
   */
  private SpringLayout.Constraints getConstraintsForCell(final int row, final int col, final Container parent,
      final int cols) {
    final SpringLayout layout = (SpringLayout) parent.getLayout();
    final Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }

  /**
   * Get the width of all the fixed sized components on the {@code StatusBar}.
   *
   * @return the width in pixels of the fixed sized components on the {@code StatusBar}. .
   */
  private int getStatusBarFixedComponentsWidth() {
    int componentsWidth = 0;

    for (StatusBarObject obj : statusBarObjects) {
      final int pixels = obj.sbComp.getPreferredPixelWidth();
      if (pixels != REMAINDER) {
        componentsWidth += pixels;
      }
    }
    return componentsWidth;
  }

  /**
   * Get the next lowest priority component on the {@code StatusBar}, working from the RHS.
   *
   * @return the next {@code StatusBarObject} with a low priority, or {@code null} if a low priority
   *         component cannot be found.
   */
  private StatusBarObject findNextLowestPriorityComponent() {
    for (int i = (statusBarObjects.size() - 1); i >= 0; i--) {
      final StatusBarObject obj = statusBarObjects.get(i);
      if (obj.priority == LOW_PRIORITY) {
        return obj;
      }
    }
    // No low priority component found.
    return null;
  }

  /**
   * This method deals with a shrinking {@code StatusBar} and works out how and when components
   * should be removed from the {@code StatusBar}.
   *
   * @param newSize the new size of the {@code StatusBar}.
   */
  private void statusBarShrinking(final Dimension newSize) {
    // The StatusBar is getting smaller, so we need to remove a component.
    // To do this, we will start looking from the RHS of the Status Bar
    // and find the first component with a low priority and remove this.

    int remainderSpace = ((int) newSize.getWidth() - getStatusBarFixedComponentsWidth());

    while (remainderSpace < MIN_REMAINING_SPACE) {
      final StatusBarObject lowPriortyComp = findNextLowestPriorityComponent();
      if (lowPriortyComp != null) {
        if (lowPriortyComp.sbComp.getPreferredPixelWidth() == REMAINDER) {
          if (((int) newSize.getWidth() - remainderSpace) < getStatusBarFixedComponentsWidth()) {
            removedStatusBarObjects.add(lowPriortyComp);
            statusBarObjects.remove(lowPriortyComp);
          } else {
            break;
            // out of while loop - not removing comp as it would leave an
            // untidy status bar. (The fixed size component would not expand,
            // so we would see a raised area to the RHS, which ruins the
            // look)
          }
        } else {
          // Add the low priority component to the removed list.
          removedStatusBarObjects.add(lowPriortyComp);

          // Remove the component from the list of visible status bar objects.
          statusBarObjects.remove(lowPriortyComp);
        }
        remainderSpace = ((int) newSize.getWidth() - getStatusBarFixedComponentsWidth());
      } else {
        break; // No low priority component returned, so nothing being removed.
      }
    }
  }

  /**
   * This method deals with a growing {@code StatusBar} and works out how and when components should
   * be added back onto the {@code StatusBar}.
   *
   * @param newSize the new size of the {@code StatusBar}.
   */
  private void statusBarGrowing(final Dimension newSize) {
    // The Status Bar is getting bigger. We need to see if there are any
    // components that have been removed, and if so, is there enough room to
    // put them back on the Status Bar without compressing things to much.

    if (!removedStatusBarObjects.isEmpty()) {
      int remainderSpace = ((int) newSize.getWidth() - getStatusBarFixedComponentsWidth());

      while ((remainderSpace > MIN_REMAINING_SPACE) && (!removedStatusBarObjects.isEmpty())) {
        // Before adding the component, see what the remaining space will
        // be when the component is added back. If it's going to be < 150,
        // then don't bother adding the component.
        final StatusBarObject comp = removedStatusBarObjects.get(removedStatusBarObjects.size() - 1);
        final int sbCompWidth = comp.sbComp.getPreferredPixelWidth();

        if ((remainderSpace - sbCompWidth) > MIN_REMAINING_SPACE) {
          // Add the last component in the removed list to end of Status Bar
          // contained components list.
          final StatusBarObject compToAdd = removedStatusBarObjects.get(removedStatusBarObjects.size() - 1);

          statusBarObjects.add(compToAdd.sbPosition, compToAdd);

          // Remove the component from the list of removed objects, as it's now
          // been placed back on the Status bar
          removedStatusBarObjects.remove(compToAdd);

          // Work out the new remaining space, as we're going to check again to
          // see if there's enough room to add another component that was
          // previously removed (if there are any)
          remainderSpace = ((int) newSize.getWidth() - getStatusBarFixedComponentsWidth());
        } else {
          break;
          // Not enough space to add a component without making the remainder
          // space too small, and therefore compressing components too much
        }
      }
    }
  }

  /**
   * Determine in which manner the {@code StatusBar}s size has changed (shrunk or grown) and invokes
   * the relevant method to deal with the change in size.
   *
   * @param newSize the new size of the {@code StatusBar}.
   */
  private void sizeChanged(final Dimension newSize) {
    if (lastSize != null) {
      if (newSize.getWidth() < lastSize.getWidth()) {
        statusBarShrinking(newSize);
      } else if (newSize.getWidth() > lastSize.getWidth()) {
        statusBarGrowing(newSize);
      }
      // else No change in size, so ignore event.
    }
    lastSize = newSize;
    arrange();
  }

  /**
   * Inner class which holds details about a {@code StatusBarComponent} on the {@code StatusBar}.
   * Details for each component include the components priority, its position on the
   * {@code StatusBar} and the actual {@code StatusBarComponent} ( i.e. {@code StatusBarButton},
   * {@code StatusBarLabel}, ...) that the component represents.
   */
  private final class StatusBarObject {

    private int priority = StatusBar.LOW_PRIORITY;

    private StatusBarComponent sbComp;

    // The position on the Status Bar for this component.
    private int sbPosition = 0;

    private StatusBarObject(final StatusBarComponent theComp, final int thePos) {
      priority = theComp.getPriority();
      sbComp = theComp;
      sbPosition = thePos;
    }
  }

  /**
   * Specialised {@code ComponentAdapter} which listens for resize events on the {@code StatusBar}
   * and calls the relevant method to deal with it.
   */
  private final class StatusBarComponentAdapter extends ComponentAdapter {

    @Override
    public void componentResized(final ComponentEvent ce) {
      StatusBar.this.sizeChanged(ce.getComponent().getSize());
    }
  }
}
