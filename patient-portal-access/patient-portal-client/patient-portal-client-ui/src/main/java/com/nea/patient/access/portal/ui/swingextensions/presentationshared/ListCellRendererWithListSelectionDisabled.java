package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * This class can be added to a {@code JList}'s cell renderer and allows the {@code JList} to
 * display a list selection, but when the user makes a list selection, it is not shown as
 * highlighted, thus giving the impression that the list's selection model is disabled. This class
 * is required because there is no means in JDK1.3 to turn off the list selection model.
 */
@SuppressWarnings("serial")
public final class ListCellRendererWithListSelectionDisabled extends JLabel
    implements ListCellRenderer {

  /**
   * {@inheritDoc}
   */
  @Override
  public Component getListCellRendererComponent(final JList list, final Object value,
      final int index, final boolean isSelected, final boolean cellHasFocus) {
    setFont(list.getFont());
    setText((value == null) ? "" : value.toString());

    // Set the list selection to default, i.e. do not show a
    // highlighted selection.
    setForeground(list.getForeground());
    setBackground(list.getBackground());

    return this;
  }
}
