package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Small utility for scheduling popup of ComboBox drop down selection areas.
 */
public final class ComboPopup implements Runnable, ActionListener {

  // Time is in ms.
  private static final int COMBO_POPUP_DELAY = 200;

  private final JComboBox component;

  private ComboPopup(final JComboBox component) {
    this.component = component;
  }

  /**
   * Schedule popup of a ComboBox for a time when the AWT thread is equipped to do it.
   *
   * @param component the ComboBox to pop.
   */
  public static void schedulePopup(final JComboBox component) {
    final Timer t = new Timer(COMBO_POPUP_DELAY, new ComboPopup(component));
    t.setRepeats(false);
    t.start();
  }

  /**
   * This is public as a consequence of the implementation.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void run() {
    if (component != null) {
      component.showPopup();
    }
  }

  /**
   * This is public as a consequence of the implementation.<br>
   * <br>
   * {@inheritDoc}
   */
  @Override
  public void actionPerformed(final ActionEvent ev) {
    SwingUtilities.invokeLater(this);
  }
}
