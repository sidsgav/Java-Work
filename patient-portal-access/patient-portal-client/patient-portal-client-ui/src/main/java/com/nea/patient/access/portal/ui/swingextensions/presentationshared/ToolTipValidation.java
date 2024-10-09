package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

/**
 * An interface which can be used by a component that is required to be notified to allow it to
 * validate whether a ToolTip is required to be set on a {@code JComboBox} popup list items. The
 * interested component can then decide whether to return a valid tooltip string to display or
 * return {@code null}. This interface is currently implemented by classes :
 * {@code ConstrainedReadOnlyComboBox}, {@code EditableComboBoxWithTriggerNotify} and
 * {@code TableView}. The method of this interface is currently called by
 * {@code ComboBoxListCellRendererWithToolTipSupport} and uses the result to determine whether to
 * set Tooltip or not on the list component.
 */
public interface ToolTipValidation {

  /**
   * This method requires the implementation class to return whether the Tooltip should be set or
   * not. The implementation class should decide to only return a valid ToolTip if {@code value}
   * contents are partially hidden by the displayed list component. In addition the {@code value}
   * contents (extracted via {@code value.toString()}) can be converted to multi-line Tooltip
   * through method call {@code PresentationUtilities.convertStringToHTMLFormatWithLineBreaks()}.
   *
   * @param value whose contents need to be validated before returning Tooltip, may be supplied as
   *        {@code null}.
   * @param listScrollBarDisplayed {@code true} if the {@code JComboBox} popup list's vertical
   *        scroll bar is displayed, {@code false} otherwise.
   * @return the Tooltip to display or {@code null}.
   */
  String validateToolTipToDisplay(Object value, boolean listScrollBarDisplayed);
}
