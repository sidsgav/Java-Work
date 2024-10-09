package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * This class can be used to override a {@code JComboBox}'s renderer used to paint the list items
 * and the item selected to allow a Tooltip to be displayed if required on the list items. In
 * addition, this component allows the selected item and popup list entries to be rendered with
 * correct alignment dependent on object's class type being rendered. The alignment rendering rules
 * are:
 * <ul>
 * <li>Numbers right aligned.</li>
 * <li>Text left aligned.</li>
 * <li>Images with text description left aligned.</li>
 * <li>Images without any description center aligned.</li>
 * </ul>
 * Note these rules are only adhered to for an non-editable {@code JComboBox}, if it's editable then
 * by default all items are rendered as strings and thus they are left aligned.<br>
 * This class makes use of interface {@code ToolTipValidation} to determine whether the hovered over
 * list entry should display a ToolTip or not. It is up to the class implementing the
 * {@code ToolTipValidation} interface to determine whether the supplied data should be displayed by
 * either simply returning the supplied data, or returning the data formatted (i.e. for multi-line
 * Tooltip) or returning {@code null}. This allows the interface implementing class to make a
 * decision on whether the data is partially hidden and therefore requires a ToolTip or not. This
 * class is currently used by classes {@code TableView}, {@code ConstrainedReadOnlyComboBox} and
 * {@code EditableComboBoxWithTriggerNotify}.
 */
@SuppressWarnings("serial")
public final class ComboBoxListCellRendererWithToolTipSupport extends JLabel implements ListCellRenderer {

  private final ToolTipValidation toolTipListenerComp;

  private final EmptyBorder emptyBorder = new EmptyBorder(1, 2, 1, 0);

  /**
   * By supplying {@code null} for parameter {@code toolTipListenerComponent} means this renderer
   * will provide no Tooltip support on {@code JComboBox} popup list items.
   *
   * @param toolTipListenerComponent interface implementation which will be called to validate
   *        whether the Tooltip should be displayed. If {@code null} is supplied then no Tooltip
   *        support is offered.
   */
  public ComboBoxListCellRendererWithToolTipSupport(final ToolTipValidation toolTipListenerComponent) {
    toolTipListenerComp = toolTipListenerComponent;
    setOpaque(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Component getListCellRendererComponent(final JList list, final Object value, final int index,
      final boolean isSelected,
      final boolean cellHasFocus) {
    String valueContents = "";
    Icon iconToSet = null;

    // Determine label contents alignment.
    if (value == null) {
      setHorizontalAlignment(SwingConstants.LEADING);
    } else {
      if (value instanceof Icon) {
        boolean imageOnlyAvailable = true;

        if (value instanceof ImageIcon) {
          final ImageIcon imageIcon = (ImageIcon) value;
          final String description = imageIcon.getDescription();
          if ((description != null) && (description.length() > 0)) {
            setHorizontalAlignment(SwingConstants.LEADING);
            valueContents = description;
            imageOnlyAvailable = false;
          }
        }
        if (imageOnlyAvailable) {
          setHorizontalAlignment(SwingConstants.CENTER);
        }
        iconToSet = (Icon) value;
      } else if (!(value instanceof Number)) {
        setHorizontalAlignment(SwingConstants.LEADING);
        valueContents = value.toString();
      } else {
        // We are required to render a number.
        setHorizontalAlignment(SwingConstants.TRAILING);
        valueContents = value.toString();
      }
    }
    setText(valueContents);
    setIcon(iconToSet);
    if (iconToSet != null) {
      setBorder(emptyBorder);
    }
    setFont(list.getFont());

    // Set list background and foreground colours.
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    // Determine whether we need to set ToolTip on list item.
    if (toolTipListenerComp != null) {
      if (index == -1) {
        // Tooltip support only offered on list items therefore set null.
        list.setToolTipText(null);
      } else {
        boolean listScrollBarDisplayed = false;
        // Determine if the displayed list is currently showing the vertical scroll bar.
        final ListModel listModel = list.getModel();
        if (listModel != null) {
          if (listModel.getSize() > list.getVisibleRowCount()) {
            listScrollBarDisplayed = true;
          }
        }
        list.setToolTipText(toolTipListenerComp.validateToolTipToDisplay(value, listScrollBarDisplayed));
      }
    }
    return this;
  }
}
