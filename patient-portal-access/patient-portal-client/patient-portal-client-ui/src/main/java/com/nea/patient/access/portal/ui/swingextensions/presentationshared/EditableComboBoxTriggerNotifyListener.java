package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

/**
 * This class is used in conjunction with {@code EditableComboBoxWithTriggerNotify}. By supplying
 * the component which implements this interface into {@code EditableComboBoxWithTriggerNotify}, it
 * will be notified with calls to the methods of this interface when the required events have
 * occurred.
 */
public interface EditableComboBoxTriggerNotifyListener {
  /**
   * Invoked when characters have been entered into the ComboBox editable component.
   */
  void comboBoxEditingCharactersEntered();

  /**
   * Invoked when no characters exist or have been cleared in the ComboBox editable component.
   */
  void comboBoxEditingFieldEmpty();

  /**
   * This method is called for the following trigger events, which are defined in class
   * {@code EditableComboBoxWithTriggerNotify}:
   * {@code EditableComboBoxWithTriggerNotify#ENTER_DURING_CHARACTER_ENTRY_TRIGGER_EVENT}
   * {@code EditableComboBoxWithTriggerNotify#LIST_SELECTION_TRIGGER_EVENT}
   * {@code EditableComboBoxWithTriggerNotify#SEARCH_BUTTON_TRIGGER_EVENT}
   * {@code EditableComboBoxWithTriggerNotify#FOCUS_LOST_TRIGGER_EVENT}
   * 
   * @param enteredOrSelectedText current contents of the editable ComboBox when trigger is
   *        processed.
   * @param triggerEvent value reflecting which trigger event has been processed.
   */
  void comboBoxEditingComplete(String enteredOrSelectedText, int triggerEvent);
}
