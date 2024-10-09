package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * An abstract class which extends {@code AbstractAction}, which provides a means for the UI
 * application to implement User Profile filtering of menu bar/tool bar/pop-up menu options.<br>
 * Need to subclass this abstract class and provide implementation for {@code actionPerformed}
 * method. In addition, the subclass needs to provide the {@code Action} permitted state which
 * indicates to this class whether the {@code Action} should be permanately disabled or can be
 * configured to be enabled/disabled on application conditions.<br>
 * This class should only be subclassed where User Profiling is required and a single {@code Action}
 * serves one or more menu option (i.e. menu bar/tool bar/popup) or button. Therefore a single
 * instance of the subclass is instantiated and from it the required menu bar/tool bar/popup menu
 * options are derived.
 */
@SuppressWarnings("serial")
public abstract class FilteredAction extends AbstractAction {

  private boolean actionPermitted = false;

  /**
   * Define an {@code Action} object with a default description string and default icon.
   *
   * @param isActionPermitted {@code true} if action is permitted, {@code false} otherwise.
   */
  public FilteredAction(final boolean isActionPermitted) {
    super();
    actionPermitted = isActionPermitted;
    setDefaultActionPermission();
  }

  /**
   * Define an {@code Action} object with the specified description string and a default icon.
   *
   * @param actionName action description string used for menu bar/popup menu option text or button
   *        text.
   * @param isActionPermitted {@code true} if action is permitted, {@code false} otherwise.
   */
  public FilteredAction(final String actionName, final boolean isActionPermitted) {
    super(actionName);
    actionPermitted = isActionPermitted;
    setDefaultActionPermission();
  }

  /**
   * Define an {@code Action} object with the specified description string and the specified icon.
   *
   * @param actionName action description string used for menu bar/popup menu option text or button
   *        text.
   * @param actionIcon image displayed on menu bar/popup menu option or button.
   * @param isActionPermitted {@code true} if action is permitted, {@code false} otherwise.
   */
  public FilteredAction(final String actionName, final Icon actionIcon, final boolean isActionPermitted) {
    super(actionName, actionIcon);
    actionPermitted = isActionPermitted;
    setDefaultActionPermission();
  }

  private void setDefaultActionPermission() {
    // By default the Action is enabled on instantiation in super class
    // {@code AbstractAction}, therefore set enabled state to that stored.
    super.setEnabled(actionPermitted);
  }

  /**
   * Provide implementation for method in subclass.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public abstract void actionPerformed(ActionEvent ae);

  /**
   * Set the action name (i.e. description string).
   *
   * @param actionName the action name to set.
   */
  public final void setActionName(final String actionName) {
    putValue(Action.NAME, actionName);
  }

  /**
   * Get the action name (i.e. description string).
   *
   * @return action description string or {@code null} if not set.
   */
  public final String getActionName() {
    final Object obj = getValue(Action.NAME);
    if ((obj != null) && (obj instanceof String)) {
      return (String) obj;
    }
    return null;
  }

  /**
   * Set the action {@code Icon}.
   *
   * @param icon new icon to set.
   */
  public final void setActionIcon(final Icon icon) {
    putValue(Action.SMALL_ICON, icon);
  }

  /**
   * Get the action {@code Icon}. This method could return {@code null}, if not it will be a class
   * that implements the {@code Icon} interface.
   *
   * @return instance which implements the {@code Icon} interface or {@code null}.
   */
  public final Object getActionIcon() {
    return getValue(Action.SMALL_ICON);
  }

  /**
   * Set the action ToolTip text.
   *
   * @param text tooltip to set.
   */
  public final void setActionToolTipText(final String text) {
    putValue(Action.SHORT_DESCRIPTION, text);
  }

  /**
   * Get action ToolTip.
   *
   * @return action tooltip or {@code null} if not set.
   */
  public final String getActionToolTipText() {
    final Object obj = getValue(Action.SHORT_DESCRIPTION);
    if ((obj != null) && (obj instanceof String)) {
      return (String) obj;
    }
    return null;
  }

  /**
   * Overridden method to determine whether the enabling of the {@code Action} is allowed dependant
   * on the stored action permitted state.
   *
   * @param newValue if {@code true} then the action permitted setting is checked before calling the
   *        same method on super class. If {@code false} then the super class method is called
   *        regardless.
   */
  @Override
  public final void setEnabled(final boolean newValue) {
    if (newValue) {
      if (actionPermitted) {
        super.setEnabled(newValue);
      }
    } else {
      super.setEnabled(newValue);
    }
  }

  /**
   * Get the {@code Action} permitted state.
   *
   * @return {@code true} if action is permitted, {@code false} otherwise.
   */
  public final boolean isActionPermitted() {
    return actionPermitted;
  }

  /**
   * Invoke if it's determined this {@code Action}'s permission has been changed at run time after
   * instantiation. The method {@code setEnabled} is called on super class reflecting new action
   * permitted state.
   *
   * @param newValue new action permitted state to set.
   */
  public final void setActionPermitted(final boolean newValue) {
    actionPermitted = newValue;
    super.setEnabled(actionPermitted);
  }
}
