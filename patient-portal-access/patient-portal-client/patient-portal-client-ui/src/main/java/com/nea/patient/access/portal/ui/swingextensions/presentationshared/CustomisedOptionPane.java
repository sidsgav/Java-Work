package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * This class is written for purely one reason and that is so that the presentation of a
 * {@code Dialog} (when created through {@code JOptionPane}) is consistent across the different Look
 * And Feel (L&F) settings. Specifically the presentation of a {@code Dialog} when viewing the
 * application in Motif L&F is completely different in button layout irrespective of whether we
 * override the default resources for OptionPane through {@code UIManager#put} calls. Therefore by
 * sub-classing {@code JOptionPane} and overriding method {@code update} we can default the L&F for
 * Motif back to the Basic L&F. This class should be instantiated in place of directly instantiating
 * {@code JOptionPane}.
 */
@SuppressWarnings("serial")
public final class CustomisedOptionPane extends JOptionPane {

  /**
   * Create a {@code JOptionPane} with a test message.
   */
  public CustomisedOptionPane() {
    super();
  }

  /**
   * Create a instance of {@code JOptionPane} to display a message using the plain-message message
   * type and the default options delivered by the UI.
   *
   * @param message to display.
   */
  public CustomisedOptionPane(final Object message) {
    super(message);
  }

  /**
   * Create an instance of {@code JOptionPane} to display a message with the specified message type
   * and the default options.
   *
   * @param message to display.
   * @param messageType the type of message to be displayed: {@code ERROR_MESSAGE},
   *        {@code INFORMATION_MESSAGE}, {@code WARNING_MESSAGE}, {@code QUESTION_MESSAGE}, or
   *        {@code PLAIN_MESSAGE}.
   */
  public CustomisedOptionPane(final Object message, final int messageType) {
    super(message, messageType);
  }

  /**
   * Create an instance of {@code JOptionPane} to display a message with the specified message type
   * and options.
   *
   * @param message to display.
   * @param messageType the type of message to be displayed: {@code ERROR_MESSAGE},
   *        {@code INFORMATION_MESSAGE}, {@code WARNING_MESSAGE}, {@code QUESTION_MESSAGE}, or
   *        {@code PLAIN_MESSAGE}.
   * @param optionType the options to display in the pane: {@code DEFAULT_OPTION},
   *        {@code YES_NO_OPTION}, {@code YES_NO_CANCEL_OPTION}, {@code OK_CANCEL_OPTION}.
   */
  public CustomisedOptionPane(final Object message, final int messageType, final int optionType) {
    super(message, messageType, optionType);
  }

  /**
   * Create an instance of {@code JOptionPane} to display a message with the specified message type,
   * options, and icon.
   *
   * @param message to display.
   * @param messageType the type of message to be displayed: {@code ERROR_MESSAGE},
   *        {@code INFORMATION_MESSAGE}, {@code WARNING_MESSAGE}, {@code QUESTION_MESSAGE}, or
   *        {@code PLAIN_MESSAGE}.
   * @param optionType the options to display in the pane: {@code DEFAULT_OPTION},
   *        {@code YES_NO_OPTION}, {@code YES_NO_CANCEL_OPTION}, {@code OK_CANCEL_OPTION}.
   * @param icon image to display.
   */
  public CustomisedOptionPane(final Object message, final int messageType, final int optionType, final Icon icon) {
    super(message, messageType, optionType, icon);
  }

  /**
   * Create an instance of {@code JOptionPane} to display a message with the specified message type,
   * icon, and options. None of the options is initially selected.
   * <p>
   * The options objects should contain either instances of {@code Component}s, (which are added
   * directly) or {@code Strings} (which are wrapped in a {@code JButton}). If you provide
   * {@code Component}s, you must ensure that when the {@code Component} is clicked it messages
   * {@code setValue} in the created {@code JOptionPane}.
   * </p>
   *
   * @param message to display.
   * @param messageType the type of message to be displayed: {@code ERROR_MESSAGE},
   *        {@code INFORMATION_MESSAGE}, {@code WARNING_MESSAGE}, {@code QUESTION_MESSAGE}, or
   *        {@code PLAIN_MESSAGE}.
   * @param optionType the options to display in the pane: {@code DEFAULT_OPTION},
   *        {@code YES_NO_OPTION}, {@code YES_NO_CANCEL_OPTION}, {@code OK_CANCEL_OPTION}.
   * @param icon image to display.
   * @param options the choices the user can select.
   */
  public CustomisedOptionPane(final Object message, final int messageType, final int optionType, final Icon icon,
      final Object[] options) {
    super(message, messageType, optionType, icon, options);
  }

  /**
   * Create an instance of {@code JOptionPane} to display a message with the specified message type,
   * icon, and options, with the initially-selected option specified.
   *
   * @param message to display.
   * @param messageType the type of message to be displayed: {@code ERROR_MESSAGE},
   *        {@code INFORMATION_MESSAGE}, {@code WARNING_MESSAGE}, {@code QUESTION_MESSAGE}, or
   *        {@code PLAIN_MESSAGE}.
   * @param optionType the options to display in the pane: {@code DEFAULT_OPTION},
   *        {@code YES_NO_OPTION}, {@code YES_NO_CANCEL_OPTION}, {@code OK_CANCEL_OPTION}.
   * @param icon image to display.
   * @param options the choices the user can select.
   * @param initialValue the choice that is initially selected; if {@code null}, then nothing will
   *        be initially selected; only meaningful if {@code options} is used.
   */
  public CustomisedOptionPane(final Object message, final int messageType, final int optionType, final Icon icon,
      final Object[] options, final Object initialValue) {
    super(message, messageType, optionType, icon, options, initialValue);
  }
}
