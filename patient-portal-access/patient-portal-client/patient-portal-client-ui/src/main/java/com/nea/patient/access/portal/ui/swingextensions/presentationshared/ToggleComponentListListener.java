package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

/**
 * This class is used in conjunction with {@code JList} when it is required to display a list of
 * {@code JCheckBox} or {@code JRadioButton} entries by using renderers
 * {@code CheckBoxListCellRenderer} or {@code RadioButtonListCellRenderer} respectively.<br>
 * This class will allow an entry in the {@code JList} component to be selected either through a
 * mouse click event or key press event by attaching itself as a mouse and key listener to the
 * supplied {@code JList} component.<br>
 * This class expects each entry inserted into the {@code JList} model to be an {@code Object}
 * instance which implements interface {@code IListToggleComponent}.<br>
 * This class assumes the {@code JList} component has been initialised to support only a single
 * selection model.
 */
public final class ToggleComponentListListener {

  private JList list;

  /**
   * On creation this class attaches itself to the supplied {@code JList} component to service both
   * mouse and key events to allow a {@code JCheckBox} or {@code JRadioButton} as displayed in the
   * list component to be selected or de-selected.
   *
   * @param listComponent list component on which the mouse and key listener is to be attached in
   *        order to service list selection events.
   */
  public ToggleComponentListListener(final JList listComponent) {
    super();
    list = listComponent;
    if (list != null) {
      list.addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(final MouseEvent e) {
          ToggleComponentListListener.this.doCheck();
        }
      });

      list.addKeyListener(new KeyAdapter() {

        @Override
        public void keyPressed(final KeyEvent e) {
          // Check if a space character was pressed (i.e. toggle selection).
          if (e.getKeyChar() == ' ') {
            ToggleComponentListListener.this.doCheck();
          }
        }
      });
    }
  }

  private void doCheck() {
    final int index = list.getSelectedIndex();
    if (index < 0) {
      return;
    }
    final Object obj = list.getModel().getElementAt(index);
    if (obj instanceof IListToggleComponent) {
      final IListToggleComponent data = (IListToggleComponent) obj;
      if (data.isToggleComponentEnabled()) {
        data.invertToggleComponentSelection();
        list.repaint();
      }
    }
  }
}
