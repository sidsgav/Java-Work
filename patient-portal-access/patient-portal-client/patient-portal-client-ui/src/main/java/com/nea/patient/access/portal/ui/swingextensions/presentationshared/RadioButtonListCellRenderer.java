package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * This class can be used to allow {@code JList} component to render a list of {@code JRadioButton}.
 * This class is used by classes {@code FilterTableEntriesDialog} and
 * {@code FindTableEntriesDialog}. This class expects the parameter {@code value} as supplied in
 * method {@code getListCellRendererComponent} to implement interface {@code IListToggleComponent}
 * to allow the state of the {@code JRadioButton} to be displayed.
 */
@SuppressWarnings("serial")
public final class RadioButtonListCellRenderer extends JRadioButton implements ListCellRenderer {

  private final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

  private boolean showListEntrySelection = true;

  /**
   * Constructor.
   * 
   * @param showListSelection if {@code false} then the normal {@code JList} entry selection
   *        decoration is not shown.
   */
  public RadioButtonListCellRenderer(final boolean showListSelection) {
    super();
    showListEntrySelection = showListSelection;
    setOpaque(true);
    setBorder(noFocusBorder);
  }

  /**
   * Implementation method of interface {@code ListCellRenderer}.
   * 
   * @param list the list we're painting.
   * @param value returned by {@code list.getModel().getElementAt(index)}. The {@code Object}
   *        instance must implement interface {@code IListToggleComponent}.
   * @param index the cells index.
   * @param isSelected {@code true} if the specified cell was selected.
   * @param cellHasFocus {@code true} if the specified cell has the focus.
   * @return a component whose {@code paint()} method will render the specified value, in this case
   *         the component to return is {@code JRadioButton}.
   */
  @Override
  public Component getListCellRendererComponent(final JList list, final Object value, final int index,
      final boolean isSelected,
      final boolean cellHasFocus) {
    setFont(list.getFont());
    setText((value == null) ? "" : value.toString());

    if (showListEntrySelection) {
      setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
      setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
    } else {
      // Set the list selection to default, i.e. do not show a highlighted selection.
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    if (value instanceof IListToggleComponent) {
      final IListToggleComponent data = (IListToggleComponent) value;
      setSelected(data.isToggleComponentSelected());
      setEnabled(data.isToggleComponentEnabled());
    }
    setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

    return this;
  }
}
