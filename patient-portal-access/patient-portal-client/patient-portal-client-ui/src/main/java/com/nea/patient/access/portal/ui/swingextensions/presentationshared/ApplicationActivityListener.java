package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.event.ComponentEvent;

/**
 * An interface which can be implemented by a component that is interested in listening for
 * application activity such as window activity and component moved and resized events.<br>
 * The interface implementation can inspect the trigger event and determine whether to reset any
 * inactivity timeout that may be in operation for the client application.<br>
 * This interface needs to be registered with
 * {@code PresentationUtilities#addApplicationActivityListener} in order to be notified of future
 * events.<br>
 * It's expected that only one implementation of this interface exists and is registered by the
 * client application at startup, although this is not enforced.
 *
 * @see PresentationUtilities#addApplicationActivityListener
 */
public interface ApplicationActivityListener {

  /**
   * Invoked by {@code PresentationUtilities} upon detecting window or component activity.
   *
   * @param evt the triggered event and will in general be either {@code ComponentEvent} itself or a
   *        subclass of it such as {@code WindowEvent}. To determine what the actual trigger event
   *        is the interested listener can call method {@code ComponentEvent#getID}. The event type
   *        returned can be checked against the event masks defined in super class {@code AWTEvent}
   *        and its subclasses.
   */
  void applicationActivityDetected(ComponentEvent evt);
}
