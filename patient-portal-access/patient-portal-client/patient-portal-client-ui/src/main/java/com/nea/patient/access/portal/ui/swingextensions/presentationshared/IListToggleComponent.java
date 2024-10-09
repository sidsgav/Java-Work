package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

/**
 * An interface which is used by the following classes {@code RadioButtonListCellRenderer},
 * {@code CheckBoxListCellRenderer} and {@code ToggleComponentListListener}. When an entry to the
 * {@code JList} is added supporting the aforementioned renderers than the added {@code Object}
 * needs to implement this interface.
 */
public interface IListToggleComponent {

  /**
   * This interface method is called by classes {@code RadioButtonListCellRenderer} and
   * {@code CheckBoxListCellRenderer} from method {@code getListCellRendererComponent} to determine
   * whether the component to be rendered is currently selected.
   *
   * @return {@code true} if the underlying ToggleComponent (i.e. {@code JRadioButton} or
   *         {@code JCheckBox}) is currently selected.
   */
  boolean isToggleComponentSelected();

  /**
   * This interface method is called by classes {@code RadioButtonListCellRenderer} and
   * {@code CheckBoxListCellRenderer} from method {@code getListCellRendererComponent} to determine
   * whether the component to be rendered is currently enabled.
   *
   * @return {@code true} if the underlying ToggleComponent (i.e. {@code JRadioButton} or
   *         {@code JCheckBox}) is currently enabled.
   */
  boolean isToggleComponentEnabled();

  /**
   * This method will be called by class {@code ToggleComponentListListener} once it has detected
   * that a underlying ToggleComponent (i.e. {@code JRadioButton} or {@code JCheckBox} displayed in
   * the {@code JList} has been selected either through a mouse click event or keyboard event. For a
   * {@code JCheckBox} component the selection needs to be inverted. For a {@code JRadioButton} the
   * selection just needs to be made as it can not be inverted.
   */
  void invertToggleComponentSelection();
}
